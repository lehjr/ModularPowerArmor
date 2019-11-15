package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.entity.LuxCapacitorEntity;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.rightclick.RightClickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LuxCapacitorModule extends AbstractPowerModule {
    public LuxCapacitorModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IRightClickModule rightClick;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.rightClick = new RightClickie(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, MPAConfig.moduleConfig);
            this.rightClick.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 1000, "RF");
            this.rightClick.addTradeoffPropertyDouble(Constants.RED, Constants.RED_HUE, 1, "%");
            this.rightClick.addTradeoffPropertyDouble(Constants.GREEN, Constants.GREEN_HUE, 1, "%");
            this.rightClick.addTradeoffPropertyDouble(Constants.BLUE, Constants.BLUE_HUE, 1, "%");
            this.rightClick.addTradeoffPropertyDouble(Constants.ALPHA, Constants.OPACITY, 1, "%");
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == PowerModuleCapability.POWER_MODULE;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return null;
        }

        class RightClickie extends RightClickModule {
            public RightClickie(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
                playerIn.setActiveHand(hand);
                if (!worldIn.isRemote) {
                    double energyConsumption = getEnergyUsage();
                    HeatUtils.heatPlayer(playerIn, energyConsumption / 500);
                    if (ElectricItemUtils.getPlayerEnergy(playerIn) > energyConsumption) {
                        ElectricItemUtils.drainPlayerEnergy(playerIn, (int) energyConsumption);

                        double red = applyPropertyModifiers(Constants.RED_HUE);
                        double green = applyPropertyModifiers(Constants.GREEN_HUE);
                        double blue = applyPropertyModifiers(Constants.BLUE_HUE);
                        double alpha = applyPropertyModifiers(Constants.OPACITY);

                        LuxCapacitorEntity luxCapacitor = new LuxCapacitorEntity(worldIn, playerIn, new Colour(red, green, blue, alpha));
                        worldIn.spawnEntity(luxCapacitor);
                    }
                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
                }
                return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(Constants.ENERGY_CONSUMPTION);
            }
        }
    }
}