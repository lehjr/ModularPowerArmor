package com.github.lehjr.modularpowerarmor.item.module.vision;

import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NightVisionModule extends AbstractPowerModule {
    static final int powerDrain = 50;
    private static final Potion nightvision = MobEffects.NIGHT_VISION;

    public NightVisionModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public static class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.VISION, EnumModuleTarget.HEADONLY, MPAConfig.moduleConfig);
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

        static class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config, true);
            }

            @Override
            public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
                if (player.world.isRemote)
                    return;

                double totalEnergy = ElectricItemUtils.getPlayerEnergy(player);
                PotionEffect nightVisionEffect = player.isPotionActive(nightvision) ? player.getActivePotionEffect(nightvision) : null;

                if (totalEnergy > powerDrain) {
                    if (nightVisionEffect == null || nightVisionEffect.getDuration() < 250 && nightVisionEffect.getAmplifier() == -3) {
                        player.addPotionEffect(new PotionEffect(nightvision, 500, -3, false, false));
                        ElectricItemUtils.drainPlayerEnergy(player, powerDrain);
                    }
                } else
                    onPlayerTickInactive(player, item);
            }

            @Override
            public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
                PotionEffect nightVisionEffect = null;
                if (player.isPotionActive(nightvision)) {
                    nightVisionEffect = player.getActivePotionEffect(nightvision);
                    if (nightVisionEffect.getAmplifier() == -3) {
                        player.removePotionEffect(nightvision);
                    }
                }
            }
        }
    }
}