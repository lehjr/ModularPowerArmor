package com.github.lehjr.modularpowerarmor.item.module.mining_enhancement;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IBlockBreakingModule;
import com.github.lehjr.mpalib.legacy.module.IMiningEnhancementModule;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.client.event.MuseIcon;
import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import com.github.lehjr.modularpowerarmor.utils.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Created by Eximius88 on 1/29/14.
 */
public class AOEPickUpgradeModule extends AbstractPowerModule implements IMiningEnhancementModule {
    public AOEPickUpgradeModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.solenoid, 1));
        //ModuleManager.INSTANCE.addInstallCost(getDataName(), new ItemStack(Item.diamond, 3));

        addBasePropertyDouble(ModuleConstants.AOE_ENERGY_CONSUMPTION, 500, "RF");
        addTradeoffPropertyDouble(ModuleConstants.DIAMETER, ModuleConstants.AOE_ENERGY_CONSUMPTION, 9500);
        addIntTradeoffProperty(ModuleConstants.DIAMETER, ModuleConstants.AOE_MINING_RADIUS, 5, "m", 2, 1);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemStack, BlockPos posIn, EntityPlayer player) {
        if (player.world.isRemote)
            return false; // fixme : check?

        RayTraceResult rayTraceResult = PlayerUtils.raytraceBlocks(player.world, player, true, 10);
        if (rayTraceResult == null)
            return false;

        int radius =  (int) (ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, ModuleConstants.AOE_MINING_RADIUS) -1)/2 ;

        if (radius == 0)
            return false;

        EnumFacing side = rayTraceResult.sideHit;
        Iterable<BlockPos> posList;
        switch(side) {
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
        int energyUsage = this.getEnergyUsage(itemStack);

        boolean harvested = false;
        for (BlockPos blockPos : posList) {
            IBlockState state = player.world.getBlockState(blockPos).getActualState(player.world, blockPos);
            Block block = state.getBlock();

            for (IPowerModule module : ModuleManager.INSTANCE.getModulesOfType(IBlockBreakingModule.class)) {
                int playerEnergy = ElectricItemUtils.getPlayerEnergy(player);

                if (ModuleManager.INSTANCE.itemHasActiveModule(itemStack, module.getDataName()) && ((IBlockBreakingModule) module).canHarvestBlock(itemStack, state, player, blockPos, playerEnergy - energyUsage)) {
                    if (block.removedByPlayer(state, player.world, blockPos, player, true)) {
                        if (posIn == blockPos) // center block
                            harvested = true;

                        block.onPlayerDestroy(player.world, blockPos, state);
                        block.harvestBlock(player.world, player, blockPos, state, player.world.getTileEntity(blockPos), player.getHeldItemMainhand());


//                    block.onBlockHarvested(player.world, blockPos, state, player);
//                    player.world.playEvent(null, 2001, blockPos, Block.getStateId(state));
//                    player.world.setBlockToAir(blockPos);
//                    block.breakBlock(player.world, blockPos, state);
//                    block.dropBlockAsItem(player.world, blockPos, state, 0);

                        ElectricItemUtils.drainPlayerEnergy(player, ((IBlockBreakingModule) module).getEnergyUsage(itemStack) + energyUsage);
                        break;
                    }
                }
            }
        }
        return harvested;
    }

    @Override
    public int getEnergyUsage(@Nonnull ItemStack itemStack) {
        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, ModuleConstants.AOE_ENERGY_CONSUMPTION);
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.MINING_ENHANCEMENT;
    }

    @Override
    public String getDataName() {
        return ModuleConstants.MODULE_AOE_PICK_UPGRADE__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.aoePickUpgrade;
    }
}