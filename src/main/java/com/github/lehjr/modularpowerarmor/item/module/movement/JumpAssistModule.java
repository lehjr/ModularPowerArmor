package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.event.MovementManager;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.control.PlayerMovementInputWrapper;
import com.github.lehjr.mpalib.player.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JumpAssistModule extends AbstractPowerModule {
    public JumpAssistModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public static class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        PlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.LEGSONLY, MPAConfig.moduleConfig);

            this.ticker.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 0, "RF");
            this.ticker.addTradeoffPropertyDouble(Constants.POWER, Constants.ENERGY_CONSUMPTION, 250);
            this.ticker.addBasePropertyDouble(Constants.MULTIPLIER, 1, "%");
            this.ticker.addTradeoffPropertyDouble(Constants.POWER, Constants.MULTIPLIER, 4);

            this.ticker.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 0, "RF");
            this.ticker.addTradeoffPropertyDouble(Constants.COMPENSATION, Constants.ENERGY_CONSUMPTION, 50);
            this.ticker.addBasePropertyDouble(Constants.FOOD_COMPENSATION, 0, "%");
            this.ticker.addTradeoffPropertyDouble(Constants.COMPENSATION, Constants.FOOD_COMPENSATION, 1);
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
                PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);
                if (playerInput.jumpKey) {
                    double multiplier = MovementManager.getPlayerJumpMultiplier(player);
                    if (multiplier > 0) {
                        player.motionY += 0.15 * Math.min(multiplier, 1);
                        MovementManager.setPlayerJumpTicks(player, multiplier - 1);
                    }
                    player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                } else {
                    MovementManager.setPlayerJumpTicks(player, 0);
                }
                PlayerUtils.resetFloatKickTicks(player);
            }
        }
    }
}
