package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.basemod.ModularPowerArmor;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.math.MathUtils;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.common.capabilities.CapabilityScanResultProvider;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.init.Items;
import li.cil.scannable.common.inventory.ItemHandlerScanner;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScannerModule extends AbstractPowerModule {
    static final ResourceLocation scannerCharge = new ResourceLocation("scannable", "scanner_charge");
    static final ResourceLocation scanner_activate = new ResourceLocation("scannable", "scanner_activate");

    public ScannerModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;
        ItemHandlerScanner itemHandler = null;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, MPAConfig.moduleConfig);

            if (ModCompatibility.isScannableLoaded()) {
                this.itemHandler = new ItemHandlerScanner(module);
            }
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return true;
            }
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && ModCompatibility.isScannableLoaded()) {
                return true;
            }
            return false;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                ticker.updateFromNBT();
                return (T) ticker;
            }

            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && ModCompatibility.isScannableLoaded()) {
                itemHandler.updateFromNBT();
                return (T) itemHandler;
            }
            return null;
        }

        class Ticker extends PlayerTickModule implements IRightClickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config, true);
            }

            @Override
            public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
                if (player.getEntityWorld().isRemote) {
                    ScanManager.INSTANCE.updateScan(player, false);
                }
            }

            @Override
            public void onPlayerStoppedUsing(ItemStack itemStack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
                if (!(entityLiving instanceof EntityPlayer || !ModCompatibility.isScannableLoaded())) {
                    return;
                }

                final EntityPlayer player = (EntityPlayer) entityLiving;
                int chargeTicks = (int) MathUtils.clampDouble(itemStack.getMaxItemUseDuration() - timeLeft, 10, 40);

                if (worldIn.isRemote) {
                    // stop sound whether inturrupted or not
                    Musique.stopPlayerSound(player, scannerCharge);

                    // cancel scan if not scanner charge cycle interrupted
                    if (chargeTicks < 40) {
                        ScanManager.INSTANCE.cancelScan();
                    }
                }

                final List<ItemStack> modules = new ArrayList<>();
                // check for installed modules
                if (!collectModules(itemStack, modules)) {
                    return;
                }

                if (worldIn.isRemote) {

                    // if charge cycle completed
                    if (chargeTicks == 40) {

                        // check if player has enough energy
                        if (tryConsumeEnergy((EntityPlayer) entityLiving, modules, false)) {

                            // actually run the scan
                            ScanManager.INSTANCE.updateScan(entityLiving, true);
                            Musique.playerSound((EntityPlayer) entityLiving, scanner_activate, SoundCategory.PLAYERS, 1F, 1F, false);
                        } else {
                            // cancel scan if not enough power
                            ScanManager.INSTANCE.cancelScan();
                        }
                    }
                }
                player.getCooldownTracker().setCooldown(itemStack.getItem(), chargeTicks);
            }

            /**
             * Everything below this line ported/copied from li/cil/scannable/common/item/ItemScanner --------------------------------------------------------------------------
             */

            /* If check if the player is in creative mode or if the player's energy is high enough to do a scan with the installed modules
             */
            private boolean tryConsumeEnergy(final EntityPlayer player, final List<ItemStack> modules, final boolean simulate) {
                if (!ModCompatibility.isScannableLoaded()) {
                    return false;
                }

                if (player.capabilities.isCreativeMode) {
                    return true;
                }

                int playerEnergy = ElectricItemUtils.getPlayerEnergy(player);
                if (playerEnergy == 0)
                    return false;

                int totalCost = 0;
                for (final ItemStack module : modules) {
                    totalCost += getModuleEnergyCost(player, module);
                }

                if (playerEnergy > totalCost) {
                    if (!simulate)
                        ElectricItemUtils.drainPlayerEnergy(player, totalCost);
                    return true;
                }
                return false;
            }

            /**
             * Calculates the energy cost of a given module using Scannable's values
             */
            int getModuleEnergyCost(final EntityPlayer player, final ItemStack module) {
                if (ModCompatibility.isScannableLoaded()) {
                    final ScanResultProvider provider = module.getCapability(CapabilityScanResultProvider.SCAN_RESULT_PROVIDER_CAPABILITY, null);
                    if (provider != null) {
                        return provider.getEnergyCost(player, module);
                    }

                    if (Items.isModuleRange(module)) {
                        return Settings.getEnergyCostModuleRange();
                    }
                }
                return 0;
            }

            /**
             * Aside from returning the boolean, this also populates the module list.
             * The boolean value indicates whether or not there is any module installed into the scanner that returns a scan result.
             * Some modules, like the range extender, do not return a scan result, but are still valid modules. So this is probably
             * the simplest way of doing this.
             */
            private boolean collectModules(final ItemStack stack, final List<ItemStack> modules) {
                return Optional.ofNullable(stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
                    boolean hasProvider = false;
                    if(itemHandler instanceof ItemHandlerScanner) {
                        final IItemHandler activeModules = ((ItemHandlerScanner)iItemHandler).getActiveModules();
                        for (int slot = 0; slot < activeModules.getSlots(); slot++) {
                            final ItemStack module = activeModules.getStackInSlot(slot);
                            if (module.isEmpty()) {
                                continue;
                            }

                            modules.add(module);
                            if (module.hasCapability(CapabilityScanResultProvider.SCAN_RESULT_PROVIDER_CAPABILITY, null)) {
                                hasProvider = true;
                            }
                        }
                    }
                    return hasProvider;
                }).orElse(false);
            }

            @Override
            public ActionResult onItemRightClick(@Nonnull ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
                if (!ModCompatibility.isScannableLoaded()){
                    return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
                }

                if (playerIn.isSneaking()) {
                    playerIn.openGui(ModularPowerArmor.getInstance(), 6, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
                } else {
                    final List<ItemStack> modules = new ArrayList<>();
                    if (!collectModules(itemStackIn, modules)) {
                        if (worldIn.isRemote) {
                            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentTranslation(Constants.MESSAGE_NO_SCAN_MODULES), Constants.CHAT_LINE_ID);
                        }
                        playerIn.getCooldownTracker().setCooldown(itemStackIn.getItem(), 10);
                        return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
                    }

                    if (!tryConsumeEnergy(playerIn, modules, true)) {
                        if (worldIn.isRemote) {
                            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentTranslation(Constants.MESSAGE_NOT_ENOUGH_ENERGY), Constants.CHAT_LINE_ID);
                        }
                        playerIn.getCooldownTracker().setCooldown(itemStackIn.getItem(), 10);
                        return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
                    } else
                        playerIn.setActiveHand(hand);
                    if (worldIn.isRemote) {
                        ScanManager.INSTANCE.beginScan(playerIn, modules);
                        Musique.playerSound(playerIn, scannerCharge, SoundCategory.PLAYERS, 2F, 1F, false);
                    }
                }
                return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
            }
        }
    }
}