package net.machinemuse.powersuits.item.module.movement;

import net.machinemuse.numina.basemod.MuseLogger;
import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.rightclick.IRightClickModule;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickCapability;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickModule;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.player.NuminaPlayerUtils;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemModuleBlinkDrive extends AbstractPowerModule {
    public ItemModuleBlinkDrive(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;
        IRightClickModule rightClick;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.CATEGORY_MOVEMENT, EnumModuleTarget.TOOLONLY, MPSConfig.INSTANCE);

            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 10000, "RF");
            this.moduleCap.addBasePropertyDouble(MPSConstants.BLINK_DRIVE_RANGE, 5, "m");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.RANGE, MPSConstants.ENERGY_CONSUMPTION, 30000);
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.RANGE, MPSConstants.BLINK_DRIVE_RANGE, 59);

            this.rightClick = new RightClickie();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == RightClickCapability.RIGHT_CLICK)
                return RightClickCapability.RIGHT_CLICK.orEmpty(cap, LazyOptional.of(() -> rightClick));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class RightClickie extends RightClickModule {
            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, Hand hand) {
                int range = (int) moduleCap.applyPropertyModifiers(MPSConstants.BLINK_DRIVE_RANGE);
                int energyConsumption = getEnergyUsage();
                if (ElectricItemUtils.getPlayerEnergy(playerIn) > energyConsumption) {
                    NuminaPlayerUtils.resetFloatKickTicks(playerIn);
                    int amountDrained = ElectricItemUtils.drainPlayerEnergy(playerIn, energyConsumption);

                    worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));
                    MuseLogger.logDebug("Range: " + range);
                    RayTraceResult hitRayTrace = rayTrace(playerIn.world, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);

                    MuseLogger.logDebug("Hit:" + hitRayTrace);
                    NuminaPlayerUtils.teleportEntity(playerIn, hitRayTrace);
                    worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));

                    MuseLogger.logDebug("blink drive anount drained: " + amountDrained);
                    return ActionResult.newResult(ActionResultType.SUCCESS, itemStackIn);
                }
                return ActionResult.newResult(ActionResultType.PASS, itemStackIn);
            }

            @Override
            public int getEnergyUsage() {
                return (int) moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
            }
        }
    }
}
