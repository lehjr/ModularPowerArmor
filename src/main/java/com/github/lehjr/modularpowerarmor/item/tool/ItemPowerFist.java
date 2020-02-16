package com.github.lehjr.modularpowerarmor.item.tool;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;
import com.github.lehjr.modularpowerarmor.basemod.config.CommonConfig;
import com.github.lehjr.modularpowerarmor.render.PowerFistSpecNBT;
import com.github.lehjr.mpalib.capabilities.heat.HeatCapability;
import com.github.lehjr.mpalib.capabilities.heat.IHeatStorage;
import com.github.lehjr.mpalib.capabilities.heat.MuseHeatItemWrapper;
import com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.capabilities.inventory.modechanging.ModeChangingModularItem;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.MPALibRangedWrapper;
import com.github.lehjr.mpalib.capabilities.module.miningenhancement.IMiningEnhancementModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.render.IHandHeldModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReader;
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

public class ItemPowerFist extends AbstractElectricTool {
    public ItemPowerFist(String regName) {
        setRegistryName(regName);
//        this.addPropertyOverride(new ResourceLocation("pull"), (itemStack, world, livingEntity) -> {
//            if (livingEntity == null) {
//                return 0.0F;
//            } else {
//                return !(livingEntity.getActiveItemStack().getItem() instanceof BowItem) ? 0.0F : (float)(itemStack.getUseDuration() - livingEntity.getItemInUseCount()) / 20.0F;
//            }
//        });
//        this.addPropertyOverride(new ResourceLocation("firing"), (itemStack, world, livingEntity) -> {
//            return livingEntity != null && livingEntity.isHandActive() && livingEntity.getActiveItemStack() == itemStack ? 1.0F : 0.0F;
//        });
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

//    /**
//     * FORGE: Overridden to allow custom tool effectiveness
//     */
//    @Override
//    public float getDestroySpeed(ItemStack itemStack, BlockState state) {
//        System.out.println("material requires tool: " + (state.getHarvestTool() != null ? state.getHarvestTool().getName() : "none"));
//        return 50.0F;
//    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }





    /**
     * Current implementations of this method in child classes do not use the
     * entry argument beside stack. They just raise the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack itemStack, LivingEntity target, LivingEntity attacker) {
//        if (ModuleManager.INSTANCE.itemHasActiveModule(stack, MPSModuleConstants.MODULE_OMNI_WRENCH__DATANAME)) {
//            target.rotationYaw += 90.0f;
//            target.rotationYaw %= 360.0f;
//        }
        if (attacker instanceof PlayerEntity) {
            itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
                if (iItemHandler instanceof IModeChangingItem) {
                    ((IModeChangingItem) iItemHandler).getOnlineModuleOrEmpty(new ResourceLocation(MPARegistryNames.MODULE_MELEE_ASSIST__REGNAME))
                            .getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(pm->{
                        if (pm instanceof IModeChangingItem) {
                            PlayerEntity player = (PlayerEntity) attacker;
                            double drain = pm.applyPropertyModifiers(MPAConstants.PUNCH_ENERGY);
                            if (ElectricItemUtils.getPlayerEnergy(player) > drain) {
                                ElectricItemUtils.drainPlayerEnergy(player, (int) drain);
                                double damage = pm.applyPropertyModifiers(MPAConstants.PUNCH_DAMAGE);
                                double knockback = pm.applyPropertyModifiers(MPAConstants.PUNCH_KNOCKBACK);
                                DamageSource damageSource = DamageSource.causePlayerDamage(player);
                                if (target.attackEntityFrom(damageSource, (float) (int) damage)) {
                                    Vec3d lookVec = player.getLookVec();
                                    target.addVelocity(lookVec.x * knockback, Math.abs(lookVec.y + 0.2f) * knockback, lookVec.z * knockback);
                                }
                            }
                        }
                    });
                }
            });
        }
        return true;
    }



    //    @Override // TODO?
//    public IItemTier getTier() {
//        return super.getTier();
//    }


//    @Override
//    public boolean canPlayerBreakBlockWhileHolding(BlockState p_195938_1_, World p_195938_2_, BlockPos p_195938_3_, PlayerEntity p_195938_4_) {
//        System.out.println("doing something here");
//
//        return super.canPlayerBreakBlockWhileHolding(p_195938_1_, p_195938_2_, p_195938_3_, p_195938_4_);
//    }


//    @Override
//    public boolean onBlockDestroyed(ItemStack itemStack, World world, BlockState state, BlockPos pos, LivingEntity player) {
//        System.out.println("doing something here");
//
//        return super.onBlockDestroyed(itemStack, world, state, pos, player);
//    }

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
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
        super.onBlockStartBreak(itemstack, pos, player);
        return itemstack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(iItemHandler -> {
            if(iItemHandler instanceof IModeChangingItem) {
                return ((IModeChangingItem) iItemHandler).getActiveModule()
                        .getCapability(PowerModuleCapability.POWER_MODULE).map(pm->{
                            if(pm instanceof IMiningEnhancementModule) {
                                return ((IMiningEnhancementModule) pm).onBlockStartBreak(itemstack, pos, player);
                            }
                            return false;
                        }).orElse(false);
            }
            return false;
        }).orElse(false);
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
//        System.out.println("doing something here");

        return false;
    }

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
//        System.out.println("doing something here");

        return oldStack.isItemEqual(newStack);
    }

//    // Only fires on blocks that need a tool
//    @Override
//    public int getHarvestLevel(ItemStack itemStack, ToolType toolType, @Nullable PlayerEntity player, @Nullable BlockState state) {
//
//        System.out.println("super level pass: " + super.getHarvestLevel(itemStack, toolType, player, state));
//
//
//        int retVal = itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(iItemHandler -> {
//            if (iItemHandler instanceof IModeChangingItem) {
//                int highestVal = 0;
//                for (ItemStack module :  ((IModeChangingItem) iItemHandler).getInstalledModulesOfType(IBlockBreakingModule.class)) {
//                    int val = module.getCapability(PowerModuleCapability.POWER_MODULE).map(pm->{
//                        if (pm instanceof IBlockBreakingModule) {
//                            return ((IBlockBreakingModule) pm).getEmulatedTool().getHarvestLevel(toolType, player, state);
//                        }
//                        return 0;
//                    }).orElse(0);
//                    if (val > highestVal) {
//                        highestVal = val;
//                    }
//                }
//                return highestVal;
//            }
//            return 0;
//        }).orElse(0);
//
//
////        System.out.println("retVal: " + retVal);
////        System.out.println("itemstack: " + itemStack);
////        System.out.println("toolType: " + toolType);
////        System.out.println("player: " + player);
////        System.out.println("state.block: " + state.getBlock());
////        System.out.println("state.harvest level: " + state.getHarvestLevel());
////        System.out.println("state.harvest tool: " + state.getHarvestTool().getName());
////        System.out.println("state.getMaterial is Wood: " + state.getMaterial().equals(Material.WOOD));
////        System.out.println("state.getMaterial requires Tool: " + !state.getMaterial().isToolNotRequired());
//
////        return retVal;
//
//
//        return 100;
//    }

//    @Override
//    public boolean canHarvestBlock(BlockState p_150897_1_) {
//
//        System.out.println("canHarvestBlock: " + super.canHarvestBlock(p_150897_1_));
//        return super.canHarvestBlock(p_150897_1_);
//    }
//
//    @Override
//    public boolean canHarvestBlock(ItemStack stack, BlockState state) {
//        System.out.println("canHarvestBlock: " + super.canHarvestBlock(stack, state));
//
//        return super.canHarvestBlock(stack, state);
//    }

    // this might be what we need

//    @Override
//    public boolean canHarvestBlock(ItemStack itemStack, BlockState state) {
//        boolean retVal = itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(iItemHandler -> {
//                    if (iItemHandler instanceof IModeChangingItem) {
//                        for (ItemStack module :  ((IModeChangingItem) iItemHandler).getInstalledModulesOfType(IBlockBreakingModule.class)) {
//                            boolean val = module.getCapability(PowerModuleCapability.POWER_MODULE).map(pm->{
//                                if (pm instanceof IBlockBreakingModule) {
//                                    if (((IBlockBreakingModule) pm).getEmulatedTool().canHarvestBlock(state))
//                                        return true;
//                                }
//                                return false;
//                            }).orElse(false);
//
//                        }
//                        return false;
//                    }
//                    return false;
//                }).orElse(false);
//        System.out.println("retVal: " + retVal);
//        return retVal;
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
        IHandHeldModelSpecNBT modelSpec;
        double maxHeat = CommonConfig.baseMaxHeatPowerFist();

        public PowerToolCap(@Nonnull ItemStack fist) {
            this.fist = fist;
            this.modeChangingItem = new ModeChangingModularItem(fist, 40)  {{
                /*
                 * Limit only Armor, Energy Storage and Energy Generation
                 *
                 * This cuts down on overhead for accessing the most commonly used values
                 */
                Map<EnumModuleCategory, MPALibRangedWrapper> rangedWrapperMap = new HashMap<>();
                rangedWrapperMap.put(EnumModuleCategory.ENERGY_STORAGE, new MPALibRangedWrapper(this, 0, 1));
                rangedWrapperMap.put(EnumModuleCategory.NONE, new MPALibRangedWrapper(this, 1, this.getSlots() - 1));
                this.setRangedWrapperMap(rangedWrapperMap);
            }};
            this.energyStorage = this.modeChangingItem.getStackInSlot(0).getCapability(CapabilityEnergy.ENERGY).orElse(new EmptyEnergyWrapper());
            this.heatStorage = new MuseHeatItemWrapper(fist, maxHeat);
            this.modelSpec = new PowerFistSpecNBT(fist);
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
            if (cap == ModelSpecNBTCapability.RENDER) {
                return ModelSpecNBTCapability.RENDER.orEmpty(cap, LazyOptional.of(()->modelSpec));
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
                return module.getCapability(PowerModuleCapability.POWER_MODULE).map(m-> {
                    if (m instanceof IRightClickModule) {
                        return ((IRightClickModule) m).onItemUse(context);
                    }
                    return fallback;
                }).orElse(fallback);
            }
            return fallback;
        }).orElse(fallback);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack itemStack, ItemUseContext context) {
        final ActionResultType fallback = ActionResultType.PASS;

        final Hand hand = context.getHand();
        if (hand != Hand.MAIN_HAND)
            return fallback;

        final ItemStack fist = context.getItem();
        return fist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(handler->{
            if(handler instanceof IModeChangingItem) {
                ItemStack module = ((IModeChangingItem) handler).getActiveModule();
                return module.getCapability(PowerModuleCapability.POWER_MODULE).map(m-> {
                    if (m instanceof IRightClickModule) {
                        return ((IRightClickModule) m).onItemUseFirst(itemStack, context);
                    }
                    return fallback;
                }).orElse(fallback);
            }
            return fallback;
        }).orElse(fallback);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler->{
            if(handler instanceof IModeChangingItem) {
                ItemStack module = ((IModeChangingItem) handler).getActiveModule();
                module.getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(m-> {
                    if (m instanceof IRightClickModule) {
                        ((IRightClickModule) m).onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
                    }
                });
            }
        });
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerIn, Hand handIn) {
        ItemStack fist = playerIn.getHeldItem(handIn);
        final ActionResult<ItemStack> fallback = new ActionResult<>(ActionResultType.PASS, fist);
        if (handIn != Hand.MAIN_HAND)
            return fallback;

        return fist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map( handler-> {
            if(handler instanceof IModeChangingItem) {
                return ((IModeChangingItem) handler).getActiveModule().
                        getCapability(PowerModuleCapability.POWER_MODULE).map(rc->
                        rc instanceof IRightClickModule ? ((IRightClickModule) rc).onItemRightClick(fist, world, playerIn, handIn) : fallback).orElse(fallback);
            }
            return fallback;
        }).orElse(fallback);
    }

    /** Durability bar for showing energy level ------------------------------------------------------------------ */
    @Override
    public boolean showDurabilityBar(final ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map( energyCap-> energyCap.getMaxEnergyStored() > 0).orElse(false);
    }

    @Override
    public double getDurabilityForDisplay(final ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map( energyCap-> 1 - energyCap.getEnergyStored() / (double) energyCap.getMaxEnergyStored()).orElse(1D);
    }
}