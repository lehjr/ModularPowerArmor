//package com.github.lehjr.modularpowerarmor.item.module.miningenhancement;
//
//import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
//import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
//import com.github.lehjr.mpalib.energy.ElectricItemUtils;
//import com.github.lehjr.mpalib.item.ItemUtils;
//import com.github.lehjr.mpalib.legacy.module.IMiningEnhancementModule;
//import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
//import com.github.lehjr.mpalib.misc.ModCompatibility;
//import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
//import com.github.lehjr.modularpowerarmor.client.event.MuseIcon;
//import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.math.BlockPos;
//
//import javax.annotation.Nonnull;
//
////
//
//
///**
// * Mekanism Atomic Disassembler module
// */
//public class MADModule extends AbstractPowerModule {
//    ItemStack emulatedTool = ItemStack.EMPTY;
//
//    //FIXME: need to create a proper storage location for all these emulated tools.
//
//    public MADModule(EnumModuleTarget moduleTargetIn) {
//        super(moduleTargetIn);
//        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.solenoid, 1));
//        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.controlCircuit, 1));
//        ModuleManager.INSTANCE.addInstallCost(getDataName(), new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("mekanism", "atomicdisassembler")), 1));
//        if (ModCompatibility.isMekanismLoaded()) {
//            emulatedTool = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("mekanism", "atomicdisassembler")), 1);
//        }
//
//        addBasePropertyDouble(ModuleConstants.ENERGY_CONSUMPTION, 100, "RF");
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
//    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
//        // set mode for the device
//        NBTTagCompound nbt = emulatedTool.getTagCompound();
//        if (nbt == null) {
//            nbt = new NBTTagCompound();
//            NBTTagCompound nbt2 = new NBTTagCompound();
//            nbt2.setInteger("mode", 3);
//            nbt.setTag("mekData", nbt2);
//            emulatedTool.setTagCompound(nbt);
//        }
//
//        ElectricItemUtils.chargeItem(emulatedTool, 100000);
//        // TODO: set tag manually?          //        System.out.println("emulated tool: " + emulatedTool.serializeNBT().toString());
//
////        {id:"mekanism:atomicdisassembler",Count:1b,tag:{mekData:{mode:3,energyStored:1000000.0d}},Damage:0s}
//
////        NBTTagCompound nbt2 = new NBTTagCompound();
//
//
//// Fixme: todo in 1.13 when emulated tools are actually stored
//        // charge the device for usage
////        ElectricItemUtils.chargeEmulatedToolFromPlayerEnergy(player, emulatedTool);
//        return emulatedTool.getItem().onBlockStartBreak(emulatedTool, pos, player);
//    }
//
//    @Override
//    public int getEnergyUsage(@Nonnull ItemStack itemStack) {
//        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, ModuleConstants.ENERGY_CONSUMPTION);
//    }
//
//    @Override
//    public TextureAtlasSprite getIcon(ItemStack item) {
//        return MuseIcon.madModule;
//    }
//
//    @Override
//    public String getDataName() {
//        return ModuleConstants.MODULE_MAD__REGNAME;
//    }
//}
