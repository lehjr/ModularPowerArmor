package com.github.machinemuse.powersuits.powermodule.mining_enhancement;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IBlockBreakingModule;
import com.github.lehjr.mpalib.legacy.module.IMiningEnhancementModule;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MuseIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;

import javax.annotation.Nonnull;


// Note: tried as an enchantment, but failed to function properly due to how block breaking code works
public class AquaAffinityModule extends PowerModuleBase implements IMiningEnhancementModule, IBlockBreakingModule {
    public AquaAffinityModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.servoMotor, 1));
        addBasePropertyDouble(MPSModuleConstants.AQUA_AFFINITY_ENERGY_CONSUMPTION, 0, "RF");
        addBasePropertyDouble(MPSModuleConstants.UNDERWATER_HARVEST_SPEED, 0.2, "%");
        addTradeoffPropertyDouble(MPSModuleConstants.POWER, MPSModuleConstants.AQUA_AFFINITY_ENERGY_CONSUMPTION, 1000);
        addTradeoffPropertyDouble(MPSModuleConstants.POWER, MPSModuleConstants.UNDERWATER_HARVEST_SPEED, 0.8);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        return false;
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.MINING_ENHANCEMENT;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_AQUA_AFFINITY__DATANAME;
    }

    @Override
    public boolean canHarvestBlock(@Nonnull ItemStack stack, IBlockState state, EntityPlayer player, BlockPos pos, int playerEnergy) {
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack itemStack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving, int playerEnergy) {
        if (this.canHarvestBlock(itemStack, state, (EntityPlayer) entityLiving, pos, playerEnergy)) {
            ElectricItemUtils.drainPlayerEnergy((EntityPlayer) entityLiving, getEnergyUsage(itemStack));
            return true;
        }
        return false;
    }

    @Override
    public int getEnergyUsage(@Nonnull ItemStack itemStack) {
        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.AQUA_AFFINITY_ENERGY_CONSUMPTION);
    }

    @Nonnull
    @Override
    public ItemStack getEmulatedTool() {
        return ItemStack.EMPTY;
    }

    @Override
    public void handleBreakSpeed(BreakSpeed event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = player.inventory.getCurrentItem();

        if (event.getNewSpeed() > 1
                && (player.isInsideOfMaterial(Material.WATER) || !player.onGround)
                && ElectricItemUtils.getPlayerEnergy(player) > getEnergyUsage(stack)) {
            event.setNewSpeed((float) (event.getNewSpeed() * 5 * ModuleManager.INSTANCE.getOrSetModularPropertyDouble(stack, MPSModuleConstants.UNDERWATER_HARVEST_SPEED)));
        }
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.aquaAffinity;
    }
}