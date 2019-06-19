package net.machinemuse.powersuits.item.module;

import net.machinemuse.powersuits.basemod.MPSItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public abstract class ItemAbstractPowerModule extends Item {

    public ItemAbstractPowerModule(String regName) {
        this(regName, new Item.Properties()
                .maxStackSize(1)
                .group(MPSItems.INSTANCE.creativeTab)
                .defaultMaxDamage(-1)
                .setNoRepair());
    }

    public ItemAbstractPowerModule(String regName, Properties properties) {
        super(properties);
        setRegistryName(regName);
    }

    @Nullable
    @Override
    public abstract ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt);
}