package com.github.lehjr.modularpowerarmor.item.tool;

import appeng.api.implementations.items.IAEWrench;
import buildcraft.api.tools.IToolWrench;
import cofh.api.item.IToolHammer;
import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.RegistryNames;
import com.github.lehjr.modularpowerarmor.basemod.config.CommonConfig;
import com.github.lehjr.modularpowerarmor.client.render.PowerFistSpecNBT;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.tool.RefinedStorageWirelessModule;
import com.github.lehjr.mpalib.capabilities.heat.HeatCapability;
import com.github.lehjr.mpalib.capabilities.heat.IHeatStorage;
import com.github.lehjr.mpalib.capabilities.heat.MuseHeatItemWrapper;
import com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.capabilities.inventory.modechanging.ModeChangingModularItem;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.MPALibRangedWrapper;
import com.github.lehjr.mpalib.capabilities.module.blockbreaking.IBlockBreakingModule;
import com.github.lehjr.mpalib.capabilities.module.miningenhancement.IMiningEnhancementModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.render.IHandHeldModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.google.common.collect.Multimap;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemProvider;
import crazypants.enderio.api.tool.ITool;
import forestry.api.arboriculture.IToolGrafter;
import mekanism.api.IMekWrench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Describes the modular power tool.
 *
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 10/26/16.
 */

@Optional.InterfaceList({
        @Optional.Interface(iface = "mekanism.api.IMekWrench", modid = "Mekanism", striprefs = true),
        @Optional.Interface(iface = "crazypants.enderio.api.tool.ITool", modid = "EnderIO", striprefs = true),
        @Optional.Interface(iface = "forestry.api.arboriculture.IToolGrafter", modid = "forestry", striprefs = true),
        @Optional.Interface(iface = "com.raoulvdberge.refinedstorage.api.network.item.INetworkItemProvider", modid = "refinedstorage", striprefs = true),
//        @Optional.Interface(iface = "mods.railcraft.api.core.items.IToolCrowbar", modid = "Railcraft", striprefs = true),
//        @Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.IMFRHammer", modid = "MineFactoryReloaded", striprefs = true),
        @Optional.Interface(iface = "cofh.api.item.IToolHammer", modid = "cofhcore", striprefs = true),
        @Optional.Interface(iface = "buildcraft.api.tools.IToolWrench", modid = "BuildCraft|Core", striprefs = true),
        @Optional.Interface(iface = "appeng.api.implementations.items.IAEWrench", modid = "appliedenergistics2", striprefs = true)
})
public class ItemPowerFist extends MPSItemElectricTool
        implements
        IToolGrafter,
        IToolHammer,
        INetworkItemProvider,
//        IToolCrowbar,
        IAEWrench,
        IToolWrench,
        ITool,
        IMekWrench {

    public ItemPowerFist(String regName) {
        super(0.0f, 0.0f, ToolMaterial.IRON); // FIXME
        this.setRegistryName(regName);
        this.setTranslationKey(new StringBuilder(Constants.MODID).append(".").append("powerFist").toString());
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setCreativeTab(MPAConfig.INSTANCE.mpsCreativeTab);
    }

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return oldStack.isItemEqual(newStack);
    }

    /**
     * FORGE: Overridden to allow custom tool effectiveness
     */
    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return 1.0f;
    }

    /**
     * returns the action that specifies what animation to play when the items
     * is being used
     */
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        EnumActionResult fallback = EnumActionResult.PASS;
        if (hand != EnumHand.MAIN_HAND) {
            return fallback;
        }
        ItemStack fist = player.getHeldItem(hand);
        return java.util.Optional.ofNullable(fist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(handler->{
            if(handler instanceof IModeChangingItem) {
                ItemStack module = ((IModeChangingItem) handler).getActiveModule();
                return java.util.Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null)).map(m-> {
                    if (m instanceof IRightClickModule) {
                        return ((IRightClickModule) m).onItemUse(fist, player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                    }
                    return fallback;
                }).orElse(fallback);
            }
            return fallback;
        }).orElse(fallback);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        final EnumActionResult fallback = EnumActionResult.PASS;
        if (hand != EnumHand.MAIN_HAND) {
            return fallback;
        }
        ItemStack fist = player.getHeldItem(hand);
        return java.util.Optional.ofNullable(fist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(handler->{
            if(handler instanceof IModeChangingItem) {
                ItemStack module = ((IModeChangingItem) handler).getActiveModule();
                return java.util.Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null)).map(m-> {
                    if (m instanceof IRightClickModule) {
                        return ((IRightClickModule) m).onItemUseFirst(fist, player, world, pos, side, hitX, hitY, hitZ, hand);
                    }
                    return fallback;
                }).orElse(fallback);
            }
            return fallback;
        }).orElse(fallback);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        java.util.Optional.ofNullable(stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(handler->{
            if(handler instanceof IModeChangingItem) {
                ItemStack module = ((IModeChangingItem) handler).getActiveModule();
                java.util.Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null)).ifPresent(m-> {
                    if (m instanceof IRightClickModule) {
                        ((IRightClickModule) m).onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
                    }
                });
            }
        });
    }

    /**
     * Called when the right click button is pressed
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack fist = playerIn.getHeldItem(handIn);
        final ActionResult<ItemStack> fallback = new ActionResult<>(EnumActionResult.PASS, fist);
        if (handIn != EnumHand.MAIN_HAND)
            return fallback;

        return java.util.Optional.ofNullable(fist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(handler-> {
            if(handler instanceof IModeChangingItem) {
                return java.util.Optional.ofNullable(((IModeChangingItem) handler).getActiveModule().
                        getCapability(PowerModuleCapability.POWER_MODULE, null)).map(rc->
                        rc instanceof IRightClickModule ? ((IRightClickModule) rc).onItemRightClick(fist, world, playerIn, handIn) : fallback).orElse(fallback);
            }
            return fallback;
        }).orElse(fallback);
    }

    /**
     * Called when a block is destroyed using this tool.
     * <p/>
     * Returns: Whether to increment player use stats with this item
     */
    @Override
    public boolean onBlockDestroyed(ItemStack fist, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (entityLiving instanceof EntityPlayer) {
            int playerEnergy = ElectricItemUtils.getPlayerEnergy((EntityPlayer) entityLiving);

            java.util.Optional.ofNullable(fist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(handler -> {
                if (handler instanceof IModeChangingItem) {
                    for (ItemStack module : ((IModeChangingItem) handler).getInstalledModulesOfType(IBlockBreakingModule.class)) {
                        if (java.util.Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null))
                                .map(rc -> rc instanceof IBlockBreakingModule ? ((IBlockBreakingModule) rc)
                                        .onBlockDestroyed(fist, worldIn, state, pos, entityLiving, playerEnergy) : false).orElse(false)) {
                            return true;
                        }
                    }
                }
                return true;
            });
        }
        return true;
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
        super.onBlockStartBreak(itemstack, pos, player);
        return java.util.Optional.ofNullable(itemstack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
            if(iItemHandler instanceof IModeChangingItem) {
                ItemStack module = ((IModeChangingItem) iItemHandler).getActiveModule();
                return java.util.Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null)).map(pm->{
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
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack itemStack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, itemStack);
        if (slot == EntityEquipmentSlot.MAINHAND) {
            ItemStack module = java.util.Optional.ofNullable(itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
                if (iItemHandler instanceof IModeChangingItem) {
                    return ((IModeChangingItem) iItemHandler).getOnlineModuleOrEmpty(new ResourceLocation(RegistryNames.MODULE_MELEE_ASSIST__REGNAME));
                }
                return ItemStack.EMPTY;
            }).orElse(ItemStack.EMPTY);

            Double punchDamage = java.util.Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null)).map(
                    pm-> pm.applyPropertyModifiers(ModuleConstants.PUNCH_DAMAGE)).orElse(0D);

            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", punchDamage, 0));
        }
        return multimap;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    /**
     * Enchantments -----------------------------------------------------------------------
     */
    // TODO: for enchantment modules
    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @Override
    public int getItemEnchantability() {
        return 0; // :P
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
        return super.getHarvestLevel(stack, toolClass, player, blockState);
    }
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    /**
     * Return the id for this tool's material.
     */
    @Override
    public String getToolMaterialName() {
        return this.toolMaterial.toString();
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 72000;
    }

    @Optional.Method(modid = "forestry")
    public float getSaplingModifier(ItemStack itemStack, World world, EntityPlayer player, BlockPos pos) {

        ItemStack module = java.util.Optional.ofNullable(itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
            if (iItemHandler instanceof IModeChangingItem) {
                return ((IModeChangingItem) iItemHandler).getOnlineModuleOrEmpty(new ResourceLocation(RegistryNames.MODULE_GRAFTER__REGNAME));
            }
            return ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY);

        return java.util.Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null)).map(pm->{
            ElectricItemUtils.drainPlayerEnergy(player, (int) pm.applyPropertyModifiers(ModuleConstants.GRAFTER_ENERGY_CONSUMPTION));
            HeatUtils.heatPlayer(player, pm.applyPropertyModifiers(ModuleConstants.GRAFTER_HEAT_GENERATION));
            return 100.0f;
        }).orElse(0F);
    }

    // The Item/ItemTool version doesn't give us the player, so we can't override that.
    public boolean canHarvestBlock(ItemStack stack, IBlockState state, EntityPlayer player, BlockPos pos, int playerEnergy) {
        if (state.getMaterial().isToolNotRequired())
            return true;
        for (IPowerModule module : ModuleManager.INSTANCE.getModulesOfType(IBlockBreakingModule.class)) {
            if (ModuleManager.INSTANCE.itemHasActiveModule(stack, module.getDataName()) && ((IBlockBreakingModule) module).canHarvestBlock(stack, state, player, pos, playerEnergy)) {
                return true;
            }
        }
        return false;
    }

















    /* TE Crescent Hammer */
    @Override
    public boolean isUsable(ItemStack itemStack, EntityLivingBase entityLivingBase, Entity entity) {
        return entityLivingBase instanceof EntityPlayer && this.getActiveMode(itemStack).equals(ModuleConstants.MODULE_OMNI_WRENCH__DATANAME);
    }

    /* TE Crescent Hammer */
    @Override
    public boolean isUsable(ItemStack itemStack, EntityLivingBase entityLivingBase, BlockPos blockPos) {
        return entityLivingBase instanceof EntityPlayer && this.getActiveMode(itemStack).equals(ModuleConstants.MODULE_OMNI_WRENCH__DATANAME);
    }

    /* TE Crescent Hammer */
    @Override
    public void toolUsed(ItemStack itemStack, EntityLivingBase entityLivingBase, Entity entity) {

    }

    /* TE Crescent Hammer */
    @Override
    public void toolUsed(ItemStack itemStack, EntityLivingBase entityLivingBase, BlockPos blockPos) {

    }
//
//    /* Railcraft Crowbar */
//    @Override
//    public boolean canWhack(EntityPlayer entityPlayer, EnumHand enumHand, ItemStack itemStack, BlockPos blockPos) {
//        return this.getActiveMode(itemStack).equals(OmniWrenchModule.MODULE_OMNI_WRENCH);
//    }
//
//    /* Railcraft Crowbar */
//    @Override
//    public boolean canLink(EntityPlayer entityPlayer, EnumHand enumHand, ItemStack itemStack, EntityMinecart entityMinecart) {
//        return this.getActiveMode(itemStack).equals(OmniWrenchModule.MODULE_OMNI_WRENCH);
//    }
//
//    /* Railcraft Crowbar */
//    @Override
//    public boolean canBoost(EntityPlayer entityPlayer, EnumHand enumHand, ItemStack itemStack, EntityMinecart entityMinecart) {
//        return this.getActiveMode(itemStack).equals(OmniWrenchModule.MODULE_OMNI_WRENCH);
//    }
//
//    /* Railcraft Crowbar */
//    @Override
//    public void onLink(EntityPlayer entityPlayer, EnumHand enumHand, ItemStack itemStack, EntityMinecart entityMinecart) {
//
//    }
//
//    /* Railcraft Crowbar */
//    @Override
//    public void onWhack(EntityPlayer entityPlayer, EnumHand enumHand, ItemStack itemStack, BlockPos blockPos) {
//
//    }
//
//    /* Railcraft Crowbar */
//    @Override
//    public void onBoost(EntityPlayer entityPlayer, EnumHand enumHand, ItemStack itemStack, EntityMinecart entityMinecart) {
//
//    }

    /* AE wrench */
    @Override
    public boolean canWrench(ItemStack itemStack, EntityPlayer entityPlayer, BlockPos blockPos) {
        return this.getActiveMode(itemStack).equals(ModuleConstants.MODULE_OMNI_WRENCH__DATANAME);
    }

    /* Buildcraft Wrench */
    @Override
    public void wrenchUsed(EntityPlayer entityPlayer, EnumHand enumHand, ItemStack itemStack, RayTraceResult rayTraceResult) {

    }

    /* Buildcraft Wrench */
    @Override
    public boolean canWrench(EntityPlayer entityPlayer, EnumHand enumHand, ItemStack itemStack, RayTraceResult rayTraceResult) {
        return this.getActiveMode(entityPlayer.getHeldItem(enumHand)).equals(ModuleConstants.MODULE_OMNI_WRENCH__DATANAME);
    }

    /* EnderIO Tool */
    @Override
    public void used(@Nonnull EnumHand enumHand, @Nonnull EntityPlayer entityPlayer, @Nonnull BlockPos blockPos) {

    }

    /* EnderIO Tool */
    @Override
    public boolean canUse(@Nonnull EnumHand enumHand, @Nonnull EntityPlayer entityPlayer, @Nonnull BlockPos blockPos) {
        return this.getActiveMode(entityPlayer.getHeldItem(enumHand)).equals(ModuleConstants.MODULE_OMNI_WRENCH__DATANAME);
    }

    /* EnderIO Tool */
    @Override
    public boolean shouldHideFacades(ItemStack itemStack, EntityPlayer entityPlayer) {
        return this.getActiveMode(itemStack).equals(ModuleConstants.MODULE_OMNI_WRENCH__DATANAME);
    }

    /* Mekanism Wrench */
    @Override
    public boolean canUseWrench(ItemStack itemStack, EntityPlayer entityPlayer, BlockPos blockPos) {
        return this.getActiveMode(itemStack).equals(ModuleConstants.MODULE_OMNI_WRENCH__DATANAME);
    }

    @Override
    public boolean canUseWrench(EntityPlayer player, EnumHand hand, ItemStack itemStack, RayTraceResult rayTrace) {

        return java.util.Optional.ofNullable(itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
            if(iItemHandler instanceof IModeChangingItem) {
                ItemStack module = ((IModeChangingItem) iItemHandler).getActiveModule();
                return java.util.Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null)).map(pm->{


                    return this.getActiveMode(itemStack).equals(ModuleConstants.MODULE_OMNI_WRENCH__DATANAME);
                }

                @Override
                @Nonnull
                @Optional.Method(modid = "refinedstorage")
                public INetworkItem provide(INetworkItemHandler handler, EntityPlayer player, ItemStack stack) {
                    return RefinedStorageWirelessModule.provide(handler, player, stack);
                }





                @Override
                public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
                    return false;
                }





//    /**
//     * FORGE: Overridden to allow custom tool effectiveness
//     */
//    @Override
//    public float getDestroySpeed(ItemStack itemStack, BlockState state) {
//        System.out.println("material requires tool: " + (state.getHarvestTool() != null ? state.getHarvestTool().getName() : "none"));
//        return 50.0F;
//    }







                /**
                 * Current implementations of this method in child classes do not use the
                 * entry argument beside stack. They just raise the damage on the stack.
                 */
                @Override
                public boolean hitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase attacker) {
//        if (ModuleManager.INSTANCE.itemHasActiveModule(stack, MPSModuleConstants.MODULE_OMNI_WRENCH__DATANAME)) {
//            target.rotationYaw += 90.0f;
//            target.rotationYaw %= 360.0f;
//        }
                    if (attacker instanceof EntityPlayer) {
                        java.util.Optional.ofNullable(itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(iItemHandler -> {
                            if (iItemHandler instanceof IModeChangingItem) {
                                ItemStack module = ((IModeChangingItem) iItemHandler).getOnlineModuleOrEmpty(new ResourceLocation(RegistryNames.MODULE_MELEE_ASSIST__REGNAME));
                                java.util.Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null)).ifPresent(pm->{
                                    if (pm instanceof IModeChangingItem) {
                                        EntityPlayer player = (EntityPlayer) attacker;
                                        double drain = pm.applyPropertyModifiers(Constants.PUNCH_ENERGY);
                                        if (ElectricItemUtils.getPlayerEnergy(player) > drain) {
                                            ElectricItemUtils.drainPlayerEnergy(player, (int) drain);
                                            double damage = pm.applyPropertyModifiers(Constants.PUNCH_DAMAGE);
                                            double knockback = pm.applyPropertyModifiers(Constants.PUNCH_KNOCKBACK);
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



                @Override
                public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
                    System.out.println("doing something here");

                    return false;
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
                public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
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
                        this.energyStorage = this.modeChangingItem.getStackInSlot(0).getCapability(CapabilityEnergy.ENERGY, null).orElse(new EmptyEnergyWrapper());
                        this.heatStorage = new MuseHeatItemWrapper(fist, maxHeat);
                        this.modelSpec = new PowerFistSpecNBT(fist);
                    }

                    @Override
                    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                            return true;
                        }

                        if (capability == HeatCapability.HEAT) {
                            return true;
                        }

                        if (capability == ModelSpecNBTCapability.RENDER) {
                            return true;
                        }

                        if (capability == CapabilityEnergy.ENERGY) {
                            return true;
                        }
                        return false;
                    }

                    @Nullable
                    @Override
                    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                            modeChangingItem.updateFromNBT();
                            return (T) modeChangingItem;
                        }

                        if (capability == HeatCapability.HEAT) {
                            heatStorage.updateFromNBT();
                            return (T) heatStorage;
                        }

                        if (capability == ModelSpecNBTCapability.RENDER) {
                            return (T) modelSpec;
                        }

                        if (capability == CapabilityEnergy.ENERGY) {
                            return (T) energyStorage;
                        }
                        return null;
                    }

                    class EmptyEnergyWrapper extends EnergyStorage {
                        public EmptyEnergyWrapper() {
                            super(0);
                        }
                    }
                }

                /** Durability bar for showing energy level ------------------------------------------------------------------ */
                @Override
                public boolean showDurabilityBar(final ItemStack stack) {
                    return java.util.Optional.ofNullable(stack.getCapability(CapabilityEnergy.ENERGY, null))
                            .map( energyCap-> energyCap.getMaxEnergyStored() > 0).orElse(false);
                }

                @Override
                public double getDurabilityForDisplay(final ItemStack stack) {
                    return java.util.Optional.ofNullable(stack.getCapability(CapabilityEnergy.ENERGY, null))
                            .map( energyCap-> 1 - energyCap.getEnergyStored() / (double) energyCap.getMaxEnergyStored()).orElse(1D);
                }
            }