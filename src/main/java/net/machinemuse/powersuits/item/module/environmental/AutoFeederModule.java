package net.machinemuse.powersuits.item.module.environmental;

import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.tickable.IModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTickCapability;
import net.machinemuse.numina.capabilities.module.toggleable.IModuleToggle;
import net.machinemuse.numina.capabilities.module.toggleable.Toggle;
import net.machinemuse.numina.capabilities.module.toggleable.ToggleCapability;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.nbt.MuseNBTUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AutoFeederModule extends AbstractPowerModule {
    public static final String TAG_FOOD = "Food";
    public static final String TAG_SATURATION = "Saturation";

    public AutoFeederModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public static double getFoodLevel(@Nonnull ItemStack stack) {
        return MuseNBTUtils.getModuleDoubleOrZero(stack, TAG_FOOD);
    }

    public static void setFoodLevel(@Nonnull ItemStack stack, double d) {
        MuseNBTUtils.setModuleDoubleOrRemove(stack,TAG_FOOD, d);
    }

    public static double getSaturationLevel(@Nonnull ItemStack stack) {
        return MuseNBTUtils.getModuleDoubleOrZero(stack, TAG_SATURATION);
    }

    public static void setSaturationLevel(@Nonnull ItemStack stack, double d) {
        MuseNBTUtils.setModuleDoubleOrRemove(stack, TAG_SATURATION, d);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;
        IModuleToggle toggle;
        IModuleTick ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.ENVIRONMENTAL, EnumModuleTarget.HEADONLY, CommonConfig.moduleConfig);
            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 100);
            this.moduleCap.addBasePropertyDouble(MPSConstants.EATING_EFFICIENCY, 50);
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.EFFICIENCY, MPSConstants.ENERGY_CONSUMPTION, 1000, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.EFFICIENCY, MPSConstants.EATING_EFFICIENCY, 50);
            this.toggle= new Toggle(module);
            this.ticker = new Ticker();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == ModuleTickCapability.TICK)
                return ModuleTickCapability.TICK.orEmpty(cap, LazyOptional.of(() -> ticker));
            if(cap == ToggleCapability.TOGGLEABLE_MODULE)
                return ToggleCapability.TOGGLEABLE_MODULE.orEmpty(cap, LazyOptional.of(() -> toggle));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class Ticker extends ModuleTick {

            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack itemX) {
                double foodLevel = getFoodLevel(module);
                double saturationLevel = getSaturationLevel(module);
                IInventory inv = player.inventory;
                double eatingEnergyConsumption = moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
                double efficiency = moduleCap.applyPropertyModifiers(MPSConstants.EATING_EFFICIENCY);

                FoodStats foodStats = player.getFoodStats();
                int foodNeeded = 20 - foodStats.getFoodLevel();
                double saturationNeeded = 20 - foodStats.getSaturationLevel();

                // this consumes all food in the player's inventory and stores the stats in a buffer
//        if (CommonConfig.moduleConfig.useOldAutoFeeder()) { // FIXME!!!!!
                if (true) {

                    for (int i = 0; i < inv.getSizeInventory(); i++) {
                        ItemStack stack = inv.getStackInSlot(i);
                        if (stack.isFood()) {
                            for (int a = 0; a < stack.getCount(); a++) {
                                foodLevel += stack.getItem().getFood().getHealing() * efficiency / 100.0;
                                //  copied this from FoodStats.addStats()
                                saturationLevel += Math.min(stack.getItem().getFood().getHealing() * stack.getItem().getFood().getSaturation() * 2.0F, 20F) * efficiency / 100.0;
                            }
                            player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                        }
                    }
                    setFoodLevel(module, foodLevel);
                    setSaturationLevel(module, saturationLevel);
                } else {
                    for (int i = 0; i < inv.getSizeInventory(); i++) {
                        if (foodNeeded < foodLevel)
                            break;
                        ItemStack stack = inv.getStackInSlot(i);
                        if (stack.isFood()) {
                            while (true) {
                                if (foodNeeded > foodLevel) {
                                    foodLevel += stack.getItem().getFood().getHealing() * efficiency / 100.0;
                                    //  copied this from FoodStats.addStats()
                                    saturationLevel += Math.min(stack.getItem().getFood().getHealing() * (double) stack.getItem().getFood().getSaturation() * 2.0D, 20D) * efficiency / 100.0;
                                    stack.setCount(stack.getCount() - 1);
                                    if (stack.getCount() == 0) {
                                        player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                                        break;
                                    } else
                                        player.inventory.setInventorySlotContents(i, stack);
                                } else
                                    break;
                            }
                        }
                    }
                    setFoodLevel(module, foodLevel);
                    setSaturationLevel(module, saturationLevel);
                }

                CompoundNBT foodStatNBT = new CompoundNBT();

                // only consume saturation if food is consumed. This keeps the food buffer from overloading with food while the
                //   saturation buffer drains completely.
                if (foodNeeded > 0 && getFoodLevel(module) >= 1) {
                    int foodUsed = 0;
                    // if buffer has enough to fill player stat
                    if (getFoodLevel(module) >= foodNeeded && foodNeeded * eatingEnergyConsumption * 0.5 < ElectricItemUtils.getPlayerEnergy(player)) {
                        foodUsed = foodNeeded;
                        // if buffer has some but not enough to fill the player stat
                    } else if ((foodNeeded - getFoodLevel(module)) > 0 && getFoodLevel(module) * eatingEnergyConsumption * 0.5 < ElectricItemUtils.getPlayerEnergy(player)) {
                        foodUsed = (int) getFoodLevel(module);
                        // last resort where using just 1 unit from buffer
                    } else if (eatingEnergyConsumption * 0.5 < ElectricItemUtils.getPlayerEnergy(player) && getFoodLevel(module) >= 1) {
                        foodUsed = 1;
                    }
                    if (foodUsed > 0) {
                        // populate the tag with the nbt data
                        foodStats.write(foodStatNBT);
                        foodStatNBT.putInt("foodLevel", foodStatNBT.getInt("foodLevel") + foodUsed);
                        // put the values back into foodstats
                        foodStats.read(foodStatNBT);
                        // update getValue stored in buffer
                        setFoodLevel(module, getFoodLevel(module) - foodUsed);
                        // split the cost between using food and using saturation
                        ElectricItemUtils.drainPlayerEnergy(player, (int) (eatingEnergyConsumption * 0.5 * foodUsed));

                        if (saturationNeeded >= 1.0D) {
                            // using int for better precision
                            int saturationUsed = 0;
                            // if buffer has enough to fill player stat
                            if (getSaturationLevel(module) >= saturationNeeded && saturationNeeded * eatingEnergyConsumption * 0.5 < ElectricItemUtils.getPlayerEnergy(player)) {
                                saturationUsed = (int) saturationNeeded;
                                // if buffer has some but not enough to fill the player stat
                            } else if ((saturationNeeded - getSaturationLevel(module)) > 0 && getSaturationLevel(module) * eatingEnergyConsumption * 0.5 < ElectricItemUtils.getPlayerEnergy(player)) {
                                saturationUsed = (int) getSaturationLevel(module);
                                // last resort where using just 1 unit from buffer
                            } else if (eatingEnergyConsumption * 0.5 < ElectricItemUtils.getPlayerEnergy(player) && getSaturationLevel(module) >= 1) {
                                saturationUsed = 1;
                            }

                            if (saturationUsed > 0) {
                                // populate the tag with the nbt data
                                foodStats.write(foodStatNBT);
                                foodStatNBT.putFloat("foodSaturationLevel", foodStatNBT.getFloat("foodSaturationLevel") + saturationUsed);
                                // put the values back into foodstats
                                foodStats.read(foodStatNBT);
                                // update getValue stored in buffer
                                setSaturationLevel(module, getSaturationLevel(module) - saturationUsed);
                                // split the cost between using food and using saturation
                                ElectricItemUtils.drainPlayerEnergy(player, (int) (eatingEnergyConsumption * 0.5 * saturationUsed));
                            }
                        }
                    }
                }
            }
        }
    }
}