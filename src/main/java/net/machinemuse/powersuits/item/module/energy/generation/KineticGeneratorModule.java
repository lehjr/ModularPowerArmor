package net.machinemuse.powersuits.item.module.energy.generation;

import net.machinemuse.numina.capabilities.inventory.modularitem.ModularItemCapability;
import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.tickable.IModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTickCapability;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.MPSItems;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KineticGeneratorModule extends AbstractPowerModule {
    static final ResourceLocation sprintAssist = new ResourceLocation(MPSItems.INSTANCE.MODULE_SPRINT_ASSIST__REGNAME);

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
        IPowerModule moduleCap;
        IModuleTick ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.CATEGORY_ENERGY_GENERATION, EnumModuleTarget.TORSOONLY, MPSConfig.INSTANCE);

            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_GENERATION, 2000);
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.ENERGY_GENERATED, MPSConstants.ENERGY_GENERATION, 6000, "RF");
            this.moduleCap.addBasePropertyDouble(MPSConstants.MOVEMENT_RESISTANCE, 0.01);
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.ENERGY_GENERATED, MPSConstants.MOVEMENT_RESISTANCE, 0.49, "%");
            this.ticker = new Ticker(moduleCap);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == ModuleTickCapability.TICK)
                return ModuleTickCapability.TICK.orEmpty(cap, LazyOptional.of(() -> ticker));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class Ticker extends ModuleTick {
            IPowerModule moduleCap;

            public Ticker(IPowerModule moduleCapIn) {
                this.moduleCap = moduleCapIn;
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, @Nonnull ItemStack itemStackIn) {
                if (player.abilities.isFlying || player.isPassenger() || player.isElytraFlying() || !player.onGround)
                    onPlayerTickInactive(player, itemStackIn);

                // really hate running this check on every tick but needed for player speed adjustments
                if (ElectricItemUtils.getPlayerEnergy(player) < ElectricItemUtils.getMaxPlayerEnergy(player)) {
                    // only fires if the sprint assist module isn't installed and active
                    if (!itemStackIn.getCapability(ModularItemCapability.MODULAR_ITEM)
                            .map(i-> i.isModuleOnline(sprintAssist)).orElse(false)) {
                        MovementManager.setMovementModifier(itemStackIn, 0, player);
                    }

                    // server side
                    if (!player.world.isRemote &&
                            // every 20 ticks
                            (player.world.getGameTime() % 20) == 0 &&
                            // player not jumping, flying, or riding
                            player.onGround) {
                        double distance = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                        ElectricItemUtils.givePlayerEnergy(player, (int) (distance * 10 *  moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_GENERATION)));
                    }
                }
            }

            @Override
            public void onPlayerTickInactive(PlayerEntity player, ItemStack itemStackIn) {
                // only fire if sprint assist module not installed.
                if (!itemStackIn.getCapability(ModularItemCapability.MODULAR_ITEM)
                        .map(i-> i.isModuleInstalled(sprintAssist)).orElse(false)) {
                    MovementManager.setMovementModifier(itemStackIn, 0, player);
                }
            }
        }
    }
}