//package com.github.lehjr.modularpowerarmor.common;
//
//import com.github.lehjr.modularpowerarmor.basemod.Constants;
//import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorBoots;
//import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorChestplate;
//import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorHelmet;
//import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorLeggings;
//import com.github.lehjr.modularpowerarmor.item.tool.ItemPowerFist;
//import com.github.lehjr.modularpowerarmor.block.BlockLuxCapacitor;
//import com.github.lehjr.modularpowerarmor.block.BlockTinkerTable;
//import com.github.lehjr.modularpowerarmor.fluid.BlockFluidLiquidNitrogen;
//import com.github.lehjr.modularpowerarmor.fluid.LiquidNitrogen;
//import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
//import net.minecraft.block.Block;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemBlock;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.event.RegistryEvent;
//import net.minecraftforge.fluids.FluidRegistry;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.fml.common.registry.GameRegistry;
//
///**
// * Created by Claire Semple on 9/9/2014.
// * <p>
// * Ported to Java by lehjr on 10/22/16.
// */
//@Mod.EventBusSubscriber(modid = Constants.MODID)
//public enum MPSItems {
//    INSTANCE;
//
//    // Armor --------------------------------------------------------------------------------------
//    public static final String powerArmorHelmetRegName = Constants.MODID + ":powerarmor_head";
//    public static final String powerArmorChestPlateRegName = Constants.MODID + ":powerarmor_torso";
//    public static final String powerArmorLeggingsRegName = Constants.MODID + ":powerarmor_legs";
//    public static final String powerArmorBootsRegName = Constants.MODID + ":powerarmor_feet";
//
//    @GameRegistry.ObjectHolder(powerArmorHelmetRegName)
//    public static final ItemPowerArmorHelmet powerArmorHead = null;
//    @GameRegistry.ObjectHolder(powerArmorChestPlateRegName)
//    public static final ItemPowerArmorChestplate powerArmorTorso = null;
//    @GameRegistry.ObjectHolder(powerArmorLeggingsRegName)
//    public static final ItemPowerArmorLeggings powerArmorLegs = null;
//    @GameRegistry.ObjectHolder(powerArmorBootsRegName)
//    public static final ItemPowerArmorBoots powerArmorFeet = null;
//
//    // HandHeld -----------------------------------------------------------------------------------
//    public static final String powerFistRegName = Constants.MODID + ":power_fist";
//
//    @GameRegistry.ObjectHolder(powerFistRegName)
//    public static final ItemPowerFist powerFist = null;
//
//    // Components ---------------------------------------------------------------------------------
//    public static final String componentsRegname = Constants.MODID + ":powerarmorcomponent";
//
//    @GameRegistry.ObjectHolder(componentsRegname)
//    public static final ItemComponent components = null;
//
//    // Blocks -------------------------------------------------------------------------------------
//    public static final String tinkerTableRegName = Constants.MODID + ":tinkertable";
//    public static final String luxCapaRegName = Constants.MODID + ":luxcapacitor";
//
//    @GameRegistry.ObjectHolder(tinkerTableRegName)
//    public static final BlockTinkerTable tinkerTable = null;
//
//    @GameRegistry.ObjectHolder(luxCapaRegName)
//    public static final BlockLuxCapacitor luxCapacitor = null;
//
//    // Fluid --------------------------------------------------------------------------------------
//    public static final LiquidNitrogen liquidNitrogen = new LiquidNitrogen();
//
//    public static final String blockLiquidNitrogenName = Constants.MODID + ":liquid_nitrogen";
//    @GameRegistry.ObjectHolder(blockLiquidNitrogenName)
//    public static final BlockFluidLiquidNitrogen blockLiquidNitrogen = null;
//
//    @SubscribeEvent
//    public static void regigisterItems(RegistryEvent.Register<Item> event) {
//        event.getRegistry().registerAll(
//                // Armor --------------------------------------------------------------------------------------
//                new ItemPowerArmorHelmet(powerArmorHelmetRegName),
//                new ItemPowerArmorChestplate(powerArmorChestPlateRegName),
//                new ItemPowerArmorLeggings(powerArmorLeggingsRegName),
//                new ItemPowerArmorBoots(powerArmorBootsRegName),
//
//                // HandHeld -----------------------------------------------------------------------------------
//                new ItemPowerFist(powerFistRegName),
//
//                // Components ---------------------------------------------------------------------------------
//                new ItemComponent(componentsRegname),
//
////                // ItemBlocks ---------------------------------------------------------------------------------
//                new ItemBlock(tinkerTable).setRegistryName(new ResourceLocation(tinkerTableRegName)),
//                new ItemBlock(luxCapacitor).setRegistryName(new ResourceLocation(luxCapaRegName))
//        );
//
//        ItemComponent temp = (ItemComponent) event.getRegistry().getValue(new ResourceLocation(componentsRegname));
//        if (temp != null)
//            temp.registerOres();
//    }
//
//    @SubscribeEvent
//    public static void initBlocks(RegistryEvent.Register<Block> event) {
//        event.getRegistry().register(new BlockTinkerTable( new ResourceLocation(tinkerTableRegName)));
//        event.getRegistry().register(new BlockLuxCapacitor(new ResourceLocation(luxCapaRegName)));
//
//        event.getRegistry().register(new BlockFluidLiquidNitrogen(new ResourceLocation(blockLiquidNitrogenName)));
//    }
//
//    static boolean alreadyRegistered = true;
//    public static void initFluids() {
//        if (!FluidRegistry.isFluidRegistered("liquidnitrogen") && !FluidRegistry.isFluidRegistered("liquid_nitrogen")) {
//            FluidRegistry.registerFluid(liquidNitrogen);
//            FluidRegistry.addBucketForFluid(liquidNitrogen);
//            alreadyRegistered = false;
//        }
//    }
//}
