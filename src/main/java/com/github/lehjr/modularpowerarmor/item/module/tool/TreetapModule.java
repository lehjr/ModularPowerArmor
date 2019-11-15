package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.rightclick.RightClickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by User: Andrew2448
 * 7:45 PM 4/23/13
 * <p>
 * Updated by leon on 6/14/16.
 */

public class TreetapModule extends AbstractPowerModule {
    public static ItemStack resin;
    public static Block rubber_wood;
    public static ItemStack emulatedTool;
    public static ItemStack treetap;
    private Method attemptExtract;
    private boolean isIC2Classic;

    public TreetapModule(String regName) {
        super(regName);
        if (ModCompatibility.isIndustrialCraftClassicLoaded()) {
            emulatedTool = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("ic2", "itemTreetapElectric")), 1);
            treetap = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("ic2", "itemTreetap")), 1);
            resin = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("ic2", "misc_resource")), 1, 4);
            rubber_wood = Block.REGISTRY.getObject(new ResourceLocation("ic2", "blockRubWood"));
            try {
                attemptExtract = treetap.getItem().getClass().
                        getDeclaredMethod("attemptExtract", ItemStack.class, EntityPlayer.class, World.class, BlockPos.class, EnumFacing.class, List.class);
            } catch (Exception ignored) {

            }
            isIC2Classic = true;
        } else {
            emulatedTool = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("ic2", "electric_treetap")), 1);
            treetap = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("ic2", "treetap")), 1);
            resin = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("ic2", "misc_resource")), 1, 4);
            rubber_wood = Block.REGISTRY.getObject(new ResourceLocation("ic2", "rubber_wood"));
            try {
                attemptExtract = treetap.getItem().getClass().
                        getDeclaredMethod("attemptExtract", EntityPlayer.class, World.class, BlockPos.class, EnumFacing.class, IBlockState.class, List.class);
            } catch (Exception ignored) {

            }
            isIC2Classic = false;
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IRightClickModule rightClick;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.rightClick = new RightClickie(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, MPAConfig.moduleConfig);
            this.rightClick.addBasePropertyDouble(ModuleConstants.ENERGY_CONSUMPTION, 1000, "RF");
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return (T) rightClick;
            }
            return null;
        }

        class RightClickie extends RightClickModule {
            public RightClickie(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
                return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
            }

            @Override
            public EnumActionResult onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
                IBlockState state = world.getBlockState(pos);
                Block block = state.getBlock();

                try {
                    // IC2 Classic
                    if (isIC2Classic) {
                        if (block == rubber_wood && getEnergyUsage() < ElectricItemUtils.getPlayerEnergy(player)) {
                            if (attemptExtract.invoke("attemptExtract", null, player, world, pos, facing, null).equals(true)) {
                                ElectricItemUtils.drainPlayerEnergy(player, (int) applyPropertyModifiers(ModuleConstants.ENERGY_CONSUMPTION));
                                return EnumActionResult.SUCCESS;
                            }
                        }
                    }
                    // IC2 Experimental
                    else {
                        if (block == rubber_wood && getEnergyUsage() < ElectricItemUtils.getPlayerEnergy(player)) {
                            if (attemptExtract.invoke("attemptExtract", player, world, pos, facing, state, null).equals(true)) {
                                ElectricItemUtils.drainPlayerEnergy(player, (int) applyPropertyModifiers(ModuleConstants.ENERGY_CONSUMPTION));
                                return EnumActionResult.SUCCESS;
                            }
                        }
                    }
                    return EnumActionResult.PASS;
                } catch (Exception ignored) {

                }
                return EnumActionResult.FAIL;
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(Constants.ENERGY_CONSUMPTION);
            }
        }
    }
}
