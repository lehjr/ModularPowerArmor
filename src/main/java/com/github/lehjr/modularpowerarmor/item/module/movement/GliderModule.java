package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.RegistryNames;

import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class GliderModule extends AbstractPowerModule {
    static final ResourceLocation parachute = new ResourceLocation(RegistryNames.MODULE_PARACHUTE__REGNAME);

    public GliderModule(String regName) {
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

            this.ticker.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 0);
            this.ticker.addBasePropertyDouble(Constants.JETBOOTS_THRUST, 0);
            this.ticker.addTradeoffPropertyDouble(Constants.THRUST, Constants.ENERGY_CONSUMPTION, 750, "RF");
            this.ticker.addTradeoffPropertyDouble(Constants.THRUST, Constants.JETBOOTS_THRUST, 0.08);
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
            public void onPlayerTickActive(EntityPlayer player, ItemStack chestPlate) {
                Vec3d playerHorzFacing = player.getLookVec();
                playerHorzFacing = new Vec3d(playerHorzFacing.x, 0, playerHorzFacing.z);
                playerHorzFacing.normalize();
                PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);

                PlayerUtils.resetFloatKickTicks(player);
                boolean hasParachute = Optional.ofNullable(chestPlate.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
                        .map(m-> m instanceof IModularItem && ((IModularItem) m).isModuleOnline(parachute)).orElse(false);
                if (playerInput.sneakKey && player.motionY < 0 && (!hasParachute || playerInput.moveForward > 0)) {
                      if (player.motionY < -0.1) {
                        double motionYchange = Math.min(0.08, -0.1 - player.motionY);
                        player.motionY += motionYchange;
                        player.motionX += playerHorzFacing.x * motionYchange;
                        player.motionZ += playerHorzFacing.z * motionYchange;

                        // sprinting speed
                        player.jumpMovementFactor += 0.03f;
                    }
                }
            }
        }
    }
}