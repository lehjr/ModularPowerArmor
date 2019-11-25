package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.rightclick.RightClickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlinkDriveModule extends AbstractPowerModule {
    public BlinkDriveModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IRightClickModule rightClickie;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.rightClickie = new RightClickie(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.TOOLONLY, MPAConfig.moduleConfig);
            this.rightClickie.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 10000, "RF");
            this.rightClickie.addBasePropertyDouble(Constants.BLINK_DRIVE_RANGE, 5, "m");
            this.rightClickie.addTradeoffPropertyDouble(Constants.RANGE, Constants.ENERGY_CONSUMPTION, 30000);
            this.rightClickie.addTradeoffPropertyDouble(Constants.RANGE, Constants.BLINK_DRIVE_RANGE, 59);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return (T) rightClickie;
            }
            return null;
        }

        class RightClickie extends RightClickModule {
            public RightClickie(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
                SoundEvent enderman_portal = SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.endermen.teleport"));
                int range = (int) applyPropertyModifiers(Constants.BLINK_DRIVE_RANGE);
                int energyConsumption = getEnergyUsage();
                if (ElectricItemUtils.getPlayerEnergy(playerIn) > energyConsumption) {
                    com.github.lehjr.mpalib.player.PlayerUtils.resetFloatKickTicks(playerIn);
                    int amountDrained = ElectricItemUtils.drainPlayerEnergy(playerIn, energyConsumption);

                    worldIn.playSound(playerIn, playerIn.getPosition(), enderman_portal, SoundCategory.PLAYERS, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));
                    MPALibLogger.logDebug("Range: " + range);
                    RayTraceResult hitRayTrace = rayTrace(playerIn.world, playerIn, true, range);

                    MPALibLogger.logDebug("Hit:" + hitRayTrace);
                    teleportEntity(playerIn, hitRayTrace);
                    worldIn.playSound(playerIn, playerIn.getPosition(), enderman_portal, SoundCategory.PLAYERS, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));

                    MPALibLogger.logDebug("blink drive anount drained: " + amountDrained);
                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
                }
                return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(Constants.ENERGY_CONSUMPTION);
            }
        }

        public void teleportEntity(EntityPlayer entityPlayer, RayTraceResult rayTraceResult) {
            if (rayTraceResult != null && entityPlayer instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) entityPlayer;
                if (player.connection.netManager.isChannelOpen()) {
                    switch (rayTraceResult.typeOfHit) {
                        case ENTITY:
                            player.setPositionAndUpdate(rayTraceResult.hitVec.x, rayTraceResult.hitVec.y, rayTraceResult.hitVec.z);
                            break;
                        case BLOCK:
                            double hitx = rayTraceResult.hitVec.x;
                            double hity = rayTraceResult.hitVec.y;
                            double hitz = rayTraceResult.hitVec.z;
                            switch (rayTraceResult.sideHit) {
                                case DOWN: // Bottom
                                    hity -= 2;
                                    break;
                                case UP: // Top
                                    // hity += 1;
                                    break;
                                case NORTH: // North
                                    hitx -= 0.5;
                                    break;
                                case SOUTH: // South
                                    hitx += 0.5;
                                    break;
                                case WEST: // West
                                    hitz += 0.5;
                                    break;
                                case EAST: // East
                                    hitz -= 0.5;
                                    break;
                            }

                            player.setPositionAndUpdate(hitx, hity, hitz);
                            break;
                        default:
                            break;

                    }
                }
            }
        }
    }
}