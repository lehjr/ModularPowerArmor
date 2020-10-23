package com.github.lehjr.modularpowerarmor.basemod;


import com.github.lehjr.modularpowerarmor.block.LuxCapacitorBlock;
import com.github.lehjr.modularpowerarmor.block.WorkBenchBlock;
import com.github.lehjr.modularpowerarmor.entity.LuxCapacitorEntity;
import com.github.lehjr.modularpowerarmor.entity.PlasmaBoltEntity;
import com.github.lehjr.modularpowerarmor.entity.SpinningBladeEntity;
import com.github.lehjr.modularpowerarmor.item.armor.PowerArmorBoots;
import com.github.lehjr.modularpowerarmor.item.armor.PowerArmorChestplate;
import com.github.lehjr.modularpowerarmor.item.armor.PowerArmorHelmet;
import com.github.lehjr.modularpowerarmor.item.armor.PowerArmorLeggins;
import com.github.lehjr.modularpowerarmor.tileentity.LuxCapacitorTileEntity;
import com.github.lehjr.modularpowerarmor.tileentity.WorkBenchTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MPAObjects {
    public static final MPACreativeTab creativeTab = new MPACreativeTab();
    public static final Item.Properties singleStack = new Item.Properties()
            .maxStackSize(1)
                .group(MPAObjects.creativeTab)
                .defaultMaxDamage(-1)
                .setNoRepair();
    public static final Item.Properties fullStack = new Item.Properties()
            .group(MPAObjects.creativeTab)
            .defaultMaxDamage(-1)
            .setNoRepair();
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

    public static final RegistryObject<TileEntityType<WorkBenchTileEntity>> WORKBENCH_TILE_TYPE = TILE_TYPES.register(MPARegistryNames.WORKBENCH,
            () -> TileEntityType.Builder.create(WorkBenchTileEntity::new, WORKBENCH_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<LuxCapacitorTileEntity>> LUX_CAP_TILE_TYPE = TILE_TYPES.register(MPARegistryNames.LUX_CAPACITOR,
            () -> TileEntityType.Builder.create(LuxCapacitorTileEntity::new, LUX_CAPACITOR_BLOCK.get()).build(null));

    /**
     * Entity Types ------------------------------------------------------------------------------
     */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MPAConstants.MOD_ID);

    public static final RegistryObject<EntityType<LuxCapacitorEntity>> LUX_CAPACITOR_ENTITY_TYPE = ENTITY_TYPES.register(MPARegistryNames.LUX_CAPACITOR,
            ()-> EntityType.Builder.<LuxCapacitorEntity>create(LuxCapacitorEntity::new, EntityClassification.MISC)
                    .size(0.25F, 0.25F)
                    .build(new ResourceLocation(MPAConstants.MOD_ID, MPARegistryNames.LUX_CAPACITOR).toString()));

    public static final RegistryObject<EntityType<SpinningBladeEntity>> SPINNING_BLADE_ENTITY_TYPE = ENTITY_TYPES.register(MPARegistryNames.SPINNING_BLADE,
            ()-> EntityType.Builder.<SpinningBladeEntity>create(SpinningBladeEntity::new, EntityClassification.MISC)
                    .size(0.25F, 0.25F) // FIXME! check size
                    .build(new ResourceLocation(MPAConstants.MOD_ID, MPARegistryNames.SPINNING_BLADE).toString()));

    public static final RegistryObject<EntityType<PlasmaBoltEntity>> PLASMA_BOLT_ENTITY_TYPE = ENTITY_TYPES.register(MPARegistryNames.PLASMA_BOLT,
            ()-> EntityType.Builder.<PlasmaBoltEntity>create(PlasmaBoltEntity::new, EntityClassification.MISC)
                    .build(new ResourceLocation(MPAConstants.MOD_ID, MPARegistryNames.PLASMA_BOLT).toString()));


    // FIXME: bolt protectile



    /**
     * Items -------------------------------------------------------------------------------------
     */
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MPAConstants.MOD_ID);

    /* BlockItems --------------------------------------------------------------------------------- */
    public static final RegistryObject<Item> WORKBENCH_ITEM = ITEMS.register(MPARegistryNames.WORKBENCH,
            () -> new BlockItem(WORKBENCH_BLOCK.get(), fullStack));

    public static final RegistryObject<Item> LUX_CAPACITOR_ITEM = ITEMS.register(MPARegistryNames.LUX_CAPACITOR,
            () -> new BlockItem(WORKBENCH_BLOCK.get(), fullStack));


    /* Armor -------------------------------------------------------------------------------------- */
    public static final RegistryObject<Item> POWER_ARMOR_HELMET = ITEMS.register(MPARegistryNames.POWER_ARMOR_HELMET,
            () -> new PowerArmorHelmet());

    public static final RegistryObject<Item> POWER_ARMOR_CHESTPLATE = ITEMS.register(MPARegistryNames.POWER_ARMOR_CHESTPLATE,
            () -> new PowerArmorChestplate());

    public static final RegistryObject<Item> POWER_ARMOR_LEGGINGS = ITEMS.register(MPARegistryNames.POWER_ARMOR_LEGGINGS,
            () -> new PowerArmorLeggins());
    public static final RegistryObject<Item> POWER_ARMOR_BOOTS = ITEMS.register(MPARegistryNames.POWER_ARMOR_BOOTS,
            () -> new PowerArmorBoots());






}
