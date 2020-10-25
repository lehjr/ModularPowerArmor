package com.github.lehjr.modularpowerarmor.event;

import com.github.lehjr.modularpowerarmor.basemod.MPACreativeTab;
import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.modularpowerarmor.block.BlockLuxCapacitor;
import com.github.lehjr.modularpowerarmor.block.BlockTinkerTable;
import com.github.lehjr.modularpowerarmor.container.MPACraftingContainer;
import com.github.lehjr.modularpowerarmor.container.TinkerTableContainer;
import com.github.lehjr.modularpowerarmor.entity.LuxCapacitorEntity;
import com.github.lehjr.modularpowerarmor.entity.PlasmaBoltEntity;
import com.github.lehjr.modularpowerarmor.entity.SpinningBladeEntity;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorBoots;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorChestplate;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorHelmet;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorLeggings;
import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import com.github.lehjr.modularpowerarmor.item.module.armor.DiamondPlatingModule;
import com.github.lehjr.modularpowerarmor.item.module.armor.EnergyShieldModule;
import com.github.lehjr.modularpowerarmor.item.module.armor.IronPlatingModule;
import com.github.lehjr.modularpowerarmor.item.module.armor.LeatherPlatingModule;
import com.github.lehjr.modularpowerarmor.item.module.cosmetic.TransparentArmorModule;
import com.github.lehjr.modularpowerarmor.item.module.energy_generation.generation.AdvancedSolarGeneratorModule;
import com.github.lehjr.modularpowerarmor.item.module.energy_generation.generation.BasicSolarGeneratorModule;
import com.github.lehjr.modularpowerarmor.item.module.energy_generation.generation.ThermalGeneratorModule;
import com.github.lehjr.modularpowerarmor.item.module.energy_generation.storage.EnergyStorageModule;
import com.github.lehjr.modularpowerarmor.item.module.environmental.*;
import com.github.lehjr.modularpowerarmor.item.module.miningenhancement.*;
import com.github.lehjr.modularpowerarmor.item.module.movement.*;
import com.github.lehjr.modularpowerarmor.item.module.special.ClockModule;
import com.github.lehjr.modularpowerarmor.item.module.special.CompassModule;
import com.github.lehjr.modularpowerarmor.item.module.special.InvisibilityModule;
import com.github.lehjr.modularpowerarmor.item.module.special.MagnetModule;
import com.github.lehjr.modularpowerarmor.item.module.tool.*;
import com.github.lehjr.modularpowerarmor.item.module.vision.BinocularsModule;
import com.github.lehjr.modularpowerarmor.item.module.vision.NightVisionModule;
import com.github.lehjr.modularpowerarmor.item.module.weapon.*;
import com.github.lehjr.modularpowerarmor.item.tool.ItemPowerFist;
import com.github.lehjr.modularpowerarmor.tile_entity.TileEntityLuxCapacitor;
import com.github.lehjr.modularpowerarmor.tile_entity.TinkerTableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.github.lehjr.modularpowerarmor.basemod.MPAConstants.MOD_ID;
import static com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames.*;

//@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
//@Mod.EventBusSubscriber(modid = MPAConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum RegisterStuff {
    INSTANCE;

    public static final MPACreativeTab creativeTab = new MPACreativeTab();




    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileEntityType.Builder.create(() ->
                        new TileEntityLuxCapacitor(), MPAObjects.INSTANCE.luxCapacitor).build(null).setRegistryName(LUX_CAPACITOR_REG_NAME + "_tile"),
                TileEntityType.Builder.create(() ->
                        new TinkerTableTileEntity(), MPAObjects.INSTANCE.tinkerTable).build(null).setRegistryName(TINKER_TABLE_REG_NAME + "_tile")
        );
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityType<?>> event ){
        event.getRegistry().registerAll(

//                EntityType.Builder.<BoltEntity>create(BoltEntity::new, EntityClassification.MISC)
//                        .setCustomClientFactory((spawnEntity, world) -> MPAObjects.BOLT_ENTITY_TYPE.create(world))
//                        .build(MOD_ID +":bolt").setRegistryName(MOD_ID +":bolt")
        );
    }

    @SubscribeEvent
    public void registerContainerTypes(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().registerAll(

//                // MODE CHANGING CONTAINER TYPE
//                new ContainerType<>(ModeChangingContainer::new)
//                        .setRegistryName(MODID + ":mode_changing_container_type"),

                // Modular Item Container

//                // the IForgeContainerType only needed for extra data
//                // ModuleConfig
//                IForgeContainerType.create((windowId, playerInventory, data) -> {
//                    int typeIndex = data.readInt();
//                    return new TinkerTableContainer(windowId, playerInventory, typeIndex);
//                }).setRegistryName(MODID + ":module_config_container_type"),
//
//                // Keybinding
//                IForgeContainerType.create((windowId, playerInventory, data) -> {
//                    int typeIndex = data.readInt();
//                    return new TinkerTableContainer(windowId, playerInventory, typeIndex);
//                }).setRegistryName(MODID + ":table_key_config_container_type"),
//
//                // Cosmetic
//                IForgeContainerType.create((windowId, playerInventory, data) -> {
//                    int typeIndex = data.readInt();
//                    return new TinkerTableContainer(windowId, playerInventory, typeIndex);
//                }).setRegistryName(MODID + ":cosmetic_config_container_type"),


        );
    }
}
