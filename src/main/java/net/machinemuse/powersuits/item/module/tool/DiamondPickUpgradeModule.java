package net.machinemuse.powersuits.item.module.tool;

import com.google.common.util.concurrent.AtomicDouble;
import net.machinemuse.numina.capabilities.inventory.modechanging.ModeChangingCapability;
import net.machinemuse.numina.capabilities.module.blockbreaking.BlockBreaking;
import net.machinemuse.numina.capabilities.module.blockbreaking.BlockBreakingCapability;
import net.machinemuse.numina.capabilities.module.blockbreaking.IBlockBreakingModule;
import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.helper.ToolHelpers;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.MPSItems;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DiamondPickUpgradeModule extends AbstractPowerModule {
    public static final ResourceLocation pickaxe = new ResourceLocation(MPSItems.INSTANCE.MODULE_PICKAXE__REGNAME);

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
        IPowerModule moduleCap;
        IBlockBreakingModule blockBreaking;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.CATEGORY_TOOL, EnumModuleTarget.TOOLONLY, MPSConfig.INSTANCE);

            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 500, "RF");
            this.moduleCap.addBasePropertyDouble(MPSConstants.HARVEST_SPEED, 8, "x");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.OVERCLOCK, MPSConstants.ENERGY_CONSUMPTION, 9500);
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.OVERCLOCK, MPSConstants.HARVEST_SPEED, 52);

            this.blockBreaking = new BlockBreaker();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == BlockBreakingCapability.BLOCK_BREAKING)
                return BlockBreakingCapability.BLOCK_BREAKING.orEmpty(cap, LazyOptional.of(() -> blockBreaking));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }


        class BlockBreaker extends BlockBreaking {
            @Override
            public boolean canHarvestBlock(@Nonnull ItemStack modeChangingStack, BlockState state, PlayerEntity player, BlockPos pos, int playerEnergy) {
                AtomicBoolean canHarvest = new AtomicBoolean(false);
                modeChangingStack.getCapability(ModeChangingCapability.MODE_CHANGING).ifPresent(powerFist -> {
                    ItemStack pickaxeModule = powerFist.itemGetActiveModuleOrEmpty(pickaxe);

                    if (!pickaxeModule.isEmpty()) {
                        int energyUsage = pickaxeModule.getCapability(BlockBreakingCapability.BLOCK_BREAKING).map(m -> m.getEnergyUsage()).orElse(0);
                        canHarvest.set(pickaxeModule.getCapability(BlockBreakingCapability.BLOCK_BREAKING).map(m -> !m.canHarvestBlock(modeChangingStack, state, player, pos, playerEnergy) &&
                                playerEnergy >= energyUsage && ToolHelpers.isToolEffective(player.getEntityWorld(), pos, getEmulatedTool())).orElse(false));
                    }
                });
                return canHarvest.get();
            }

            @Override
            public boolean onBlockDestroyed(ItemStack modeChangingStack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving, int playerEnergy) {
                if (this.canHarvestBlock(modeChangingStack, state, (PlayerEntity) entityLiving, pos, playerEnergy)) {
                    AtomicInteger energyUsage = new AtomicInteger(0);
                    modeChangingStack.getCapability(ModeChangingCapability.MODE_CHANGING).ifPresent(powerFist -> {
                        ItemStack pickaxeModule = powerFist.itemGetActiveModuleOrEmpty(pickaxe);

                        if (!pickaxeModule.isEmpty()) {
                            energyUsage.set(pickaxeModule.getCapability(BlockBreakingCapability.BLOCK_BREAKING).map(m -> m.getEnergyUsage()).orElse(0));
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

                modeChangingStack.getCapability(ModeChangingCapability.MODE_CHANGING).ifPresent(powerFist -> {
                    ItemStack pickaxeModule = powerFist.itemGetActiveModuleOrEmpty(pickaxe);

                    if (!pickaxeModule.isEmpty()) {
                        newSpeed.set(newSpeed.get() *
                                pickaxeModule.getCapability(PowerModuleCapability.POWER_MODULE).map(m ->
                                        m.applyPropertyModifiers(MPSConstants.HARVEST_SPEED)).orElse(1D));
                    }
                });
                event.setNewSpeed((float) newSpeed.get());
            }
        }
    }
}