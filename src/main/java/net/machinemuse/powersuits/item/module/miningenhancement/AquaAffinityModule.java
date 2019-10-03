package net.machinemuse.powersuits.item.module.miningenhancement;

import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.module.blockbreaking.IBlockBreakingModule;
import net.machinemuse.numina.capabilities.module.miningenhancement.MiningEnhancement;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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


// Note: tried as an enchantment, but failed to function properly due to how block breaking code works
public class AquaAffinityModule extends AbstractPowerModule {
    public AquaAffinityModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        MiningEnhancement miningEnhancement;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.miningEnhancement = new BlockBreaker(module, EnumModuleCategory.MINING_ENHANCEMENT, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
            this.miningEnhancement.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 0, "RF");
            this.miningEnhancement.addBasePropertyDouble(MPSConstants.HARVEST_SPEED, 0.2, "%");
            this.miningEnhancement.addTradeoffPropertyDouble(MPSConstants.POWER, MPSConstants.ENERGY_CONSUMPTION, 1000);
            this.miningEnhancement.addTradeoffPropertyDouble(MPSConstants.POWER, MPSConstants.HARVEST_SPEED, 0.8);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> miningEnhancement));
        }

        class BlockBreaker extends MiningEnhancement implements IBlockBreakingModule {
            public BlockBreaker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public boolean canHarvestBlock(@Nonnull ItemStack stack, BlockState state, PlayerEntity player, BlockPos pos, int playerEnergy) {
                return false;
            }

            @Override
            public boolean onBlockDestroyed(ItemStack itemStack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving, int playerEnergy) {
                if (this.canHarvestBlock(itemStack, state, (PlayerEntity) entityLiving, pos, playerEnergy)) {
                    ElectricItemUtils.drainPlayerEnergy((PlayerEntity) entityLiving, getEnergyUsage());
                    return true;
                }
                return false;
            }

            @Override
            public void handleBreakSpeed(PlayerEvent.BreakSpeed event) {
                PlayerEntity player = event.getEntityPlayer();
                if (event.getNewSpeed() > 1 && (player.canSwim() || !player.onGround)
                        && ElectricItemUtils.getPlayerEnergy(player) > getEnergyUsage()) {
                    event.setNewSpeed((float) (event.getNewSpeed() * 5 * applyPropertyModifiers(MPSConstants.HARVEST_SPEED)));
                }
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
            }

            @Nonnull
            @Override
            public ItemStack getEmulatedTool() {
                return ItemStack.EMPTY; // FIXME?
            }
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
        return false;
    }
}