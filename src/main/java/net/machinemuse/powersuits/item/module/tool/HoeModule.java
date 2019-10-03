package net.machinemuse.powersuits.item.module.tool;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.capabilities.module.rightclick.IRightClickModule;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickModule;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.helper.ToolHelpers;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class HoeModule extends AbstractPowerModule {
    protected static final Map<Block, BlockState> HOE_LOOKUP = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.getDefaultState(), Blocks.GRASS_PATH, Blocks.FARMLAND.getDefaultState(), Blocks.DIRT, Blocks.FARMLAND.getDefaultState(), Blocks.COARSE_DIRT, Blocks.DIRT.getDefaultState()));

    public HoeModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IRightClickModule rightClick;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.rightClick = new RightClickie(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
            this.rightClick.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 500, "RF");
            this.rightClick.addTradeoffPropertyDouble(MPSConstants.RADIUS, MPSConstants.ENERGY_CONSUMPTION, 9500);
            this.rightClick.addTradeoffPropertyDouble(MPSConstants.RADIUS, MPSConstants.RADIUS, 8, "m");
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
              return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> rightClick));
        }

        class RightClickie extends RightClickModule {
            public RightClickie(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public ActionResultType onItemUse(ItemUseContext context) {
                int energyConsumed = this.getEnergyUsage();
                PlayerEntity player = context.getPlayer();
                World world = context.getWorld();
                BlockPos pos = context.getPos();
                Direction facing = context.getFace();
                ItemStack itemStack = context.getItem();

                if (!player.canPlayerEdit(pos, facing, itemStack) || ElectricItemUtils.getPlayerEnergy(player) < energyConsumed) {
                    return ActionResultType.PASS;
                } else {
                    int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(context);
                    if (hook != 0) return hook > 0 ? ActionResultType.SUCCESS : ActionResultType.FAIL;
                    int radius = (int)applyPropertyModifiers(MPSConstants.RADIUS);
                    for (int i = (int) Math.floor(-radius); i < radius; i++) {
                        for (int j = (int) Math.floor(-radius); j < radius; j++) {
                            if (i * i + j * j < radius * radius) {
                                BlockPos newPos = pos.add(i, 0, j);
                                if (facing != Direction.DOWN && (world.isAirBlock(newPos.up()) || ToolHelpers.blockCheckAndHarvest(player, world, newPos.up()))) {
                                    if (facing != Direction.DOWN && world.isAirBlock(newPos.up())) {
                                        BlockState blockstate = HOE_LOOKUP.get(world.getBlockState(newPos).getBlock());
                                        if (blockstate != null) {
                                            world.playSound(player, newPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

                                            if (!world.isRemote) {
                                                world.setBlockState(newPos, blockstate, 11);
                                                ElectricItemUtils.drainPlayerEnergy(player, energyConsumed);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return ActionResultType.SUCCESS;
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
            }
        }
    }
}