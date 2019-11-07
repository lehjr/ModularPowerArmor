package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.rightclick.RightClickModule;
import com.github.lehjr.mpalib.config.MPALibConfig;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.math.MathUtils;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemWirelessGrid;
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
 * Created by leon on 4/26/17.
 */
public class RefinedStorageWirelessModule extends AbstractPowerModule {
    public static final ResourceLocation wirelessGridRegName = new ResourceLocation("refinedstorage", "wireless_grid");
    public static final ResourceLocation wirelessCraftingGridRegName = new ResourceLocation("refinedstorage", "wireless_crafting_grid");

    public RefinedStorageWirelessModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IRightClickModule rightClick;

        public CapProvider(ItemStack module) {
            this.module = module;
            this.rightClick = new RightClickie(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, MPAConfig.moduleConfig);
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
                NBTTagCompound tag = NBTUtils.getMuseModuleTag(itemStackIn);
                ItemStack emulatedTool = getEmulatedTool();

                if (tag != null) {
                    if (!isModuleTagValid(tag)) {
                        tag = initializeDefaults(tag);
                    }

                    if (!isModuleTagSet(tag)) {
                        return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
                    }

                    int energy = (int) MathUtils.clampDouble(ElectricItemUtils.getPlayerEnergy(playerIn) * MPALibConfig.INSTANCE.getRSRatio(), 0, 3500);
                    tag.setInteger("Energy", energy);
                    emulatedTool.setTagCompound(tag);
                    ActionResult result = emulatedTool.getItem().onItemRightClick(worldIn, playerIn, hand);
                    double energyUsed = ((energy - emulatedTool.getTagCompound().getInteger("Energy")) * MPALibConfig.INSTANCE.getRSRatio());
                    ElectricItemUtils.drainPlayerEnergy(playerIn, (int) energyUsed);
                    return ActionResult.newResult(result.getType(), itemStackIn);
                }
                return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
            }

            /*
             * sets up the nbt tags needed to use the device and stores them in the power fists's tags to be passed back during right click.
             * The wireless grid and wireless crafting grid are a bit different but we can handle both the same way.
             */
            @Override
            public EnumActionResult onItemUse(ItemStack itemStackIn, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
                NBTTagCompound tag = NBTUtils.getMuseModuleTag(itemStackIn);
                ItemStack emulatedTool = getEmulatedTool();
                emulatedTool.setTagCompound(tag);
                EnumActionResult result = emulatedTool.onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                NBTTagCompound tag2 = emulatedTool.getTagCompound();

                // maybe loop through a set of string keys instead?
                if (tag2 != null) {
                    if (tag2.hasKey("Initialized"))
                        tag.setInteger("Initialized", 1);

                    if (tag2.hasKey("SearchBoxMode"))
                        tag.setInteger("SearchBoxMode", tag2.getInteger("SearchBoxMode"));

                    if (tag2.hasKey("SortingType"))
                        tag.setInteger("SortingType", tag2.getInteger("SortingType"));

                    if (tag2.hasKey("DimensionID"))
                        tag.setInteger("DimensionID", tag2.getInteger("DimensionID"));

                    if (tag2.hasKey("ViewType"))
                        tag.setInteger("ViewType", tag2.getInteger("ViewType"));

                    if (tag2.hasKey("GridX"))
                        tag.setInteger("GridX", tag2.getInteger("GridX"));

                    if (tag2.hasKey("GridY"))
                        tag.setInteger("GridY", tag2.getInteger("GridY"));

                    if (tag2.hasKey("GridZ"))
                        tag.setInteger("GridZ", tag2.getInteger("GridZ"));

                    if (tag2.hasKey("ControllerX"))
                        tag.setInteger("ControllerX", tag2.getInteger("ControllerX"));

                    if (tag2.hasKey("ControllerY"))
                        tag.setInteger("ControllerY", tag2.getInteger("ControllerY"));

                    if (tag2.hasKey("ControllerZ"))
                        tag.setInteger("ControllerZ", tag2.getInteger("ControllerZ"));
                }
                return result;
            }
        }
    }


    static ItemStack getEmulatedTool() {
        if (ModCompatibility.isWirelessCraftingGridLoaded())
            return new ItemStack(Item.REGISTRY.getObject(wirelessCraftingGridRegName), 1, 0);
        else
            return new ItemStack(Item.REGISTRY.getObject(wirelessGridRegName), 1, 0);
    }

    @Nonnull
    public static INetworkItem provide(INetworkItemHandler handler, EntityPlayer player, ItemStack itemStackIn) {
        ItemStack emulatedTool = getEmulatedTool();
        NBTTagCompound tag = NBTUtils.getMuseModuleTag(itemStackIn);
        emulatedTool.setTagCompound(tag);
        return new NetworkItemWirelessGrid(handler, player, emulatedTool);
    }

    private NBTTagCompound initializeDefaults(NBTTagCompound nbt) {
        if (nbt == null)
            nbt = new NBTTagCompound();
        if (!nbt.hasKey("ViewType"))
            nbt.setInteger("ViewType", 0);
        if (!nbt.hasKey("SortingDirection"))
            nbt.setInteger("SortingDirection", 1);
        if (!nbt.hasKey("SortingType"))
            nbt.setInteger("SortingType", 0);
        if (!nbt.hasKey("SearchBoxMode"))
            nbt.setInteger("SearchBoxMode", 0);
        return nbt;
    }

    public boolean isModuleTagSet(NBTTagCompound nbt) {
        if (nbt == null)
            return false;

        return nbt.hasKey("ControllerY")
                && nbt.hasKey("ControllerY")
                && nbt.hasKey("ControllerZ")
                && nbt.hasKey("DimensionID");
    }

    public boolean isModuleTagValid(NBTTagCompound nbt) {
        if (nbt == null)
            return false;

        return nbt.hasKey("ViewType")
                && nbt.hasKey("SortingDirection")
                && nbt.hasKey("SortingType")
                && nbt.hasKey("SearchBoxMode");
    }
}
