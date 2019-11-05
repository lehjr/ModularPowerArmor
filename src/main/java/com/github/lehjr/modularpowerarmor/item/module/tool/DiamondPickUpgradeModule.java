package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.RegistryNames;
import com.github.lehjr.modularpowerarmor.basemod.config.CommonConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.capabilities.module.blockbreaking.IBlockBreakingModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.helper.ToolHelpers;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DiamondPickUpgradeModule extends AbstractPowerModule {
    public static final ResourceLocation pickaxe = new ResourceLocation(RegistryNames.MODULE_PICKAXE__REGNAME);

    public DiamondPickUpgradeModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IBlockBreakingModule blockBreaking;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.blockBreaking = new BlockBreaker(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
            this.blockBreaking.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 500, "RF");
            this.blockBreaking.addBasePropertyDouble(Constants.HARVEST_SPEED, 8, "x");
            this.blockBreaking.addTradeoffPropertyDouble(Constants.OVERCLOCK, Constants.ENERGY_CONSUMPTION, 9500);
            this.blockBreaking.addTradeoffPropertyDouble(Constants.OVERCLOCK, Constants.HARVEST_SPEED, 52);
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == PowerModuleCapability.POWER_MODULE;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return (T) blockBreaking;
            }
            return null;
        }

        class BlockBreaker extends PowerModule implements IBlockBreakingModule {
            public BlockBreaker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public boolean canHarvestBlock(@Nonnull ItemStack modeChangingStack, IBlockState state, EntityPlayer player, BlockPos pos, int playerEnergy) {
                AtomicBoolean canHarvest = new AtomicBoolean(false);
                Optional.ofNullable(modeChangingStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(powerFist -> {
                    if (powerFist instanceof IModeChangingItem) {
                        ItemStack pickaxeModule = ((IModeChangingItem) powerFist).getOnlineModuleOrEmpty(pickaxe);
                        if (!pickaxeModule.isEmpty()) {
                            int energyUsage = Optional.ofNullable(pickaxeModule.getCapability(PowerModuleCapability.POWER_MODULE, null)).map(m -> {
                                if (m instanceof IBlockBreakingModule) {
                                    return ((IBlockBreakingModule) m).getEnergyUsage();
                                }
                                return 0;
                            }).orElse(0);
                            canHarvest.set(Optional.ofNullable(pickaxeModule.getCapability(PowerModuleCapability.POWER_MODULE, null)).map(m -> {
                                if (m instanceof IBlockBreakingModule) {
                                    return !((IBlockBreakingModule) m).canHarvestBlock(modeChangingStack, state, player, pos, playerEnergy) &&
                                            playerEnergy >= energyUsage && ToolHelpers.isToolEffective(player.getEntityWorld(), pos, getEmulatedTool());
                                }
                                return false;
                            }).orElse(false));
                        }
                    }
                });
                return canHarvest.get();
            }

            @Override
            public boolean onBlockDestroyed(ItemStack modeChangingStack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving, int playerEnergy) {
                if (this.canHarvestBlock(modeChangingStack, state, (EntityPlayer) entityLiving, pos, playerEnergy)) {
                    AtomicInteger energyUsage = new AtomicInteger(0);
                    Optional.ofNullable(modeChangingStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(powerFist -> {
                        if (powerFist instanceof IModeChangingItem) {
                            ItemStack pickaxeModule = ((IModeChangingItem) powerFist).getOnlineModuleOrEmpty(pickaxe);
                            if (!pickaxeModule.isEmpty()) {
                                energyUsage.set(Optional.ofNullable(pickaxeModule.getCapability(PowerModuleCapability.POWER_MODULE, null)).map(m -> {
                                    if (m instanceof IBlockBreakingModule) {
                                        return ((IBlockBreakingModule) m).getEnergyUsage();
                                    }
                                    return 0;
                                }).orElse(0));
                            }
                        }
                    });
                    ElectricItemUtils.drainPlayerEnergy((EntityPlayer) entityLiving, energyUsage.get());
                    return true;
                }
                return false;
            }

            @Override
            public ItemStack getEmulatedTool() {
                return new ItemStack(Items.DIAMOND_PICKAXE);
            }

            @Override
            public void handleBreakSpeed(PlayerEvent.BreakSpeed event) {
                EntityPlayer player = event.getEntityPlayer();
                ItemStack modeChangingStack = player.getActiveItemStack();
                AtomicDouble newSpeed = new AtomicDouble(event.getNewSpeed());
                Optional.ofNullable(modeChangingStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(powerFist -> {
                    if (powerFist instanceof IModeChangingItem) {
                        ItemStack pickaxeModule = ((IModeChangingItem) powerFist).getOnlineModuleOrEmpty(pickaxe);
                        if (!pickaxeModule.isEmpty()) {
                            newSpeed.set(newSpeed.get() *
                                    Optional.ofNullable(pickaxeModule.getCapability(PowerModuleCapability.POWER_MODULE, null)).map(m ->
                                            m.applyPropertyModifiers(Constants.HARVEST_SPEED)).orElse(1D));
                        }
                    }
                });
                event.setNewSpeed((float) newSpeed.get());
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(Constants.ENERGY_CONSUMPTION);
            }
        }
    }
}