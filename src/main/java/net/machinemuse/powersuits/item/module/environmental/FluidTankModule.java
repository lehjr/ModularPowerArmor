package net.machinemuse.powersuits.item.module.environmental;

import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.capabilities.module.tickable.IPlayerTickModule;
import net.machinemuse.numina.capabilities.module.tickable.PlayerTickModule;
import net.machinemuse.numina.nbt.MuseNBTUtils;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidTankModule extends AbstractPowerModule {
    static final String FLUID_NBT_KEY = "Fluid";
    public FluidTankModule(String regName) {
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
        IFluidHandlerItem fluidHandler;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.ENVIRONMENTAL, EnumModuleTarget.TORSOONLY, CommonConfig.moduleConfig, true);
            this.ticker.addBasePropertyInteger(MPSConstants.FLUID_TANK_SIZE, 20000);
            this.fluidHandler = new ModuleTank(ticker.applyPropertyModifierBaseInt(MPSConstants.FLUID_TANK_SIZE));

            /*
                    addBaseProperty(ACTIVATION_PERCENT, 0.5);
        addTradeoffProperty("Activation Percent", ACTIVATION_PERCENT, 0.5, "%");
             */

        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> fluidHandler));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> ticker));
        }

        class ModuleTank extends FluidTank implements IFluidTank, IFluidHandler, IFluidHandlerItem {
            public ModuleTank(int capacity) {
                super(capacity);
            }

            @Nullable
            public FluidStack getFluid() {
                CompoundNBT tagCompound = MuseNBTUtils.getMuseModuleTag(module);
                if (tagCompound == null || !tagCompound.contains(FLUID_NBT_KEY)) {
                    return null;
                }
                return FluidStack.loadFluidStackFromNBT(tagCompound.getCompound(FLUID_NBT_KEY));
            }

            @Override
            protected void onContentsChanged() {
                MuseNBTUtils.getMuseModuleTag(module).put(FLUID_NBT_KEY, writeToNBT(new CompoundNBT()));
            }

            @Nonnull
            @Override
            public ItemStack getContainer() {
                return module;
            }
        }

        class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config, boolean defBool) {
                super(module, category, target, config, defBool);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, @Nonnull ItemStack item) {
//                if (MuseItemUtils.getWaterLevel(item) > ModuleManager.computeModularProperty(item, WATER_TANK_SIZE)) {
//                    MuseItemUtils.setWaterLevel(item, ModuleManager.computeModularProperty(item, WATER_TANK_SIZE));
//                }
//                // Fill tank if player is in water
//                Block block = player.worldObj.getBlock(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
//                if (((block == Blocks.water) || block == Blocks.flowing_water) && MuseItemUtils.getWaterLevel(item) < ModuleManager.computeModularProperty(item, WATER_TANK_SIZE)) {
//                    MuseItemUtils.setWaterLevel(item, MuseItemUtils.getWaterLevel(item) + 1);
//                }
//                // Fill tank if raining
//                int xCoord = MathHelper.floor_double(player.posX);
//                int zCoord = MathHelper.floor_double(player.posZ);
//                boolean isRaining = (player.worldObj.getWorldChunkManager().getBiomeGenAt(xCoord, zCoord).getIntRainfall() > 0) && (player.worldObj.isRaining() || player.worldObj.isThundering());
//                if (isRaining && player.worldObj.canBlockSeeTheSky(xCoord, MathHelper.floor_double(player.posY) + 1, zCoord) && (player.worldObj.getTotalWorldTime() % 5) == 0 && MuseItemUtils.getWaterLevel(item) < ModuleManager.computeModularProperty(item, WATER_TANK_SIZE)) {
//                    MuseItemUtils.setWaterLevel(item, MuseItemUtils.getWaterLevel(item) + 1);
//                }
//                // Apply cooling
//                double currentHeat = MuseHeatUtils.getPlayerHeat(player);
//                double maxHeat = MuseHeatUtils.getMaxHeat(player);
//                if ((currentHeat / maxHeat) >= ModuleManager.computeModularProperty(item, ACTIVATION_PERCENT) && MuseItemUtils.getWaterLevel(item) > 0) {
//                    MuseHeatUtils.coolPlayer(player, 1);
//                    MuseItemUtils.setWaterLevel(item, MuseItemUtils.getWaterLevel(item) - 1);
//                    for (int i = 0; i < 4; i++) {
//                        player.worldObj.spawnParticle("smoke", player.posX, player.posY + 0.5, player.posZ, 0.0D, 0.0D, 0.0D);
//                    }
//                }
            }
        }
    }
}