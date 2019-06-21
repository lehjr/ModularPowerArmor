package net.machinemuse.powersuits.item.module.vision;

import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.tickable.IModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTickCapability;
import net.machinemuse.numina.capabilities.module.toggleable.IModuleToggle;
import net.machinemuse.numina.capabilities.module.toggleable.Toggle;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemModuleNightVision extends AbstractPowerModule {
    static final int powerDrain = 50;
    private static final Effect nightvision = Effects.NIGHT_VISION;

    public ItemModuleNightVision(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities (ItemStack stack, @Nullable CompoundNBT nbt){
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;
        IModuleTick ticker;
        IModuleToggle toggle;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.CATEGORY_VISION, EnumModuleTarget.HEADONLY, MPSConfig.INSTANCE);
            this.toggle = new Toggle(module);
            this.ticker = new Ticker();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == ModuleTickCapability.TICK)
                return ModuleTickCapability.TICK.orEmpty(cap, LazyOptional.of(() -> ticker));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class Ticker extends ModuleTick {
            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack item) {
                if (player.world.isRemote)
                    return;

                double totalEnergy = ElectricItemUtils.getPlayerEnergy(player);
                EffectInstance nightVisionEffect = player.isPotionActive(nightvision) ? player.getActivePotionEffect(nightvision) : null;

                if (totalEnergy > powerDrain) {
                    if (nightVisionEffect == null || nightVisionEffect.getDuration() < 250 && nightVisionEffect.getAmplifier() == -3) {
                        player.addPotionEffect(new EffectInstance(nightvision, 500, -3, false, false));
                        ElectricItemUtils.drainPlayerEnergy(player, powerDrain);
                    }
                } else
                    onPlayerTickInactive(player, item);
            }

            @Override
            public void onPlayerTickInactive(PlayerEntity player, ItemStack item) {
                EffectInstance nightVisionEffect = null;
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