package com.github.lehjr.modularpowerarmor.item.module.environmental;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author lehjr
 */
public class WaterTankModule extends AbstractPowerModule {
    public WaterTankModule(String regName) {
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
            ticker.addBasePropertyDouble(Constants.HEAT_ACTIVATION_PERCENT, 0.5);
            ticker.addTradeoffPropertyDouble("Activation Percent", Constants.HEAT_ACTIVATION_PERCENT, 0.5, "%");
            this.fluidHandler = new ModuleTank(ticker.applyPropertyModifierBaseInt(Constants.FLUID_TANK_SIZE));
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
                ticker.updateFromNBT();
                return (T) ticker;
            }

            return null;
        }

        public class ModuleTank extends FluidTank implements IFluidHandler, IFluidHandlerItem, INBTSerializable<NBTTagCompound> {
            public ModuleTank(int tankSize) {
                super(tankSize);
            }

            @Nonnull
            @Override
            public ItemStack getContainer() {
                return module;
            }

            public void updateFromNBT() {
                NBTTagCompound moduleTag = NBTUtils.getMuseItemTag(module);
                if (moduleTag != null && moduleTag.hasKey(MPALIbConstants.FLUID_NBT_KEY, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND)) {
                    deserializeNBT(moduleTag.getCompoundTag(MPALIbConstants.FLUID_NBT_KEY));
                }
            }

            @Override
            public NBTTagCompound serializeNBT() {
                return this.writeToNBT(new NBTTagCompound());
            }

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
                this.setFluid(FluidStack.loadFluidStackFromNBT(nbt));
            }

            @Override
            public boolean canDrainFluidType(@Nullable FluidStack fluid) {
                return true;
            }

            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                if (fluid != null) {
                    return fluid.getFluid() == FluidRegistry.WATER;
                }
                return false;
            }

            @Override
            protected void onContentsChanged() {
                NBTTagCompound moduleTag = NBTUtils.getMuseModuleTag(module);
                if (moduleTag != null) {
                    NBTTagCompound fluidTag = this.writeToNBT(new NBTTagCompound());
                    moduleTag.setTag(MPALIbConstants.FLUID_NBT_KEY, fluidTag);
                    deserializeNBT(fluidTag);
                }
            }

        }
        public class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config, boolean defBool) {
                super(module, category, target, config, defBool);
            }

            @Override
            public void onPlayerTickActive(EntityPlayer player, @Nonnull ItemStack item) {
                // Fill tank if player is in water
                Block block = player.world.getBlockState(new BlockPos(MathHelper.floor(player.posX), MathHelper.floor(player.posY), MathHelper.floor(player.posZ))).getBlock();

                if (((block == Blocks.WATER) || block == Blocks.FLOWING_WATER) && ((FluidTank)fluidHandler).getFluid().amount < applyPropertyModifiers(Constants.FLUID_TANK_SIZE)) {
                    fluidHandler.fill(new FluidStack(FluidRegistry.WATER, 1), true);
                }
                // Fill tank if raining
                int xCoord = MathHelper.floor(player.posX);
                int zCoord = MathHelper.floor(player.posZ);

                boolean isRaining = (player.world.getBiome(player.getPosition()).getRainfall() > 0) && (player.world.isRaining() || player.world.isThundering());
                if (isRaining && player.world.canBlockSeeSky(new BlockPos(xCoord, MathHelper.floor(player.posY) + 1, zCoord)) && (player.world.getTotalWorldTime() % 5) == 0 && ((FluidTank)fluidHandler).getFluid().amount < applyPropertyModifiers(Constants.FLUID_TANK_SIZE)) {
                    fluidHandler.fill(new FluidStack(FluidRegistry.WATER, 1), true);
                }
                // Apply cooling
                double currentHeat = HeatUtils.getPlayerHeat(player);
                double maxHeat = HeatUtils.getPlayerMaxHeat(player);
                if ((currentHeat / maxHeat) >= applyPropertyModifiers(Constants.HEAT_ACTIVATION_PERCENT) && fluidHandler.drain(1, true).amount > 0) {
                    HeatUtils.coolPlayer(player, 1);
                    for (int i = 0; i < 4; i++) {
                        player.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, player.posX, player.posY + 0.5, player.posZ, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
    }
}