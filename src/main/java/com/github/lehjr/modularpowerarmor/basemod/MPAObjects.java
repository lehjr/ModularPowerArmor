package com.github.lehjr.modularpowerarmor.basemod;


import com.github.lehjr.modularpowerarmor.block.LuxCapacitorBlock;
import com.github.lehjr.modularpowerarmor.block.WorkBenchBlock;
import com.github.lehjr.modularpowerarmor.tileentity.LuxCapacitorTileEntity;
import com.github.lehjr.modularpowerarmor.tileentity.WorkBenchTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MPAObjects {

    /**
     * Blocks ------------------------------------------------------------------------------------
     */
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MPAConstants.MOD_ID);

    public static final RegistryObject<WorkBenchBlock> WORKBENCH_BLOCK = BLOCKS.register(MPARegistryNames.WORKBENCH,
            () -> new WorkBenchBlock());

    public static final RegistryObject<LuxCapacitorBlock> LUX_CAPACITOR_BLOCK = BLOCKS.register(MPARegistryNames.LUX_CAPACITOR,
            () -> new LuxCapacitorBlock());


    /**
     * Tile Entity Types -------------------------------------------------------------------------
     */
    public static final DeferredRegister<TileEntityType<?>> TILE_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MPAConstants.MOD_ID);

    public static final RegistryObject<TileEntityType<WorkBenchTileEntity>> WORKBENCH_TILE_TYPE = TILE_TYPES.register(MPARegistryNames.WORKBENCH + "tile",
            () -> TileEntityType.Builder.create(WorkBenchTileEntity::new, WORKBENCH_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<LuxCapacitorTileEntity>> LUX_CAP_TILE_TYPE = TILE_TYPES.register(MPARegistryNames.LUX_CAPACITOR + "tile",
            () -> TileEntityType.Builder.create(LuxCapacitorTileEntity::new, LUX_CAPACITOR_BLOCK.get()).build(null));





}
