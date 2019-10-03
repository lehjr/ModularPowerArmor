package net.machinemuse.powersuits.item.module.tool;

import com.google.common.util.concurrent.AtomicDouble;
import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.numina.capabilities.module.blockbreaking.IBlockBreakingModule;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModule;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.helper.ToolHelpers;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.MPSRegistryNames;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DiamondPickUpgradeModule extends AbstractPowerModule {
    public static final ResourceLocation pickaxe = new ResourceLocation(MPSRegistryNames.MODULE_PICKAXE__REGNAME);

    public DiamondPickUpgradeModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IBlockBreakingModule blockBreaking;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.blockBreaking = new BlockBreaker(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
            this.blockBreaking.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 500, "RF");
            this.blockBreaking.addBasePropertyDouble(MPSConstants.HARVEST_SPEED, 8, "x");
            this.blockBreaking.addTradeoffPropertyDouble(MPSConstants.OVERCLOCK, MPSConstants.ENERGY_CONSUMPTION, 9500);
            this.blockBreaking.addTradeoffPropertyDouble(MPSConstants.OVERCLOCK, MPSConstants.HARVEST_SPEED, 52);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> blockBreaking));
        }

        class BlockBreaker extends PowerModule implements IBlockBreakingModule {
            public BlockBreaker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public boolean canHarvestBlock(@Nonnull ItemStack modeChangingStack, BlockState state, PlayerEntity player, BlockPos pos, int playerEnergy) {
                AtomicBoolean canHarvest = new AtomicBoolean(false);
                modeChangingStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(powerFist -> {
                    if (powerFist instanceof IModeChangingItem) {
                        ItemStack pickaxeModule = ((IModeChangingItem) powerFist).getOnlineModuleOrEmpty(pickaxe);
                        if (!pickaxeModule.isEmpty()) {
                            int energyUsage = pickaxeModule.getCapability(PowerModuleCapability.POWER_MODULE).map(m -> {
                                if (m instanceof IBlockBreakingModule) {
                                    return ((IBlockBreakingModule) m).getEnergyUsage();
                                }
                                return 0;
                            }).orElse(0);
                            canHarvest.set(pickaxeModule.getCapability(PowerModuleCapability.POWER_MODULE).map(m -> {
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
            public boolean onBlockDestroyed(ItemStack modeChangingStack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving, int playerEnergy) {
                if (this.canHarvestBlock(modeChangingStack, state, (PlayerEntity) entityLiving, pos, playerEnergy)) {
                    AtomicInteger energyUsage = new AtomicInteger(0);
                    modeChangingStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(powerFist -> {
                        if (powerFist instanceof IModeChangingItem) {
                            ItemStack pickaxeModule = ((IModeChangingItem) powerFist).getOnlineModuleOrEmpty(pickaxe);
                            if (!pickaxeModule.isEmpty()) {
                                energyUsage.set(pickaxeModule.getCapability(PowerModuleCapability.POWER_MODULE).map(m -> {
                                    if (m instanceof IBlockBreakingModule) {
                                        return ((IBlockBreakingModule) m).getEnergyUsage();
                                    }
                                    return 0;
                                }).orElse(0));
                            }
                        }
                    });
                    ElectricItemUtils.drainPlayerEnergy((PlayerEntity) entityLiving, energyUsage.get());
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
                PlayerEntity player = event.getEntityPlayer();
                ItemStack modeChangingStack = player.getActiveItemStack();
                AtomicDouble newSpeed = new AtomicDouble(event.getNewSpeed());
                modeChangingStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(powerFist -> {
                    if (powerFist instanceof IModeChangingItem) {
                        ItemStack pickaxeModule = ((IModeChangingItem) powerFist).getOnlineModuleOrEmpty(pickaxe);
                        if (!pickaxeModule.isEmpty()) {
                            newSpeed.set(newSpeed.get() *
                                    pickaxeModule.getCapability(PowerModuleCapability.POWER_MODULE).map(m ->
                                            m.applyPropertyModifiers(MPSConstants.HARVEST_SPEED)).orElse(1D));
                        }
                    }
                });
                event.setNewSpeed((float) newSpeed.get());
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
            }
        }
    }
}