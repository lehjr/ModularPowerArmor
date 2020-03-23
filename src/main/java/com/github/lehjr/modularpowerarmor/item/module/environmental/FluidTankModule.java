package com.github.lehjr.modularpowerarmor.item.module.environmental;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.config.CommonConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.IItemStackUpdate;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
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
            this.ticker.addBasePropertyInteger(MPAConstants.FLUID_TANK_SIZE, 20000);
//            this.ticker.addBasePropertyDouble(ACTIVATION_PERCENT, 0.5);
//            this.ticker.addTradeoffPropertyDouble("Activation Percent", ACTIVATION_PERCENT, 0.5, "%");
            this.fluidHandler = new ModuleTank(ticker.applyPropertyModifierBaseInt(MPAConstants.FLUID_TANK_SIZE));
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
                return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> fluidHandler));
            }
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> ticker));
        }

        class ModuleTank extends FluidTank implements IItemStackUpdate, IFluidTank, IFluidHandler, IFluidHandlerItem, INBTSerializable<CompoundNBT> {
            public ModuleTank(int capacity) {
                super(capacity);
                this.updateFromNBT();
            }

            @Override
            protected void onContentsChanged() {
                NBTUtils.getModuleTag(module).put(FLUID_NBT_KEY, writeToNBT(new CompoundNBT()));
            }

            @Nonnull
            @Override
            public ItemStack getContainer() {
                return module;
            }

            @Override
            public void updateFromNBT() {
                CompoundNBT nbt = NBTUtils.getModuleTag(module);
                if (nbt != null && nbt.contains(FLUID_NBT_KEY, Constants.NBT.TAG_COMPOUND)) {
                    this.deserializeNBT(nbt.getCompound(FLUID_NBT_KEY));
                }
            }

            @Override
            public CompoundNBT serializeNBT() {
                return this.writeToNBT(new CompoundNBT());
            }

            @Override
            public void deserializeNBT(CompoundNBT nbt) {
                this.setFluid(FluidStack.loadFluidStackFromNBT(nbt));
            }
        }

        class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config, boolean defBool) {
                super(module, category, target, config, defBool);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, @Nonnull ItemStack item) {
                if (/*player.world.isRemote() &&*/ player.getEntityWorld().getGameTime() % 50 == 0) {
                    // seems to be setup in such a way as to allow for multiple tanks in one
                    for (int i = 0; i < fluidHandler.getTanks(); i++) {
                        int maxFluid = fluidHandler.getTankCapacity(i);
                        int currentFluid = fluidHandler.getFluidInTank(i).getAmount();

                        // fill the tank
                        if (currentFluid < maxFluid) {
                            BlockPos pos = new BlockPos(player);

                            BlockState blockstate = player.world.getBlockState(pos);

                            // fill by being in water
                            if (player.isInWater() && player.world.getBlockState(pos).getBlock() != Blocks.BUBBLE_COLUMN) {
                            if (blockstate.getBlock() instanceof IBucketPickupHandler) {
                                Fluid fluid = ((IBucketPickupHandler) blockstate.getBlock()).pickupFluid(player.world, pos, blockstate);
//                                player.world.removeBlock(pos, false);
                                FluidStack water = new FluidStack(fluid, 1000);
                                fluidHandler.fill(water, IFluidHandler.FluidAction.EXECUTE);
                                player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                            }

                                // fill by being in the rain or bubble column
                            } else if (player.isInWaterRainOrBubbleColumn()) {
                                FluidStack water = new FluidStack(Fluids.WATER, 100);
                                fluidHandler.fill(water, IFluidHandler.FluidAction.EXECUTE);
                                player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                            }
                        }
                    }

                    // TODO: actual cooling







                }

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