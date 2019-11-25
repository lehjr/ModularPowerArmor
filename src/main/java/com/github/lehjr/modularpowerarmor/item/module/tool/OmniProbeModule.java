package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.basemod.RegistryNames;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by User: Korynkai
 * 6:30 PM 2014-11-17
 * <p>
 * TODO: Fix ProjectRed (may require PR to ProjectRed)
 */
public class OmniProbeModule extends AbstractPowerModule {
    private ItemStack rcMeter = ItemStack.EMPTY;

    private ItemStack conduitProbe = ItemStack.EMPTY;

    private ItemStack teMultimeter = ItemStack.EMPTY;

    public OmniProbeModule(String regName) {
        super(regName);
        if (ModCompatibility.isRailcraftLoaded()) {
            rcMeter = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("railcraft", "tool_charge_meter")), 1);
        }

        /* untested */
        if (ModCompatibility.isThermalExpansionLoaded()) {
            teMultimeter = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("thermalexpansion", "multimeter")), 1);
        }

        if (ModCompatibility.isEnderIOLoaded()) {
            conduitProbe = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("enderio", "item_conduit_probe")), 1);
        }
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
            public EnumActionResult onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
                Block block = world.getBlockState(pos).getBlock();

                if (block == null || block.isAir(world.getBlockState(pos), world, pos))
                    return EnumActionResult.PASS;

                try {
                    if (ModCompatibility.isEnderIOLoaded()) {
                        if (conduitProbe.getItem().onItemUse(player, world, pos, EnumHand.MAIN_HAND, side, hitX, hitY, hitZ) == EnumActionResult.SUCCESS)
                            return EnumActionResult.SUCCESS;
                    }

                    if (ModCompatibility.isRailcraftLoaded()) {
                        if (rcMeter.getItem().onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS)
                            return EnumActionResult.SUCCESS;
                    }

                    if (ModCompatibility.isThermalExpansionLoaded()) {
                        if (teMultimeter.getItem().onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS)
                            return EnumActionResult.SUCCESS;
                    }
                } catch (Exception ignored) {

                }
                return EnumActionResult.PASS;
            }

            public static final String TAG_EIO_NO_COMPLETE = "eioNoCompete";
            public static final String TAG_EIO_FACADE_TRANSPARENCY = "eioFacadeTransparency";

            public String getEIONoCompete(@Nonnull ItemStack stack) {
                NBTTagCompound moduleTag = NBTUtils.getMuseModuleTag(stack);
                return moduleTag != null ? moduleTag.getString(TAG_EIO_NO_COMPLETE) : "";
            }

            public void setEIONoCompete(@Nonnull ItemStack stack, String s) {
                NBTTagCompound moduleTag = NBTUtils.getMuseModuleTag(stack);
                moduleTag.setString(TAG_EIO_NO_COMPLETE, s);
            }

            public boolean getEIOFacadeTransparency(@Nonnull ItemStack stack) {
                NBTTagCompound moduleTag = NBTUtils.getMuseModuleTag(stack);
                if (moduleTag != null) {
                    return moduleTag.getBoolean(TAG_EIO_FACADE_TRANSPARENCY);
                }
                return false;
            }

            public void setEIOFacadeTransparency(@Nonnull ItemStack stack, boolean b) {
                NBTTagCompound moduleTag = NBTUtils.getMuseModuleTag(stack);
                moduleTag.setBoolean(TAG_EIO_FACADE_TRANSPARENCY, b);
            }

            @Override
            public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
                if (!getEIOFacadeTransparency(item)) {
                    setEIONoCompete(item, RegistryNames.MODULE_OMNIPROBE__REGNAME);
                    setEIOFacadeTransparency(item, true);
                }
            }

            @Override
            public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
                if (!(getEIONoCompete(item).isEmpty()) && (!getEIONoCompete(item).isEmpty())) {
                    if (getEIONoCompete(item).equals(RegistryNames.MODULE_OMNIPROBE__REGNAME)) {
                        setEIONoCompete(item, "");
                        if (getEIOFacadeTransparency(item)) {
                            setEIOFacadeTransparency(item, false);
                        }
                    }
                } else {
                    if (getEIOFacadeTransparency(item)) {
                        setEIOFacadeTransparency(item, false);
                    }
                }
            }
        }
    }
}