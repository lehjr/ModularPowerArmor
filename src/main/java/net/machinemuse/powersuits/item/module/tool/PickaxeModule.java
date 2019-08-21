package net.machinemuse.powersuits.item.module.tool;

import net.machinemuse.numina.capabilities.module.blockbreaking.BlockBreaking;
import net.machinemuse.numina.capabilities.module.blockbreaking.BlockBreakingCapability;
import net.machinemuse.numina.capabilities.module.blockbreaking.IBlockBreakingModule;
import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class PickaxeModule extends AbstractPowerModule {
    protected static final ItemStack emulatedTool = new ItemStack(Items.IRON_PICKAXE);

    public PickaxeModule(String regName) {
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
            this.moduleCap = new PowerModule(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);

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
            public boolean onBlockDestroyed(ItemStack itemStack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving, int playerEnergy) {
                if (this.canHarvestBlock(itemStack, state, (PlayerEntity) entityLiving, pos, playerEnergy)) {
                    ElectricItemUtils.drainPlayerEnergy((PlayerEntity) entityLiving, getEnergyUsage());
                    return true;
                }
                return false;
            }

            @Override
            public ItemStack getEmulatedTool() {
                return emulatedTool;
            }

            @Override
            public int getEnergyUsage() {
                return (int) moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
            }

            @Override
            public void handleBreakSpeed(PlayerEvent.BreakSpeed event) {
                event.setNewSpeed((float) (event.getNewSpeed() * moduleCap.applyPropertyModifiers(MPSConstants.HARVEST_SPEED)));
            }
        }
    }
}