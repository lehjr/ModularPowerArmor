package com.github.lehjr.modularpowerarmor.item.module.miningenhancement;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.capabilities.module.blockbreaking.IBlockBreakingModule;
import com.github.lehjr.mpalib.capabilities.module.miningenhancement.IMiningEnhancementModule;
import com.github.lehjr.mpalib.capabilities.module.miningenhancement.MiningEnhancement;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Eximius88 on 1/29/14.
 */
public class AOEPickUpgradeModule extends AbstractPowerModule {
    public AOEPickUpgradeModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IMiningEnhancementModule miningEnhancement;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.miningEnhancement = new Enhancement(module, EnumModuleCategory.MINING_ENHANCEMENT, EnumModuleTarget.TOOLONLY, MPAConfig.moduleConfig);
            this.miningEnhancement.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 500, "RF");
            this.miningEnhancement.addTradeoffPropertyDouble(Constants.DIAMETER, Constants.ENERGY_CONSUMPTION, 9500);
            this.miningEnhancement.addIntTradeoffProperty(Constants.DIAMETER, Constants.AOE_MINING_RADIUS, 5, "m", 2, 1);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return (T) miningEnhancement;
            }
            return null;
        }

        class Enhancement extends MiningEnhancement {
            public Enhancement(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public boolean onBlockStartBreak(ItemStack itemStack, BlockPos posIn, EntityPlayer player) {
                if (player.world.isRemote)
                    return false; // fixme : check?

                RayTraceResult rayTraceResult = rayTrace(player.world, player, true);
                if (rayTraceResult == null)
                    return false;

                int radius = (int) (applyPropertyModifiers(Constants.AOE_MINING_RADIUS) - 1) / 2;

                if (radius == 0)
                    return false;

                EnumFacing side = rayTraceResult.sideHit;
                Iterable<BlockPos> posList;
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
                        posList = new ArrayList<>();
                }
                int energyUsage = this.getEnergyUsage();

                AtomicBoolean harvested = new AtomicBoolean(false);
                for (BlockPos blockPos : posList) {
                    IBlockState state = player.world.getBlockState(blockPos).getActualState(player.world, blockPos);
                    Block block = state.getBlock();

                    int playerEnergy = ElectricItemUtils.getPlayerEnergy(player);
                    Optional.ofNullable(itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(modeChanging -> {
                        if (modeChanging instanceof IModeChangingItem) {
                            for (ItemStack module : ((IModeChangingItem) modeChanging).getInstalledModulesOfType(IBlockBreakingModule.class)) {
                                if (Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null)).map(b -> {
                                    if (b instanceof IBlockBreakingModule) {
                                        if (((IBlockBreakingModule) b).canHarvestBlock(itemStack, state, player, blockPos, playerEnergy - energyUsage)) {
                                            return true;
                                        }
                                    }
                                    return false;
                                }).orElse(false)) {
                                    if (posIn == blockPos) { // center block
                                        harvested.set(true);
                                    }
                                    block.onPlayerDestroy(player.world, blockPos, state);
                                    block.harvestBlock(player.world, player, blockPos, state, player.world.getTileEntity(blockPos), player.getHeldItemMainhand());

                                    ElectricItemUtils.drainPlayerEnergy(player,
                                            Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null)).map(m -> {
                                                if (m instanceof IBlockBreakingModule) {
                                                    return ((IBlockBreakingModule) m).getEnergyUsage();
                                                }
                                                return 0;
                                            }).orElse(0) + energyUsage);
                                    break;

                                }
                            }
                        }
                    });
                }
                return harvested.get();
            }

            @Override
            public int getEnergyUsage () {
                return (int) applyPropertyModifiers(Constants.ENERGY_CONSUMPTION);
            }
        }
    }
}