package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IRightClickModule;
import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.client.event.MuseIcon;
import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nonnull;


/**
 * Created by Eximius88 on 2/3/14.
 */
public class DimensionalRiftModule extends AbstractPowerModule implements IRightClickModule {
    final int theOverworld = 0;
    final int theNether = -1;
    final int theEnd = 1;


    public DimensionalRiftModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.servoMotor, 2));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.controlCircuit, 1));
        addBasePropertyDouble(ModuleConstants.HEAT_GENERATION, 55);
        addBasePropertyDouble(ModuleConstants.ENERGY_CONSUMPTION, 200000);
        this.defaultTag.setBoolean(MPALIbConstants.TAG_ONLINE, false);
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.TOOL;
    }

    @Override
    public String getDataName() {
        return ModuleConstants.MODULE_DIMENSIONAL_RIFT__DATANAME;
    }

    @Override
    public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (!playerIn.isRiding() && !playerIn.isBeingRidden() && playerIn.isNonBoss() && ((playerIn instanceof EntityPlayerMP))) {
            EntityPlayerMP player = (EntityPlayerMP) playerIn;
            BlockPos coords = playerIn.bedLocation != null ? playerIn.bedLocation : playerIn.world.getSpawnPoint();

            while (!worldIn.isAirBlock(coords) && !worldIn.isAirBlock(coords.up())) {
                coords = coords.up();
            }

            playerIn.changeDimension(0, new CommandTeleporter(coords));
            int energyConsumption = (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStackIn, ModuleConstants.ENERGY_CONSUMPTION);
            int playerEnergy = ElectricItemUtils.getPlayerEnergy(playerIn);
            if (playerEnergy >= energyConsumption) {
                ElectricItemUtils.drainPlayerEnergy(player, getEnergyUsage(itemStackIn));
                HeatUtils.heatPlayer(player, ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStackIn, ModuleConstants.HEAT_GENERATION));
                return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
            }
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
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
    }

    @Override
    public int getEnergyUsage(@Nonnull ItemStack itemStack) {
        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, ModuleConstants.ENERGY_CONSUMPTION);
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.dimRiftGen;
    }

    // Copied from Forge.
    private static class CommandTeleporter implements ITeleporter
    {
        private final BlockPos targetPos;

        private CommandTeleporter(BlockPos targetPos)
        {
            this.targetPos = targetPos;
        }

        @Override
        public void placeEntity(World world, Entity entity, float yaw)
        {
            entity.moveToBlockPosAndAngles(targetPos, yaw, entity.rotationPitch);
        }
    }
}
