package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.modularpowerarmor.basemod.Constants;

import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
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

public class ParachuteModule extends AbstractPowerModule {
    public ParachuteModule(String regName) {
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
            this.ticker = new Ticker(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.TORSOONLY, MPAConfig.moduleConfig);
            this.ticker.addTradeoffPropertyDouble(Constants.THRUST, Constants.ENERGY_CONSUMPTION, 1000, "RF");
            this.ticker.addTradeoffPropertyDouble(Constants.THRUST, Constants.SWIM_BOOST_AMOUNT, 1, "m/s");
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
            public void onPlayerTickActive (EntityPlayer player, ItemStack itemStack){
                PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);
                boolean hasGlider = false;
                PlayerUtils.resetFloatKickTicks(player);
                if (playerInput.sneakKey && player.motionY < -0.1 && (!hasGlider || playerInput.moveForward <= 0)) {
                    double totalVelocity = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ + player.motionY * player.motionY);
                    if (totalVelocity > 0) {
                        player.motionX = player.motionX * 0.1 / totalVelocity;
                        player.motionY = player.motionY * 0.1 / totalVelocity;
                        player.motionZ = player.motionZ * 0.1 / totalVelocity;
                    }
                }
            }
        }
    }
}