package net.machinemuse.powersuits.item.tool;

import net.machinemuse.numina.capabilities.heat.CapabilityHeat;
import net.machinemuse.numina.capabilities.heat.IHeatStorage;
import net.machinemuse.numina.capabilities.heat.MuseHeatItemWrapper;
import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.numina.capabilities.inventory.modechanging.ModeChangingCapability;
import net.machinemuse.numina.capabilities.inventory.modechanging.ModeChangingModularItem;
import net.machinemuse.numina.capabilities.inventory.modularitem.ModularItemCapability;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ItemPowerFist extends AbstractElectricTool {
    public ItemPowerFist(String regName) {
        setRegistryName(regName);
    }

//    @Override // TODO?
//    public IItemTier getTier() {
//        return super.getTier();
//    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new PowerArmorCap(stack);
    }

    class PowerArmorCap implements ICapabilityProvider {
        ItemStack fist;
        IModeChangingItem modeChangingItem;
        IEnergyStorage energyStorage;
        IHeatStorage heatStorage;
        double maxHeat = MPSConfig.GENERAL_BASE_MAX_HEAT_CHEST != null ? MPSConfig.GENERAL_BASE_MAX_HEAT_CHEST.get() : 5.0;

        public PowerArmorCap(@Nonnull ItemStack armor) {
            this.fist = armor;
            this.modeChangingItem = new ModeChanging();
            this.energyStorage = this.modeChangingItem.getStackInSlot(0).getCapability(CapabilityEnergy.ENERGY).orElse(new EmptyEnergyWrapper());
            this.heatStorage = new MuseHeatItemWrapper(armor, maxHeat);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == ModeChangingCapability.MODE_CHANGING)
                return ModeChangingCapability.MODE_CHANGING.orEmpty(cap, LazyOptional.of(()-> modeChangingItem));
            if (cap == CapabilityEnergy.ENERGY)
                return CapabilityEnergy.ENERGY.orEmpty(cap, LazyOptional.of(()-> energyStorage));
            return CapabilityHeat.HEAT.orEmpty(cap, LazyOptional.of(()-> heatStorage));
        }

        class ModeChanging extends ModeChangingModularItem {
            public ModeChanging() {
                super(fist, 40);
                /*
                 * Limit only Armor, Energy Storage and Energy Generation
                 *
                 * This cuts down on overhead for accessing the most commonly used values
                 */
                Map<EnumModuleCategory, RangedWrapper> rangedWrapperMap = new HashMap<>();
                rangedWrapperMap.put(EnumModuleCategory.CATEGORY_ENERGY_STORAGE,new RangedWrapper(this, 0, 1));
                rangedWrapperMap.put(EnumModuleCategory.CATEGORY_NONE,new RangedWrapper(this, 1, this.getSlots()-1));
                this.setRangedWrapperMap(rangedWrapperMap);
            }
        }

        class EmptyEnergyWrapper extends EnergyStorage {
            public EmptyEnergyWrapper() {
                super(0);
            }
        }
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerIn, Hand handIn) {
        ItemStack fist = playerIn.getHeldItem(handIn);

        if (world.isRemote()) {
            fist.getCapability(ModeChangingCapability.MODE_CHANGING).ifPresent( m-> {

                System.out.println("active mode: " + m.getActiveMode());


                ItemStack battery = m.getStackInSlot(0);
                if (battery.isEmpty()) {
                    System.out.println("battery is empty");


                    m.installModule(new ItemStack(MPSItems.INSTANCE.moduleBatteryUltimate));
                } else {
                    System.out.println("battery NBT: " + battery.serializeNBT());

                    battery.getCapability(CapabilityEnergy.ENERGY).ifPresent(e ->
                            e.receiveEnergy(e.getMaxEnergyStored() - e.getEnergyStored(), false));
                }

                if (m.getStackInSlot(1).isEmpty())
                    m.installModule(new ItemStack(MPSItems.INSTANCE.hoe));




            });

        }

        System.out.println("energy: " + fist.getCapability(CapabilityEnergy.ENERGY).map(e->e.getEnergyStored()).orElse(0));
        System.out.println("Maxenergy: " + fist.getCapability(CapabilityEnergy.ENERGY).map(e->e.getMaxEnergyStored()).orElse(0));




        return new ActionResult<>(ActionResultType.PASS, playerIn.getHeldItem(handIn));
    }
}