package net.machinemuse.powersuits.item.tool;

import net.machinemuse.numina.capabilities.heat.HeatCapability;
import net.machinemuse.numina.capabilities.heat.IHeatStorage;
import net.machinemuse.numina.capabilities.heat.MuseHeatItemWrapper;
import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.numina.capabilities.inventory.modechanging.ModeChangingModularItem;
import net.machinemuse.numina.capabilities.inventory.modularitem.MuseRangedWrapper;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.rightclick.IRightClickModule;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickCapability;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ItemPowerFist extends AbstractElectricTool {
    public ItemPowerFist(String regName) {
        setRegistryName(regName);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }


//    @Override // TODO?
//    public IItemTier getTier() {
//        return super.getTier();
//    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new PowerToolCap(stack);
    }

    class PowerToolCap implements ICapabilityProvider {
        ItemStack fist;
        IModeChangingItem modeChangingItem;
        IEnergyStorage energyStorage;
        IHeatStorage heatStorage;
        double maxHeat = CommonConfig.baseMaxHeatPowerFist();

        public PowerToolCap(@Nonnull ItemStack fist) {
            this.fist = fist;
            this.modeChangingItem = new ModeChangingModularItem(fist, 40)  {{
                /*
                 * Limit only Armor, Energy Storage and Energy Generation
                 *
                 * This cuts down on overhead for accessing the most commonly used values
                 */
                Map<EnumModuleCategory, MuseRangedWrapper> rangedWrapperMap = new HashMap<>();
                rangedWrapperMap.put(EnumModuleCategory.ENERGY_STORAGE, new MuseRangedWrapper(this, 0, 1));
                rangedWrapperMap.put(EnumModuleCategory.NONE, new MuseRangedWrapper(this, 1, this.getSlots() - 1));
                this.setRangedWrapperMap(rangedWrapperMap);
            }};
            this.energyStorage = this.modeChangingItem.getStackInSlot(0).getCapability(CapabilityEnergy.ENERGY).orElse(new EmptyEnergyWrapper());
            this.heatStorage = new MuseHeatItemWrapper(fist, maxHeat);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                modeChangingItem.updateFromNBT();
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> modeChangingItem));
            }
            if (cap == HeatCapability.HEAT) {
                ((MuseHeatItemWrapper) heatStorage).updateFromNBT();
            }
            return CapabilityEnergy.ENERGY.orEmpty(cap, LazyOptional.of(() ->
                    this.modeChangingItem.getStackInSlot(0).getCapability(CapabilityEnergy.ENERGY).orElse(new EmptyEnergyWrapper())));
        }

        class EmptyEnergyWrapper extends EnergyStorage {
            public EmptyEnergyWrapper() {
                super(0);
            }
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        final ActionResultType fallback = ActionResultType.PASS;

        final Hand hand = context.getHand();
        if (hand != Hand.MAIN_HAND)
            return fallback;

        final ItemStack fist = context.getItem();
        return fist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(handler->{
            if(handler instanceof IModeChangingItem) {
                ItemStack module = ((IModeChangingItem) handler).getActiveModule();
                return module.getCapability(RightClickCapability.RIGHT_CLICK).map(m-> m.onItemUse(context)).orElse(fallback);
            }
            return fallback;
        }).orElse(fallback);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler->{
            if(handler instanceof IModeChangingItem) {
                ItemStack module = ((IModeChangingItem) handler).getActiveModule();

                System.out.println("module: " + module);

                module.getCapability(RightClickCapability.RIGHT_CLICK).ifPresent(m-> m.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft));
            }
        });
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerIn, Hand handIn) {
        ItemStack fist = playerIn.getHeldItem(handIn);
        final ActionResult<ItemStack> fallack = new ActionResult<>(ActionResultType.PASS, fist);
        if (handIn != Hand.MAIN_HAND)
            return fallack;

        ItemStack module = fist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map( handler-> {
            if(handler instanceof IModeChangingItem) {
                return ((IModeChangingItem) handler).getActiveModule();
            }
            return ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY);

        ActionResult<ItemStack> test = module.getCapability(RightClickCapability.RIGHT_CLICK).map(rc-> rc.onItemRightClick(fist, world, playerIn, handIn)).orElse(fallack);

        System.out.println("test: " + test.getType().name());

        return test;
    }

    @Override
    public boolean showDurabilityBar(final ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(final ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map( energyCap-> 1 - energyCap.getEnergyStored() / (double) energyCap.getMaxEnergyStored()).orElse(1D);
    }
}