package com.github.lehjr.modularpowerarmor.item.module.energy.generation;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.RegistryNames;
import com.github.lehjr.modularpowerarmor.event.MovementManager;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class KineticGeneratorModule extends AbstractPowerModule {
    static final ResourceLocation sprintAssist = new ResourceLocation(RegistryNames.MODULE_SPRINT_ASSIST__REGNAME);

    public KineticGeneratorModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.ENERGY_GENERATION, EnumModuleTarget.TORSOONLY, CommonConfig.moduleConfig);
            this.ticker.addBasePropertyDouble(Constants.ENERGY_GENERATION, 2000);
            this.ticker.addTradeoffPropertyDouble(Constants.ENERGY_GENERATED, Constants.ENERGY_GENERATION, 6000, "RF");
            this.ticker.addBasePropertyDouble(Constants.MOVEMENT_RESISTANCE, 0.01);
            this.ticker.addTradeoffPropertyDouble(Constants.ENERGY_GENERATED, Constants.MOVEMENT_RESISTANCE, 0.49, "%");
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
            public void onPlayerTickActive(EntityPlayer player, @Nonnull ItemStack itemStackIn) {
                if (player.capabilities.isFlying || player.isRiding() || player.isElytraFlying() || !player.onGround) {
                    onPlayerTickInactive(player, itemStackIn);
                }

                // really hate running this check on every tick but needed for player speed adjustments
                if (ElectricItemUtils.getPlayerEnergy(player) < ElectricItemUtils.getMaxPlayerEnergy(player)) {
                    Optional.ofNullable(itemStackIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(h->{
                        if(h instanceof IModularItem && !((IModularItem) h).isModuleOnline(sprintAssist));
                        // only fires if the sprint assist module isn't installed and active
                        MovementManager.setMovementModifier(itemStackIn, 0, player);
                    });

                    // server side
                    if (!player.world.isRemote &&
                            // every 20 ticks
                            (player.world.getTotalWorldTime() % 20) == 0 &&
                            // player not jumping, flying, or riding
                            player.onGround) {
                        double distance = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                        ElectricItemUtils.givePlayerEnergy(player, (int) (distance * 10 * applyPropertyModifiers(Constants.ENERGY_GENERATION)));
                    }
                }
            }

            @Override
            public void onPlayerTickInactive(EntityPlayer player, ItemStack itemStackIn) {
                Optional.ofNullable(itemStackIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(h->{
                    if (h instanceof IModularItem && !((IModularItem) h).isModuleOnline(sprintAssist)) {
                        // only fire if sprint assist module not installed.
                        MovementManager.setMovementModifier(itemStackIn, 0, player);
                    }
                });
            }
        }
    }
}