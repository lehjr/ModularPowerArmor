package com.github.lehjr.modularpowerarmor.block;

import com.github.lehjr.modularpowerarmor.container.providers.TinkerContainerProvider;
import com.github.lehjr.modularpowerarmor.tileentity.TinkerTableTileEntity;
import com.github.lehjr.mpalib.client.sound.SoundDictionary;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class BlockTinkerTable extends HorizontalBlock implements IWaterLoggable {






    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        player.playSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1.0F, 1.0F);

//        if(!worldIn.isRemote) {
//            NetworkHooks.openGui((ServerPlayerEntity) player,
//                    new TinkerContainerProvider(0), (buffer) -> buffer.writeInt(0));
//        }

        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            player.openContainer(state.getContainer(worldIn, pos));
//            player.addStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return ActionResultType.SUCCESS;
        }

//        if (worldIn.isRemote()) {
////        Musique.playClientSound(, 1);
//            Minecraft.getInstance().enqueue(() -> Minecraft.getInstance().displayGuiScreen(new TestGui(new TranslationTextComponent("gui.tinkertable"))));
////}
//        }
//        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Nullable
    @Override
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return new TinkerContainerProvider(0);


//        return new SimpleNamedContainerProvider((windowID, playerInventory, playerEntity) -> {
//            return new WorkbenchContainer(windowID, playerInventory, IWorldPosCallable.of(worldIn, pos));
//        }, title);
    }








}