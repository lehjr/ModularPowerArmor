package net.machinemuse.powersuits.item.module.movement;

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
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParachuteModule extends AbstractPowerModule {
    public ParachuteModule(String regName) {
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

            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.THRUST, MPSConstants.ENERGY_CONSUMPTION, 1000, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.THRUST, MPSConstants.SWIM_BOOST_AMOUNT, 1, "m/s");

            this.toggle = new Toggle(module);
            this.ticker = new Ticker();
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
            public void onPlayerTickActive (PlayerEntity player, ItemStack itemStack){
                PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);
                boolean hasGlider = false;
                NuminaPlayerUtils.resetFloatKickTicks(player);
                if (playerInput.sneakKey && player.getMotion().y < -0.1 && (!hasGlider || playerInput.moveForward <= 0)) {
                    double totalVelocity = Math.sqrt(player.getMotion().x * player.getMotion().x + player.getMotion().z * player.getMotion().z + player.getMotion().y * player.getMotion().y);
                    if (totalVelocity > 0) {
                        Vec3d motion = player.getMotion();
                        player.setMotion(
                                motion.x * 0.1 / totalVelocity,
                                motion.y * 0.1 / totalVelocity,
                                motion.z * 0.1 / totalVelocity);
                    }
                }
            }
        }
    }
}