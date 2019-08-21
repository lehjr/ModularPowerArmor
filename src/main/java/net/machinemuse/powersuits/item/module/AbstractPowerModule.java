package net.machinemuse.powersuits.item.module;

import net.machinemuse.powersuits.basemod.MPSModules;
import net.machinemuse.powersuits.event.RegisterStuff;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public abstract class AbstractPowerModule extends Item {

    public AbstractPowerModule(String regName) {
        this(regName, new Item.Properties()
                .maxStackSize(1)
                .group(RegisterStuff.INSTANCE.creativeTab)
                .defaultMaxDamage(-1)
                .setNoRepair());
    }

    public AbstractPowerModule(String regName, Properties properties) {
        super(properties);
        setRegistryName(regName);
        MPSModules.INSTANCE.addModule(new ResourceLocation(regName));
    }

    @Nullable
    @Override
    public abstract ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt);
}