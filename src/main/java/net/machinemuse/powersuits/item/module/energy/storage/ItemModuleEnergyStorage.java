package net.machinemuse.powersuits.item.module.energy.storage;

import net.machinemuse.numina.capabilities.energy.ForgeEnergyItemWrapper;
import net.machinemuse.numina.module.EnumModuleCategory;
import net.machinemuse.numina.module.EnumModuleTarget;
import net.machinemuse.powersuits.basemod.MPSItems;
import net.machinemuse.powersuits.item.module.ItemAbstractPowerModule;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class ItemModuleEnergyStorage extends ItemAbstractPowerModule {
    protected final int maxEnergy;
    protected final int maxTransfer;

    public ItemModuleEnergyStorage(int maxEnergy, int maxTransfer, String regName) {
        super(regName);
        this.maxEnergy = maxEnergy;
        this.maxTransfer = maxTransfer;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return null;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ForgeEnergyItemWrapper(stack, maxEnergy, maxTransfer);
    }


}
