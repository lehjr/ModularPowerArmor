package com.github.lehjr.modularpowerarmor.item.module.environmental;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author lehjr
 */
public class FluidTankModule extends AbstractPowerModule {
    static final String FLUID_NBT_KEY = "Fluid";
    public FluidTankModule(String regName) {
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
        IFluidHandlerItem fluidHandler;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.ENVIRONMENTAL, EnumModuleTarget.TORSOONLY, MPAConfig.INSTANCE.moduleConfig, true);
            this.ticker.addBasePropertyInteger(Constants.FLUID_TANK_SIZE, 20000);
            this.fluidHandler = new ModuleTank(ticker.applyPropertyModifierBaseInt(Constants.FLUID_TANK_SIZE));

            /*
                    addBaseProperty(ACTIVATION_PERCENT, 0.5);
        addTradeoffProperty("Activation Percent", ACTIVATION_PERCENT, 0.5, "%");
             */

        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
                return true;
            }
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return true;
            }
            return false;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
                return (T) fluidHandler;
            }
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return (T) ticker;
            }

            return null;
        }

        class ModuleTank extends FluidTankModule implements IFluidTank, IFluidHandler, IFluidHandlerItem {
            public ModuleTank(int capacity) {
                super(capacity);
            }

            @Nullable
            public FluidStack getFluid() {
                NBTTagCompound tagCompound = NBTUtils.getMuseModuleTag(module);
                if (tagCompound == null || !tagCompound.hasKey(FLUID_NBT_KEY)) {
                    return null;
                }
                return FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag(FLUID_NBT_KEY));
            }

            @Override
            protected void onContentsChanged() {
                NBTUtils.getMuseModuleTag(module).setTag(FLUID_NBT_KEY, writeToNBT(new NBTTagCompound()));
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
            public void onPlayerTickActive(EntityPlayer player, @Nonnull ItemStack item) {
                // seems to be setup in such a way as to allow for multiple tanks in one
                int maxFluid = fluidHandler.getTankCapacity(1);
                int currentFluid = fluidHandler.getFluidInTank(0).getAmount();






//                if (ItemUtils.getWaterLevel(item) > ModuleManager.computeModularProperty(item, WATER_TANK_SIZE)) {
//                    ItemUtils.setWaterLevel(item, ModuleManager.computeModularProperty(item, WATER_TANK_SIZE));
//                }

//                // Fill tank if player is in water
//                Block block = player.world.getBlockState(new BlockPos(MathHelper.floor(player.posX), MathHelper.floor(player.posY), MathHelper.floor(player.posZ)));
//
////                if (player.isInWater())
//
//
//                if (((block == Blocks.water) || block == Blocks.flowing_water) && ItemUtils.getWaterLevel(item) < ModuleManager.computeModularProperty(item, WATER_TANK_SIZE)) {
//                    ItemUtils.setWaterLevel(item, ItemUtils.getWaterLevel(item) + 1);
//                }
//                // Fill tank if raining
//                int xCoord = MathHelper.floor_double(player.posX);
//                int zCoord = MathHelper.floor_double(player.posZ);
//                boolean isRaining = (player.worldObj.getWorldChunkManager().getBiomeGenAt(xCoord, zCoord).getIntRainfall() > 0) && (player.worldObj.isRaining() || player.worldObj.isThundering());
//                if (isRaining && player.worldObj.canBlockSeeTheSky(xCoord, MathHelper.floor_double(player.posY) + 1, zCoord) && (player.worldObj.getTotalWorldTime() % 5) == 0 && ItemUtils.getWaterLevel(item) < ModuleManager.computeModularProperty(item, WATER_TANK_SIZE)) {
//                    ItemUtils.setWaterLevel(item, ItemUtils.getWaterLevel(item) + 1);
//                }
//                // Apply cooling
//                double currentHeat = HeatUtils.getPlayerHeat(player);
//                double maxHeat = HeatUtils.getMaxHeat(player);
//                if ((currentHeat / maxHeat) >= ModuleManager.computeModularProperty(item, ACTIVATION_PERCENT) && ItemUtils.getWaterLevel(item) > 0) {
//                    HeatUtils.coolPlayer(player, 1);
//                    ItemUtils.setWaterLevel(item, ItemUtils.getWaterLevel(item) - 1);
//                    for (int i = 0; i < 4; i++) {
//                        player.worldObj.spawnParticle("smoke", player.posX, player.posY + 0.5, player.posZ, 0.0D, 0.0D, 0.0D);
//                    }
//                }
            }
        }
    }
}