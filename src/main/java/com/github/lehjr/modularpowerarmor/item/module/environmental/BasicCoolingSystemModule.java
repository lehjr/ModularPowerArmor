package com.github.lehjr.modularpowerarmor.item.module.environmental;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BasicCoolingSystemModule extends CoolingSystemBase {
    public BasicCoolingSystemModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProviderThis(stack);
    }

    public static class CapProviderThis extends CapProvider {
        public CapProviderThis(@Nonnull ItemStack module) {
            super(module);
            this.ticker = new ThisTicker(module, EnumModuleCategory.ENVIRONMENTAL, EnumModuleTarget.TORSOONLY, MPAConfig.moduleConfig, true);
            this.ticker.addBasePropertyInteger(Constants.FLUID_TANK_SIZE, 20000);
            this.ticker.addBasePropertyDouble(Constants.COOLING_FACTOR, 1);
            this.ticker.addTradeoffPropertyDouble(Constants.COOLING_POWER, Constants.COOLING_BONUS, 4, "%");
            this.ticker.addTradeoffPropertyDouble(Constants.COOLING_POWER, Constants.ENERGY_CONSUMPTION, 100, "RF/t");

            this.fluidHandler = new ThisModuleTank(ticker.applyPropertyModifierBaseInt(Constants.FLUID_TANK_SIZE));
        }

        public class ThisModuleTank extends ModuleTank {
            public ThisModuleTank(int tankSize) {
                super(tankSize);
            }

            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                if (fluid != null) {
                    return fluid.getFluid() == FluidRegistry.WATER;
                }
                return false;
            }
        }

        public class ThisTicker extends Ticker {
            public ThisTicker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config, boolean defBool) {
                super(module, category, target, config, defBool);
            }

            @Override
            public void onPlayerTickActive(EntityPlayer player, ItemStack itemStack) {
                super.onPlayerTickActive(player, itemStack);
                fillWaterFromEnvironment(player);
            }

            public void fillWaterFromEnvironment(EntityPlayer player) {
                // Fill with water block and remove that block from the world
                if (player.isInWater()) {
                    // Fill tank if player is in water
                    IBlockState iblockstate = player.world.getBlockState(player.getPosition());
                    Material material = iblockstate.getMaterial();

                    if (material == Material.WATER && iblockstate.getValue(BlockLiquid.LEVEL).intValue() == 0) {
                        Fluid fluid = FluidRegistry.lookupFluidForBlock(iblockstate.getBlock());
                        player.world.setBlockState(player.getPosition(), Blocks.AIR.getDefaultState(), 11);
                        player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                        fluidHandler.fill(new FluidStack(fluid, Math.min(((CoolingSystemBase.CapProvider.ModuleTank)fluidHandler).getCapacity() - ((CoolingSystemBase.CapProvider.ModuleTank)fluidHandler).getFluidAmount(), 1000)), false);
                    }
                }

                // Fill tank if raining
                boolean isRaining = (player.world.getBiomeForCoordsBody(player.getPosition()).getRainfall() > 0) && (player.world.isRaining() || player.world.isThundering());
                if (isRaining && player.world.canBlockSeeSky(player.getPosition().add(0, 1, 0))
                        && (player.world.getTotalWorldTime() % 5) == 0 && ((CoolingSystemBase.CapProvider.ModuleTank)fluidHandler).getFluidAmount() < ((CoolingSystemBase.CapProvider.ModuleTank)fluidHandler).getCapacity()) {
                    fluidHandler.fill(new FluidStack(FluidRegistry.WATER, Math.min(((CoolingSystemBase.CapProvider.ModuleTank)fluidHandler).getCapacity() - ((CoolingSystemBase.CapProvider.ModuleTank)fluidHandler).getFluidAmount(), 100)), false);
                }
            }
        }
    }
}