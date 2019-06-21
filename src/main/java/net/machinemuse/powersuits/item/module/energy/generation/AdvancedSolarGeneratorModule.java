package net.machinemuse.powersuits.item.module.energy.generation;

import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.tickable.IModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTickCapability;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.heat.MuseHeatUtils;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.item.armor.ItemPowerArmorHelmet;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Eximius88 on 1/12/14.
 */
public class AdvancedSolarGeneratorModule extends AbstractPowerModule {
    public AdvancedSolarGeneratorModule(String regName) {
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
            this.moduleCap = new PowerModule(module, EnumModuleCategory.CATEGORY_ENERGY_GENERATION, EnumModuleTarget.HEADONLY, MPSConfig.INSTANCE);
            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_GENERATION_DAY, 45000, "RF");
            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_GENERATION_NIGHT, 1500, "RF");
            this.moduleCap.addBasePropertyDouble(MPSConstants.HEAT_GENERATION_DAY, 15);
            this.moduleCap.addBasePropertyDouble(MPSConstants.HEAT_GENERATION_NIGHT, 5);
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
            public void onPlayerTickActive(PlayerEntity player, ItemStack itemStack) {
                World world = player.world;
                boolean isRaining, canRain = true;
                if (world.getGameTime() % 20 == 0) {
                    canRain = world.getBiome(player.getPosition()).getPrecipitation() != Biome.RainType.NONE;
                }
                isRaining = canRain && (world.isRaining() || world.isThundering());
                boolean sunVisible = world.isDaytime() && !isRaining && world.canBlockSeeSky(player.getPosition().up());
                boolean moonVisible = !world.isDaytime() && !isRaining && world.canBlockSeeSky(player.getPosition().up());

                if (!world.isRemote && world.dimension.hasSkyLight() && (world.getGameTime() % 80) == 0) {
                    double lightLevelScaled = (world.getLightFor(LightType.SKY, player.getPosition().up()) - world.getSkylightSubtracted())/15D;

                    if (sunVisible) {
                        ElectricItemUtils.givePlayerEnergy(player, (int) (moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_GENERATION_DAY) * lightLevelScaled));
                        MuseHeatUtils.heatPlayer(player, moduleCap.applyPropertyModifiers(MPSConstants.HEAT_GENERATION_DAY) * lightLevelScaled / 2);
                    } else if (moonVisible) {
                        ElectricItemUtils.givePlayerEnergy(player, (int) (moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_GENERATION_NIGHT) * lightLevelScaled));
                        MuseHeatUtils.heatPlayer(player, moduleCap.applyPropertyModifiers(MPSConstants.HEAT_GENERATION_NIGHT) * lightLevelScaled / 2);
                    }
                }
            }
        }
    }
}