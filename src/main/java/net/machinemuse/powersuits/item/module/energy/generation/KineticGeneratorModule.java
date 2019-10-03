package net.machinemuse.powersuits.item.module.energy.generation;

import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.capabilities.module.tickable.IPlayerTickModule;
import net.machinemuse.numina.capabilities.module.tickable.PlayerTickModule;
import net.machinemuse.numina.capabilities.module.toggleable.IToggleableModule;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.MPSRegistryNames;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.event.MovementManager;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
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
    static final ResourceLocation sprintAssist = new ResourceLocation(MPSRegistryNames.MODULE_SPRINT_ASSIST__REGNAME);

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
            this.ticker = new Ticker(module, EnumModuleCategory.ENERGY_GENERATION, EnumModuleTarget.TORSOONLY, CommonConfig.moduleConfig);
            this.ticker.addBasePropertyDouble(MPSConstants.ENERGY_GENERATION, 2000);
            this.ticker.addTradeoffPropertyDouble(MPSConstants.ENERGY_GENERATED, MPSConstants.ENERGY_GENERATION, 6000, "RF");
            this.ticker.addBasePropertyDouble(MPSConstants.MOVEMENT_RESISTANCE, 0.01);
            this.ticker.addTradeoffPropertyDouble(MPSConstants.ENERGY_GENERATED, MPSConstants.MOVEMENT_RESISTANCE, 0.49, "%");
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
                        ElectricItemUtils.givePlayerEnergy(player, (int) (distance * 10 * applyPropertyModifiers(MPSConstants.ENERGY_GENERATION)));
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