package net.machinemuse.powersuits.item.module.movement;

import net.machinemuse.numina.capabilities.inventory.modularitem.ModularItemCapability;
import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.tickable.IModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTickCapability;
import net.machinemuse.numina.capabilities.module.toggleable.IModuleToggle;
import net.machinemuse.numina.capabilities.module.toggleable.Toggle;
import net.machinemuse.numina.capabilities.module.toggleable.ToggleCapability;
import net.machinemuse.numina.control.PlayerMovementInputWrapper;
import net.machinemuse.numina.player.NuminaPlayerUtils;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.MPSItems;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GliderModule extends AbstractPowerModule {
    static final ResourceLocation parachute = new ResourceLocation(MPSItems.INSTANCE.MODULE_PARACHUTE__REGNAME);

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
        IPowerModule moduleCap;
        IModuleTick ticker;
        IModuleToggle toggle;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.CATEGORY_MOVEMENT, EnumModuleTarget.TORSOONLY, MPSConfig.INSTANCE);

            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 0);
            this.moduleCap.addBasePropertyDouble(MPSConstants.JETBOOTS_THRUST, 0);
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.THRUST, MPSConstants.ENERGY_CONSUMPTION, 750, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.THRUST, MPSConstants.JETBOOTS_THRUST, 0.08);

            this.toggle = new Toggle(module);
            this.ticker = new CapProvider.Ticker();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == ModuleTickCapability.TICK)
                return ModuleTickCapability.TICK.orEmpty(cap, LazyOptional.of(() -> ticker));
            if (cap == ToggleCapability.TOGGLEABLE_MODULE)
                return ToggleCapability.TOGGLEABLE_MODULE.orEmpty(cap, LazyOptional.of(() -> toggle));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class Ticker extends ModuleTick {
            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack chestPlate) {
                Vec3d playerHorzFacing = player.getLookVec();
                playerHorzFacing = new Vec3d(playerHorzFacing.x, 0, playerHorzFacing.z);
                playerHorzFacing.normalize();
                PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);

                NuminaPlayerUtils.resetFloatKickTicks(player);
                boolean hasParachute = chestPlate.getCapability(ModularItemCapability.MODULAR_ITEM).map(m->m.isModuleOnline(parachute)).orElse(false);

                if (playerInput.sneakKey && player.getMotion().y < 0 && (!hasParachute || playerInput.moveForward > 0)) {
                    if (player.getMotion().y < -0.1) {
                        float vol = (float) (player.getMotion().x * player.getMotion().x + player.getMotion().z * player.getMotion().z);
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