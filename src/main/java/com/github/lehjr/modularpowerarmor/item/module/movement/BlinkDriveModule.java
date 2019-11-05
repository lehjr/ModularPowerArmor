package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IRightClickModule;
import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.client.event.MuseIcon;
import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import com.github.lehjr.modularpowerarmor.utils.PlayerUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlinkDriveModule extends AbstractPowerModule implements IRightClickModule {
    public BlinkDriveModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.ionThruster, 1));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.fieldEmitter, 2));

        addBasePropertyDouble(ModuleConstants.BLINK_DRIVE_ENERGY_CONSUMPTION, 10000, "RF");
        addBasePropertyDouble(ModuleConstants.BLINK_DRIVE_RANGE, 5, "m");
        addTradeoffPropertyDouble(ModuleConstants.RANGE, ModuleConstants.BLINK_DRIVE_ENERGY_CONSUMPTION, 30000);
        addTradeoffPropertyDouble(ModuleConstants.RANGE, ModuleConstants.BLINK_DRIVE_RANGE, 59);
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.MOVEMENT;
    }

    @Override
    public String getDataName() {
        return ModuleConstants.MODULE_BLINK_DRIVE__DATANAME;
    }

    @Override
    public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        SoundEvent enderman_portal = SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.endermen.teleport"));
        int range = (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStackIn, ModuleConstants.BLINK_DRIVE_RANGE);
        int energyConsumption = getEnergyUsage(itemStackIn);
        if (ElectricItemUtils.getPlayerEnergy(playerIn) > energyConsumption) {
            com.github.lehjr.mpalib.player.PlayerUtils.resetFloatKickTicks(playerIn);
            int amountDrained = ElectricItemUtils.drainPlayerEnergy(playerIn, energyConsumption);

            worldIn.playSound(playerIn, playerIn.getPosition(), enderman_portal, SoundCategory.PLAYERS, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));
            MPALibLogger.logDebug("Range: " + range);
            RayTraceResult hitRayTrace = PlayerUtils.doCustomRayTrace(playerIn.world, playerIn, true, range);

            MPALibLogger.logDebug("Hit:" + hitRayTrace);
            PlayerUtils.teleportEntity(playerIn, hitRayTrace);
            worldIn.playSound(playerIn, playerIn.getPosition(), enderman_portal, SoundCategory.PLAYERS, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));

            MPALibLogger.logDebug("blink drive anount drained: " + amountDrained);
            return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
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
        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, ModuleConstants.BLINK_DRIVE_ENERGY_CONSUMPTION);
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.blinkDrive;
    }
}
