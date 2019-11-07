package com.github.lehjr.modularpowerarmor.item.module.environmental;

import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

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
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public static double getFoodLevel(@Nonnull ItemStack stack) {
        return NBTUtils.getModuleDoubleOrZero(stack, TAG_FOOD);
    }

    public static void setFoodLevel(@Nonnull ItemStack stack, double d) {
        NBTUtils.setModuleDoubleOrRemove(stack,TAG_FOOD, d);
    }

    public static double getSaturationLevel(@Nonnull ItemStack stack) {
        return NBTUtils.getModuleDoubleOrZero(stack, TAG_SATURATION);
    }

    public static void setSaturationLevel(@Nonnull ItemStack stack, double d) {
        NBTUtils.setModuleDoubleOrRemove(stack, TAG_SATURATION, d);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.ENVIRONMENTAL, EnumModuleTarget.HEADONLY, MPAConfig.moduleConfig);
            this.ticker.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 100);
            this.ticker.addBasePropertyDouble(Constants.EATING_EFFICIENCY, 50);
            this.ticker.addTradeoffPropertyDouble(Constants.EFFICIENCY, Constants.ENERGY_CONSUMPTION, 1000, "RF");
            this.ticker.addTradeoffPropertyDouble(Constants.EFFICIENCY, Constants.EATING_EFFICIENCY, 50);
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == PowerModuleCapability.POWER_MODULE;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                ticker.updateFromNBT();
                return (T) ticker;
            }
            return null;
        }

        class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config, true);
            }

            @Override
            public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
                double foodLevel = getFoodLevel(item);
                double saturationLevel = getSaturationLevel(item);
                IInventory inv = player.inventory;
                double eatingEnergyConsumption = applyPropertyModifiers(ModuleConstants.EATING_ENERGY_CONSUMPTION);
                double efficiency = applyPropertyModifiers(ModuleConstants.EATING_EFFICIENCY);

                FoodStats foodStats = player.getFoodStats();
                int foodNeeded = 20 - foodStats.getFoodLevel();
                double saturationNeeded = 20 - foodStats.getSaturationLevel();

                // this consumes all food in the player's inventory and stores the stats in a buffer
                if (MPAConfig.INSTANCE.useOldAutoFeeder()) {
                    for (int i = 0; i < inv.getSizeInventory(); i++) {
                        ItemStack stack = inv.getStackInSlot(i);
                        if (!stack.isEmpty() && stack.getItem() instanceof ItemFood) {
                            ItemFood food = (ItemFood) stack.getItem();
                            for (int a = 0; a < stack.getCount(); a++) {
                                foodLevel += food.getHealAmount(stack) * efficiency / 100.0;
                                //  copied this from FoodStats.addStats()
                                saturationLevel += Math.min(food.getHealAmount(stack) * food.getSaturationModifier(stack) * 2.0F, 20F) * efficiency / 100.0;
                            }
                            player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                        }
                    }
                    setFoodLevel(item, foodLevel);
                    setSaturationLevel(item, saturationLevel);
                } else {
                    for (int i = 0; i < inv.getSizeInventory(); i++) {
                        if (foodNeeded < foodLevel)
                            break;
                        ItemStack stack = inv.getStackInSlot(i);
                        if (!stack.isEmpty() && stack.getItem() instanceof ItemFood) {
                            ItemFood food = (ItemFood) stack.getItem();
                            while (true) {
                                if (foodNeeded > foodLevel) {
                                    foodLevel += food.getHealAmount(stack) * efficiency / 100.0;
                                    //  copied this from FoodStats.addStats()
                                    saturationLevel += Math.min(food.getHealAmount(stack) * (double) food.getSaturationModifier(stack) * 2.0D, 20D) * efficiency / 100.0;
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
                    setFoodLevel(item, foodLevel);
                    setSaturationLevel(item, saturationLevel);
                }

                NBTTagCompound foodStatNBT = new NBTTagCompound();

                // only consume saturation if food is consumed. This keeps the food buffer from overloading with food while the
                //   saturation buffer drains completely.
                if (foodNeeded > 0 && getFoodLevel(item) >= 1) {
                    int foodUsed = 0;
                    // if buffer has enough to fill player stat
                    if (getFoodLevel(item) >= foodNeeded && foodNeeded * eatingEnergyConsumption * 0.5 < ElectricItemUtils.getPlayerEnergy(player)) {
                        foodUsed = foodNeeded;
                        // if buffer has some but not enough to fill the player stat
                    } else if ((foodNeeded - getFoodLevel(item)) > 0 && getFoodLevel(item) * eatingEnergyConsumption * 0.5 < ElectricItemUtils.getPlayerEnergy(player)) {
                        foodUsed = (int) getFoodLevel(item);
                        // last resort where using just 1 unit from buffer
                    } else if (eatingEnergyConsumption * 0.5 < ElectricItemUtils.getPlayerEnergy(player) && getFoodLevel(item) >= 1) {
                        foodUsed = 1;
                    }
                    if (foodUsed > 0) {
                        // populate the tag with the nbt data
                        foodStats.writeNBT(foodStatNBT);
                        foodStatNBT.setInteger("foodLevel",
                                foodStatNBT.getInteger("foodLevel") + foodUsed);
                        // put the values back into foodstats
                        foodStats.readNBT(foodStatNBT);
                        // update getValue stored in buffer
                        setFoodLevel(item, getFoodLevel(item) - foodUsed);
                        // split the cost between using food and using saturation
                        ElectricItemUtils.drainPlayerEnergy(player, (int) (eatingEnergyConsumption * 0.5 * foodUsed));

                        if (saturationNeeded >= 1.0D) {
                            // using int for better precision
                            int saturationUsed = 0;
                            // if buffer has enough to fill player stat
                            if (getSaturationLevel(item) >= saturationNeeded && saturationNeeded * eatingEnergyConsumption * 0.5 < ElectricItemUtils.getPlayerEnergy(player)) {
                                saturationUsed = (int) saturationNeeded;
                                // if buffer has some but not enough to fill the player stat
                            } else if ((saturationNeeded - getSaturationLevel(item)) > 0 && getSaturationLevel(item) * eatingEnergyConsumption * 0.5 < ElectricItemUtils.getPlayerEnergy(player)) {
                                saturationUsed = (int) getSaturationLevel(item);
                                // last resort where using just 1 unit from buffer
                            } else if (eatingEnergyConsumption * 0.5 < ElectricItemUtils.getPlayerEnergy(player) && getSaturationLevel(item) >= 1) {
                                saturationUsed = 1;
                            }

                            if (saturationUsed > 0) {
                                // populate the tag with the nbt data
                                foodStats.writeNBT(foodStatNBT);
                                foodStatNBT.setFloat("foodSaturationLevel", foodStatNBT.getFloat("foodSaturationLevel") + saturationUsed);
                                // put the values back into foodstats
                                foodStats.readNBT(foodStatNBT);
                                // update getValue stored in buffer
                                setSaturationLevel(item, getSaturationLevel(item) - saturationUsed);
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