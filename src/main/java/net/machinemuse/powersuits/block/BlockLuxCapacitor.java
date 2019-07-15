package net.machinemuse.powersuits.block;

import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.tileentity.TileEntityLuxCapacitor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class BlockLuxCapacitor extends DirectionalBlock {
    public static final ModelProperty<Integer> COLOUR_PROP = new ModelProperty<>();
    public static final Colour defaultColor = new Colour(0.4D, 0.2D, 0.9D);

    protected static final VoxelShape LUXCAPACITOR_EAST_AABB = Block.makeCuboidShape(12, 1, 1, 16, 15, 15);
    protected static final VoxelShape LUXCAPACITOR_WEST_AABB = Block.makeCuboidShape(0, 1, 1, 4, 15, 15);
    protected static final VoxelShape LUXCAPACITOR_SOUTH_AABB = Block.makeCuboidShape(1, 1, 12, 15, 15, 16);
    protected static final VoxelShape LUXCAPACITOR_NORTH_AABB = Block.makeCuboidShape(1, 1, 0.0, 15, 15, 4);
    protected static final VoxelShape LUXCAPACITOR_UP_AABB = Block.makeCuboidShape(1, 12, 1, 15, 16.0, 15);
    protected static final VoxelShape LUXCAPACITOR_DOWN_AABB = Block.makeCuboidShape(1, 0.0, 1, 15, 4, 15);

    public BlockLuxCapacitor(String regName) {
        super(Block.Properties.create(Material.IRON)
                .hardnessAndResistance(0.05F, 10.0F)
                .sound(SoundType.METAL)
                .variableOpacity()
                .lightValue(15));
        setRegistryName(regName);
        setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.DOWN));
    }

    @Override
    public int getHarvestLevel(BlockState state) {
        return 0;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return ToolType.PICKAXE;
    }

    @Override
    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
        return state;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return NonNullList.create();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
    }

    @SuppressWarnings( "deprecation" )
    @Deprecated
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING)) {
            default:
            case DOWN:
                return LUXCAPACITOR_DOWN_AABB;
            case UP:
                return LUXCAPACITOR_UP_AABB;
            case NORTH:
                return LUXCAPACITOR_NORTH_AABB;
            case SOUTH:
                return LUXCAPACITOR_SOUTH_AABB;
            case WEST:
                return LUXCAPACITOR_WEST_AABB;
            case EAST:
                return LUXCAPACITOR_EAST_AABB;
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityLuxCapacitor();
    }
}
