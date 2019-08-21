package net.machinemuse.powersuits.item.module.miningenhancement;

import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.numina.capabilities.module.blockbreaking.BlockBreakingCapability;
import net.machinemuse.numina.capabilities.module.miningenhancement.MiningEnhancement;
import net.machinemuse.numina.capabilities.module.miningenhancement.MiningEnhancementCapability;
import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * Created by Eximius88 on 1/29/14.
 */
public class AOEPickUpgradeModule extends AbstractPowerModule {
    public AOEPickUpgradeModule(String regName) {
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
        MiningEnhancement miningEnhancement;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.MINING_ENHANCEMENT, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 500, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.DIAMETER, MPSConstants.ENERGY_CONSUMPTION, 9500);
            this.moduleCap.addIntTradeoffProperty(MPSConstants.DIAMETER, MPSConstants.AOE_MINING_RADIUS, 5, "m", 2, 1);
            this.miningEnhancement = new Enhancement();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == MiningEnhancementCapability.MINING_ENHANCEMENT)
                return MiningEnhancementCapability.MINING_ENHANCEMENT.orEmpty(cap, LazyOptional.of(() -> miningEnhancement));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class Enhancement extends MiningEnhancement {
            @Override
            public boolean onBlockStartBreak(ItemStack itemStack, BlockPos posIn, PlayerEntity player) {
                if (player.world.isRemote)
                    return false; // fixme : check?

                AtomicBoolean harvested = new AtomicBoolean(false);
                RayTraceResult rayTraceResult = rayTrace(player.world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
                if (rayTraceResult == null || rayTraceResult.getType() != RayTraceResult.Type.BLOCK)
                    return false;

                int radius = (int) (moduleCap.applyPropertyModifiers(MPSConstants.AOE_MINING_RADIUS) - 1) / 2;

                if (radius == 0)
                    return false;

                Direction side = ((BlockRayTraceResult) rayTraceResult).getFace();
                Stream<BlockPos> posList;
                switch (side) {
                    case UP:
                    case DOWN:
                        posList = BlockPos.getAllInBox(posIn.north(radius).west(radius), posIn.south(radius).east(radius));
                        break;

                    case EAST:
                    case WEST:
                        posList = BlockPos.getAllInBox(posIn.up(radius).north(radius), posIn.down(radius).south(radius));
                        break;

                    case NORTH:
                    case SOUTH:
                        posList = BlockPos.getAllInBox(posIn.up(radius).west(radius), posIn.down(radius).east(radius));
                        break;

                    default:
                        posList = new ArrayList<BlockPos>().stream();
                }
                int energyUsage = this.getEnergyUsage();

                posList.forEach(blockPos-> {
                    BlockState state = player.world.getBlockState(blockPos);
                    Block block = state.getBlock();
                    int playerEnergy = ElectricItemUtils.getPlayerEnergy(player);
                    itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(modeChanging -> {
                        if (modeChanging instanceof IModeChangingItem) {
                            for (ItemStack blockBreakingModule : ((IModeChangingItem) modeChanging).getInstalledModulesOfType(BlockBreakingCapability.BLOCK_BREAKING)) {
                                if (blockBreakingModule.getCapability(BlockBreakingCapability.BLOCK_BREAKING).map(b -> b
                                        .canHarvestBlock(itemStack, state, player, blockPos, playerEnergy - energyUsage)).orElse(false)) {
                                    if (posIn == blockPos) // center block
                                        harvested.set(true);
                                    block.onPlayerDestroy(player.world, blockPos, state);
                                    block.harvestBlock(player.world, player, blockPos, state, player.world.getTileEntity(blockPos), player.getHeldItemMainhand());
//                                player.world.playEvent(null, 2001, blockPos, Block.getStateId(state));
//                                player.world.removeBlock(blockPos, false);
//                                block.breakBlock(player.world, blockPos, state);
//                                block.dropBlockAsItem(player.world, blockPos, state, 0);
                                    ElectricItemUtils.drainPlayerEnergy(player,
                                            blockBreakingModule.getCapability(BlockBreakingCapability.BLOCK_BREAKING)
                                                    .map(m -> m.getEnergyUsage()).orElse(0) + energyUsage);
                                    break;
                                }
                            }
                        }
                    });
                });
                return harvested.get();
            }

            @Override
            public int getEnergyUsage() {
                return (int) moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
            }
        }
    }
}