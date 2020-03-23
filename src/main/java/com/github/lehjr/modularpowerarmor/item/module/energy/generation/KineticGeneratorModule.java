package com.github.lehjr.modularpowerarmor.item.module.energy.generation;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;
import com.github.lehjr.modularpowerarmor.event.MovementManager;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
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

public class KineticGeneratorModule extends AbstractPowerModule {
    static final ResourceLocation sprintAssist = new ResourceLocation(MPARegistryNames.MODULE_SPRINT_ASSIST__REGNAME);

    public KineticGeneratorModule(String regName) {
        super(regName);
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
            this.ticker = new Ticker(module, EnumModuleCategory.ENERGY_GENERATION, EnumModuleTarget.TORSOONLY, CommonConfigX.moduleConfig);
            this.ticker.addBasePropertyDouble(MPAConstants.ENERGY_GENERATION, 2000);
            this.ticker.addTradeoffPropertyDouble(MPAConstants.ENERGY_GENERATED, MPAConstants.ENERGY_GENERATION, 6000, "RF");
            this.ticker.addBasePropertyDouble(MPAConstants.MOVEMENT_RESISTANCE, 0.01);
            this.ticker.addTradeoffPropertyDouble(MPAConstants.ENERGY_GENERATED, MPAConstants.MOVEMENT_RESISTANCE, 0.49, "%");
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
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config, true);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, @Nonnull ItemStack itemStackIn) {
                if (player.abilities.isFlying || player.isPassenger() || player.isElytraFlying() || !player.onGround)
                    onPlayerTickInactive(player, itemStackIn);

                // really hate running this check on every tick but needed for player speed adjustments
                if (ElectricItemUtils.getPlayerEnergy(player) < ElectricItemUtils.getMaxPlayerEnergy(player)) {
                    itemStackIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h->{
                        if(h instanceof IModularItem && !((IModularItem) h).isModuleOnline(sprintAssist));
                        // only fires if the sprint assist module isn't installed and active
                        MovementManager.setMovementModifier(itemStackIn, 0, player);
                    });

                    // server side
                    if (!player.world.isRemote &&
                            // every 20 ticks
                            (player.world.getGameTime() % 20) == 0 &&
                            // player not jumping, flying, or riding
                            player.onGround) {
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
                        MovementManager.setMovementModifier(itemStackIn, 0, player);
                    }
                });
            }
        }
    }
}