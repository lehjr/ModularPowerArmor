package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.modularpowerarmor.utils.modulehelpers.PersonalShrinkingModuleHelper;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by User: Korynkai
 * 5:41 PM 2014-11-19
 */

/*
    TODO: the mechanics have changed a bit. This module will require some reworking
 */
public class PersonalShrinkingModule extends AbstractPowerModule {
    private final ItemStack cpmPSD = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("cm2", "psd")), 1);

    public PersonalShrinkingModule(String regName) {
        super(regName);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("fluid", 4000);
        cpmPSD.setTagCompound(nbt);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, MPAConfig.moduleConfig);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                ticker.updateFromNBT();
                return (T) ticker;
            }
            return null;
        }

        class Ticker extends PlayerTickModule implements IRightClickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config, false);
            }

            @Override
            public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand handIn) {
                return new ActionResult(EnumActionResult.FAIL, itemStackIn);
            }

            @Override
            public EnumActionResult onItemUseFirst(ItemStack itemStackIn, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
                return cpmPSD.getItem().onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
            }

            @Override
            public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
                if (!PersonalShrinkingModuleHelper.getCanShrink(item)) {
                    PersonalShrinkingModuleHelper.setCanShrink(item, true);
                }
            }

            @Override
            public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
                if (PersonalShrinkingModuleHelper.getCanShrink(item)) {
                    PersonalShrinkingModuleHelper.setCanShrink(item, false);
                }
            }
        }
    }
}