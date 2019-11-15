package com.github.lehjr.modularpowerarmor.item.module.miningenhancement;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.miningenhancement.IMiningEnhancementModule;
import com.github.lehjr.mpalib.capabilities.module.miningenhancement.MiningEnhancement;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Mekanism Atomic Disassembler module
 */
public class MADModule extends AbstractPowerModule {
    ItemStack emulatedTool = ItemStack.EMPTY;

    public MADModule(String regName) {
        super(regName);
        if (ModCompatibility.isMekanismLoaded()) {
            emulatedTool = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("mekanism", "atomicdisassembler")), 1);
        }
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
            this.miningEnhancement.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 100, "RF");
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

            /**
             * Called before a block is broken.  Return true to prevent default block harvesting.
             *
             * Note: In SMP, this is called on both client and server sides!
             *
             * @param itemstack The current ItemStack
             * @param pos Block's position in world
             * @param player The Player that is wielding the item
             * @return True to prevent harvesting, false to continue as normal
             */
            @Override
            public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
                // set mode for the device
                NBTTagCompound nbt = emulatedTool.getTagCompound();
                if (nbt == null) {
                    nbt = new NBTTagCompound();
                    NBTTagCompound nbt2 = new NBTTagCompound();
                    nbt2.setInteger("mode", 3);
                    nbt.setTag("mekData", nbt2);
                    emulatedTool.setTagCompound(nbt);
                }

                ElectricItemUtils.chargeItem(emulatedTool, 100000);
                // TODO: set tag manually?          //        System.out.println("emulated tool: " + emulatedTool.serializeNBT().toString());

//        {id:"mekanism:atomicdisassembler",Count:1b,tag:{mekData:{mode:3,energyStored:1000000.0d}},Damage:0s}

//        NBTTagCompound nbt2 = new NBTTagCompound();


// Fixme: todo in 1.13 when emulated tools are actually stored
                // charge the device for usage
//        ElectricItemUtils.chargeEmulatedToolFromPlayerEnergy(player, emulatedTool);
                return emulatedTool.getItem().onBlockStartBreak(emulatedTool, pos, player);
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(Constants.ENERGY_CONSUMPTION);
            }
        }
    }
}
