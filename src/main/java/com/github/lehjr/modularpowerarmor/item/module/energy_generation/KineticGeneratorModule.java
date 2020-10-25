package com.github.lehjr.modularpowerarmor.item.module.energy_generation;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.event.MovementManager;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.util.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.util.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.util.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.util.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.util.energy.ElectricItemUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class KineticGeneratorModule extends AbstractPowerModule {
    static final ResourceLocation sprintAssist = new ResourceLocation(MPAConstants.MOD_ID, MPARegistryNames.SPRINT_ASSIST_MODULE);

    public KineticGeneratorModule() {
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities (ItemStack stack, @Nullable CompoundNBT nbt){
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.ENERGY_GENERATION, EnumModuleTarget.TORSOONLY, MPASettings::getModuleConfig);
            this.ticker.addBaseProperty(MPAConstants.ENERGY_GENERATION, 2000);
            this.ticker.addTradeoffProperty(MPAConstants.ENERGY_GENERATED, MPAConstants.ENERGY_GENERATION, 6000, "FE");
            this.ticker.addBaseProperty(MPAConstants.MOVEMENT_RESISTANCE, 0.01F);
            this.ticker.addTradeoffProperty(MPAConstants.ENERGY_GENERATED, MPAConstants.MOVEMENT_RESISTANCE, 0.49F, "%");
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap instanceof IToggleableModule) {
                ((IToggleableModule) cap).updateFromNBT();
            }
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> ticker));
        }

        class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, Callable<IConfig> config) {
                super(module, category, target, config, true);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, @Nonnull ItemStack itemStackIn) {
                if (player.abilities.isFlying || player.isPassenger() || player.isElytraFlying() || !player.isOnGround())
                    onPlayerTickInactive(player, itemStackIn);

                // really hate running this check on every tick but needed for player speed adjustments
                if (ElectricItemUtils.getPlayerEnergy(player) < ElectricItemUtils.getMaxPlayerEnergy(player)) {
                    itemStackIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h->{
                        if(h instanceof IModularItem && !((IModularItem) h).isModuleOnline(sprintAssist));
                        // only fires if the sprint assist module isn't installed and active
                        MovementManager.INSTANCE.setMovementModifier(itemStackIn, 0, player);
                    });

                    // server side
                    if (!player.world.isRemote &&
                            // every 20 ticks
                            (player.world.getGameTime() % 20) == 0 &&
                            // player not jumping, flying, or riding
                            player.isOnGround()) {
                        double distance = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                        ElectricItemUtils.givePlayerEnergy(player, (int) (distance * 10 * applyPropertyModifiers(MPAConstants.ENERGY_GENERATION)));
                    }
                }
            }

            @Override
            public void onPlayerTickInactive(PlayerEntity player, ItemStack itemStackIn) {
                itemStackIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h->{
                    if (h instanceof IModularItem && !((IModularItem) h).isModuleOnline(sprintAssist)) {
                        // only fire if sprint assist module not installed.
                        MovementManager.INSTANCE.setMovementModifier(itemStackIn, 0, player);
                    }
                });
            }
        }
    }
}