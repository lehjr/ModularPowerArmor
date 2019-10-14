//package com.github.lehjr.modularpowerarmor.item.module.miningenhancement;
//
//import com.github.lehjr.mpalib.module.EnumModuleCategory;
//import com.github.lehjr.mpalib.module.EnumModuleTarget;
//import com.github.lehjr.mpalib.module.IMiningEnhancementModule;
//import com.github.lehjr.mpalib.module.IToggleableModule;
//import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.math.BlockPos;
//
//import javax.annotation.Nonnull;
//
////
//
// FIXME!! rewrite as an ore vein miner without Mekanism dependency
///**
// * Mekanism Atomic Disassembler module
// */
//public class ItemModuleMAD extends AbstractPowerModule implements IToggleableModule, IMiningEnhancementModule {
////    ItemStack emulatedTool = ItemStack.EMPTY;
//
//    //FIXME: need to create a proper storage location for all these emulated tools.
//
//    public ItemModuleMAD(String regName) {
//        super(regName, EnumModuleTarget.TOOLONLY, EnumModuleCategory.CATEGORY_MINING_ENHANCEMENT);
////        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(Iteminecraftomponent.solenoid, 1));
////        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(Iteminecraftomponent.controlCircuit, 1));
////        ModuleManager.INSTANCE.addInstallCost(getDataName(), new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("mekanism", "atomicdisassembler")), 1));
////        if (ModCompatibility.isMekanismLoaded()) {
////            emulatedTool = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("mekanism", "atomicdisassembler")), 1);
////        }
////
////        addBasePropertyDouble(MPSModuleConstants.ENERGY_CONSUMPTION, 100, "RF");
//    }
//
//    /**
//     * Called before a block is broken.  Return true to prevent default block harvesting.
//     *
//     * Note: In SMP, this is called on both client and server sides!
//     *
//     * @param itemstack The current ItemStack
//     * @param pos Block's position in world
//     * @param player The Player that is wielding the item
//     * @return True to prevent harvesting, false to continue as normal
//     */
//    @Override
//    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
////        // set mode for the device
////        CompoundNBT nbt = emulatedTool.getTagCompound();
////        if (nbt == null) {
////            nbt = new CompoundNBT();
////            CompoundNBT nbt2 = new CompoundNBT();
////            nbt2.setInteger("mode", 3);
////            nbt.setTag("mekData", nbt2);
////            emulatedTool.setTagCompound(nbt);
////        }
////
////        ElectricItemUtils.chargeItem(emulatedTool, 100000);
////        // TODO: set tag manually?          //        System.out.println("emulated tool: " + emulatedTool.serializeNBT().toString());
////
//////        {id:"mekanism:atomicdisassembler",Count:1b,tag:{mekData:{mode:3,energyStored:1000000.0d}},Damage:0s}
////
//////        CompoundNBT nbt2 = new CompoundNBT();
////
////
////// Fixme: todo in 1.13 when emulated tools are actually stored
////        // charge the device for usage
//////        ElectricItemUtils.chargeEmulatedToolFromPlayerEnergy(player, emulatedTool);
////        return emulatedTool.getItem().onBlockStartBreak(emulatedTool, pos, player);
//        return false;
//    }
//
//    @Override
//    public int getEnergyUsage() {
//
//        return 0;
////        return (int) moduleCap.applyPropertyModifiers(MPSModuleConstants.ENERGY_CONSUMPTION);
//    }
//}