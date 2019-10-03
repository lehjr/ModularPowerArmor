package net.machinemuse.powersuits.item.module.movement;

import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.capabilities.module.tickable.IPlayerTickModule;
import net.machinemuse.numina.capabilities.module.tickable.PlayerTickModule;
import net.machinemuse.numina.capabilities.module.toggleable.IToggleableModule;
import net.machinemuse.numina.control.PlayerMovementInputWrapper;
import net.machinemuse.numina.player.NuminaPlayerUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.MPSRegistryNames;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GliderModule extends AbstractPowerModule {
    static final ResourceLocation parachute = new ResourceLocation(MPSRegistryNames.MODULE_PARACHUTE__REGNAME);

    public GliderModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.TORSOONLY, CommonConfig.moduleConfig);

            this.ticker.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 0);
            this.ticker.addBasePropertyDouble(MPSConstants.JETBOOTS_THRUST, 0);
            this.ticker.addTradeoffPropertyDouble(MPSConstants.THRUST, MPSConstants.ENERGY_CONSUMPTION, 750, "RF");
            this.ticker.addTradeoffPropertyDouble(MPSConstants.THRUST, MPSConstants.JETBOOTS_THRUST, 0.08);
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
                super(module, category, target, config, false);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack chestPlate) {
                Vec3d playerHorzFacing = player.getLookVec();
                playerHorzFacing = new Vec3d(playerHorzFacing.x, 0, playerHorzFacing.z);
                playerHorzFacing.normalize();
                PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);

                NuminaPlayerUtils.resetFloatKickTicks(player);
                boolean hasParachute = chestPlate.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                        .map(m-> m instanceof IModularItem && ((IModularItem) m).isModuleOnline(parachute)).orElse(false);
                if (playerInput.sneakKey && player.getMotion().y < 0 && (!hasParachute || playerInput.moveForward > 0)) {
                    if (player.getMotion().y < -0.1) {
                        // FIXME: volume parameter missing
//                        float vol = (float) (player.getMotion().x * player.getMotion().x + player.getMotion().z * player.getMotion().z);
                        double motionYchange = Math.min(0.08, -0.1 - player.getMotion().y);
                        Vec3d motion = player.getMotion();
                        player.setMotion(motion.add(
                                playerHorzFacing.x * motionYchange,
                                motionYchange,
                                playerHorzFacing.z * motionYchange
                        ));

                        // sprinting speed
                        player.jumpMovementFactor += 0.03f;
                    }
                }
            }
        }
    }
}