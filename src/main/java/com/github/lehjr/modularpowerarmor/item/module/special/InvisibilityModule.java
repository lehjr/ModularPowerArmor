package com.github.lehjr.modularpowerarmor.item.module.special;

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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InvisibilityModule extends AbstractPowerModule {
    private final Potion invisibility = Potion.getPotionFromResourceLocation("invisibility");

    public InvisibilityModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.SPECIAL, EnumModuleTarget.ARMORONLY, MPAConfig.moduleConfig);
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
                super(module, category, target, config, false);
            }

            @Override
            public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
                double totalEnergy = ElectricItemUtils.getPlayerEnergy(player);
                PotionEffect invis = null;
                if (player.isPotionActive(invisibility)) {
                    invis = player.getActivePotionEffect(invisibility);
                }
                if (50 < totalEnergy) {
                    if (invis == null || invis.getDuration() < 210) {
                        player.addPotionEffect(new PotionEffect(invisibility, 500, -3, false, false));
                        ElectricItemUtils.drainPlayerEnergy(player, 50);
                    }
                } else {
                    onPlayerTickInactive(player, item);
                }
            }

            @Override
            public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
                PotionEffect invis = null;
                if (player.isPotionActive(invisibility)) {
                    invis = player.getActivePotionEffect(invisibility);
                }
                if (invis != null && invis.getAmplifier() == -3) {
                    if (player.world.isRemote) {
                        player.removeActivePotionEffect(invisibility);
                    } else {
                        player.removePotionEffect(invisibility);
                    }
                }
            }
        }
    }
}