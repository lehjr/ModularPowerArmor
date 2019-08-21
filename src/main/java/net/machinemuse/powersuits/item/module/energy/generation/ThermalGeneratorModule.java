package net.machinemuse.powersuits.item.module.energy.generation;

import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.tickable.IModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTickCapability;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.heat.MuseHeatUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by User: Andrew2448
 * 6:43 PM 4/23/13
 */
public class ThermalGeneratorModule extends AbstractPowerModule {
    public ThermalGeneratorModule(String regName) {
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
            this.moduleCap = new PowerModule(module, EnumModuleCategory.ENERGY_GENERATION, EnumModuleTarget.TORSOONLY, CommonConfig.moduleConfig);
            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_GENERATION, 250);
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.ENERGY_GENERATED, MPSConstants.ENERGY_GENERATION, 250, "RF");
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
            public void onPlayerTickActive(PlayerEntity player, ItemStack item) {
                double currentHeat = MuseHeatUtils.getPlayerHeat(player);
                double maxHeat = MuseHeatUtils.getPlayerMaxHeat(player);
                if (player.world.getGameTime() % 20 == 0) {
                    if (player.isBurning()) {
                        ElectricItemUtils.givePlayerEnergy(player, (int) (4 * moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_GENERATION)));
                    } else if (currentHeat >= 200) {
                        ElectricItemUtils.givePlayerEnergy(player, (int) (2 * moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_GENERATION)));
                    } else if ((currentHeat / maxHeat) >= 0.5) {
                        ElectricItemUtils.givePlayerEnergy(player, (int) moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_GENERATION));
                    }
                }
            }
        }
    }
}