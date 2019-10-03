package net.machinemuse.powersuits.item.module.movement;

import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.capabilities.module.rightclick.IRightClickModule;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickModule;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.player.NuminaPlayerUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlinkDriveModule extends AbstractPowerModule {
    public BlinkDriveModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IRightClickModule rightClickie;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.rightClickie = new RightClickie(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
            this.rightClickie.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 10000, "RF");
            this.rightClickie.addBasePropertyDouble(MPSConstants.BLINK_DRIVE_RANGE, 5, "m");
            this.rightClickie.addTradeoffPropertyDouble(MPSConstants.RANGE, MPSConstants.ENERGY_CONSUMPTION, 30000);
            this.rightClickie.addTradeoffPropertyDouble(MPSConstants.RANGE, MPSConstants.BLINK_DRIVE_RANGE, 59);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> rightClickie));
        }

        class RightClickie extends RightClickModule {
            public RightClickie(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, Hand hand) {
                int range = (int) applyPropertyModifiers(MPSConstants.BLINK_DRIVE_RANGE);
                int energyConsumption = getEnergyUsage();

                RayTraceResult hitRayTrace = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY, range);
                if (hitRayTrace != null && hitRayTrace.getType() == RayTraceResult.Type.BLOCK) {
                    double distance = hitRayTrace.getHitVec().distanceTo(new Vec3d(playerIn.getPosition()));

                    // adjust energy consumption for actual distance.
                    energyConsumption = (int) (energyConsumption * (distance/range));

                    if (ElectricItemUtils.getPlayerEnergy(playerIn) > energyConsumption) {
                        NuminaPlayerUtils.resetFloatKickTicks(playerIn);
                        int amountDrained = ElectricItemUtils.drainPlayerEnergy(playerIn, energyConsumption);
                        worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));
                        NuminaPlayerUtils.teleportEntity(playerIn, hitRayTrace);
                        worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));
                        return ActionResult.newResult(ActionResultType.SUCCESS, itemStackIn);
                    }
                }
                return ActionResult.newResult(ActionResultType.PASS, itemStackIn);
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
            }
        }
    }
}
