package com.github.lehjr.modularpowerarmor.item.module.environmental;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.RegistryNames;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.IItemStackUpdate;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.client.gui.hud.meters.FluidMeter;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import com.github.lehjr.mpalib.string.StringUtils;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class CoolingSystemBase extends AbstractPowerModule {
    public CoolingSystemBase(String regName) {
        super(regName);
    }

    public class CapProvider implements ICapabilityProvider {
        public ItemStack module;
        public IPlayerTickModule ticker;
        public IFluidHandlerItem fluidHandler;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
                return true;
            }
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return true;
            }
            return false;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
                ((IItemStackUpdate) fluidHandler).updateFromNBT();
                return (T) fluidHandler;
            }

            if (capability == PowerModuleCapability.POWER_MODULE) {
                ticker.updateFromNBT();
                return (T) ticker;
            }

            return null;
        }

        public class ModuleTank extends FluidTank implements IItemStackUpdate, IFluidHandler, IFluidHandlerItem, INBTSerializable<NBTTagCompound> {
            public ModuleTank(int tankSize) {
                super(tankSize);
            }

            /**

             ( Some notes on how this evolved )

             Bonuses
             ------------------------------------

             volumeEfficiency = volume/maxVolume
             temperatureEfficiency = boilingPointOfWater - fluidTemperatureInCelsius

             ( volume/maxVolume ) * (boilingPointOfWater - fluidTemperatureInCelsius)

             T(K) = T(°C) + 273.15


             fluid has these attributes

             viscosity ( bonus for viscosity lower than water )
             density
             temperature
             amount
             doesVaporize

             mass * volumeEfficiency/ (joulesIn + fluidTemp)

             temperature 300  / ? = 750

             assume effiency of 75% for water cooling with full tank

             Heat = mass of object × change in temperature × specific heat capacity of material

             ideally, specific heat would be the best way to go, however, fluid temps are static..

             (baseCooling - fluidTemp) / tankEmptyness

             fluidTemperature( Kelvin ) * fluidAmount

             (fl * fluidAmount)

             the emptier the tank, the lowere the efficiency
             the hotter the fluid, the lower the efficiency
             the ...

             */

            public double getCoolingEfficiency() {
                // closer to full greater heat transfer efficiency
                double volumeEfficiency = getFluidAmount() / getCapacity();

                int boilingPointOfWater = 100;
                int fluidTemperatureInCelsius = fluid.getFluid().getTemperature() - 273; // should be - 273.15 but we're not working with much precision here.

                int temperatureEfficiency = boilingPointOfWater - fluidTemperatureInCelsius;


                // TODO: viscosity bonus
                // so far water efficiency = cooling efficiency: 73.0

                return temperatureEfficiency * volumeEfficiency;
            }

            public List<String> getFluidDisplayString() {
                List<String> currentTipList = new LinkedList<>();
                if (getCapacity() > 0 && getFluidAmount() > 0) {
                    String fluidInfo = I18n.format(getFluid().getLocalizedName()) + " " + StringUtils.formatNumberShort(getFluidAmount()) + '/'
                            + StringUtils.formatNumberShort(getCapacity());

                    currentTipList.add(StringUtils.wrapMultipleFormatTags(fluidInfo, StringUtils.FormatCodes.Italic.character,
                            StringUtils.FormatCodes.Indigo));

                    fluidInfo = StringUtils.formatNumberFromUnits(getFluid().getFluid().getTemperature() - 273.15D, "°C");

                    currentTipList.add(StringUtils.wrapMultipleFormatTags(fluidInfo, StringUtils.FormatCodes.Italic.character,
                            StringUtils.FormatCodes.Indigo));
                }
                return currentTipList;
            }

            public double getHeat() {
                return getFluidMass() * getFluidTemperature();
            }

            public int getViscosity() {
                FluidStack fluid = getFluid();

                if (fluid != null) {
                    return fluid.getFluid().getViscosity();
                }
                return 0;
            }

            /*
             * This will probably make the assumption that 1 bucket = 1 liter or whatever Minecraft math applies
             * returns the amount in
             */
            public double getFluidMass() {
                // fluidVolumeInBuckets * density
                return fluid.amount / 1000 * fluid.getFluid().getDensity();
            }

            public int getFluidTemperature() {
                int fluidTemp = 300;
                FluidStack fluid = getFluid();
                fluidTemp = fluid != null ? fluid.getFluid().getTemperature(fluid) : fluidTemp;
                return fluidTemp - 273; // convert to celsius
            }

            @Nonnull
            @Override
            public ItemStack getContainer() {
                return module;
            }

            @Override
            public void updateFromNBT() {
                NBTTagCompound moduleTag = NBTUtils.getMuseItemTag(module);
                if (moduleTag != null && moduleTag.hasKey(MPALIbConstants.FLUID_NBT_KEY, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND)) {
                    deserializeNBT(moduleTag.getCompoundTag(MPALIbConstants.FLUID_NBT_KEY));
                }
            }

            @Override
            public NBTTagCompound serializeNBT() {
                return this.writeToNBT(new NBTTagCompound());
            }

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
                this.setFluid(FluidStack.loadFluidStackFromNBT(nbt));
            }

            @Override
            public boolean canDrainFluidType(@Nullable FluidStack fluid) {
                return true;
            }

            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return false;
            }

            @Override
            protected void onContentsChanged() {
                NBTTagCompound moduleTag = NBTUtils.getMuseModuleTag(module);
                if (moduleTag != null) {
                    NBTTagCompound fluidTag = this.writeToNBT(new NBTTagCompound());
                    moduleTag.setTag(MPALIbConstants.FLUID_NBT_KEY, fluidTag);
                    deserializeNBT(fluidTag);
                }
            }
        }

        public class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config, boolean defBool) {
                super(module, category, target, config, defBool);
            }

            @Override
            public void onPlayerTickActive(EntityPlayer player, ItemStack itemStack) {
                if (!player.world.isRemote) {
                    double currentHeat = HeatUtils.getPlayerHeat(player);
                    if (currentHeat <= 0)
                        return;

                    double maxHeat = HeatUtils.getPlayerMaxHeat(player);
                    double fluidEfficiencyBoost = ((ModuleTank)fluidHandler).getCoolingEfficiency();

                    // if not overheating
                    if (currentHeat < maxHeat) {
                        double coolJoules = (fluidEfficiencyBoost + getCoolingBonus()) * getCoolingFactor();
                        if (ElectricItemUtils.getPlayerEnergy(player) > coolJoules) {
                            coolJoules = HeatUtils.coolPlayer(player, coolJoules);

                            ElectricItemUtils.drainPlayerEnergy(player,
                                    (int) (coolJoules * getEnergyConsumption()));
                        }

                        // sacrificial emergency cooling
                    } else {
                        // how much player is overheating
                        double overheatAmount = currentHeat - maxHeat;

                        int fluidLevel = ((ModuleTank) fluidHandler).getFluidAmount();

                        boolean usedEmergencyCooling = false;
                        // if system has enough fluid using this "very special" formula
                        if (fluidLevel >= (int) (fluidEfficiencyBoost * overheatAmount)) {
                            fluidHandler.drain((int) (fluidEfficiencyBoost * overheatAmount), true);
                            HeatUtils.coolPlayer(player, overheatAmount + 1);
                            usedEmergencyCooling = true;

                            // sacrifice whatever fluid is in the system
                        } else if (fluidLevel > 0) {
                            fluidHandler.drain(fluidLevel, true);
                            HeatUtils.coolPlayer(player, fluidEfficiencyBoost * fluidLevel);
                            usedEmergencyCooling = true;
                        }

                        if (usedEmergencyCooling)
                            for (int i = 0; i < 4; i++) {
                                player.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, player.posX, player.posY + 0.5, player.posZ, 0.0D, 0.0D, 0.0D);
                            }
                    }
                }
            }

            public double getCoolingBonus() {
                return applyPropertyModifiers(Constants.COOLING_BONUS);
            }

            public double getCoolingFactor() {
                return applyPropertyModifiers(Constants.COOLING_FACTOR);
            }

            public int getEnergyConsumption() {
                return (int) applyPropertyModifiers(Constants.ENERGY_CONSUMPTION);
            }
        }
    }
}
