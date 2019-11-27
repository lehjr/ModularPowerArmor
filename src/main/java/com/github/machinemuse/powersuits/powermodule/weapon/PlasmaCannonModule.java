package com.github.machinemuse.powersuits.powermodule.weapon;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IRightClickModule;
import com.github.lehjr.mpalib.math.MathUtils;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MuseIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.entity.EntityPlasmaBolt;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class PlasmaCannonModule extends PowerModuleBase implements IRightClickModule {
    public PlasmaCannonModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.fieldEmitter, 2));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.hvcapacitor, 2));

        addBasePropertyDouble(MPSModuleConstants.PLASMA_CANNON_ENERGY_PER_TICK, 100, "RF");
        addBasePropertyDouble(MPSModuleConstants.PLASMA_CANNON_DAMAGE_AT_FULL_CHARGE, 2, "pt");
        addTradeoffPropertyDouble(MPSModuleConstants.AMPERAGE, MPSModuleConstants.PLASMA_CANNON_ENERGY_PER_TICK, 1500, "RF");
        addTradeoffPropertyDouble(MPSModuleConstants.AMPERAGE, MPSModuleConstants.PLASMA_CANNON_DAMAGE_AT_FULL_CHARGE, 38, "pt");
        addTradeoffPropertyDouble(MPSModuleConstants.VOLTAGE, MPSModuleConstants.PLASMA_CANNON_ENERGY_PER_TICK, 500, "RF");
        addTradeoffPropertyDouble(MPSModuleConstants.VOLTAGE, MPSModuleConstants.PLASMA_CANNON_EXPLOSIVENESS, 0.5, MPSModuleConstants.CREEPER);
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.WEAPON;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_PLASMA_CANNON__DATANAME;
    }

    @Override
    public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (hand == EnumHand.MAIN_HAND && ElectricItemUtils.getPlayerEnergy(playerIn) > 500) {
            playerIn.setActiveHand(hand);
            return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
        }
        return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.PASS;
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        return EnumActionResult.PASS;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        int chargeTicks = (int) MathUtils.clampDouble(itemStack.getMaxItemUseDuration() - timeLeft, 10, 50);

        if (!worldIn.isRemote) {
            double energyConsumption = getEnergyUsage(itemStack)* chargeTicks;
            if (entityLiving instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entityLiving;
                HeatUtils.heatPlayer(player, energyConsumption / 5000);
                if (ElectricItemUtils.getPlayerEnergy(player) > energyConsumption) {
                    ElectricItemUtils.drainPlayerEnergy(player, (int) energyConsumption);
                    double explosiveness = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.PLASMA_CANNON_EXPLOSIVENESS);
                    double damagingness = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.PLASMA_CANNON_DAMAGE_AT_FULL_CHARGE);

                    EntityPlasmaBolt plasmaBolt = new EntityPlasmaBolt(worldIn, player, explosiveness, damagingness, chargeTicks);
                    worldIn.spawnEntity(plasmaBolt);
                }
            }
        }
    }

    @Override
    public int getEnergyUsage(@Nonnull ItemStack itemStack) {
        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.PLASMA_CANNON_ENERGY_PER_TICK);
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.plasmaCannon;
    }
}
