package net.machinemuse.powersuits.item.module.miningenhancement;

import net.machinemuse.numina.capabilities.module.enchantment.EnchantmentModule;
import net.machinemuse.numina.capabilities.module.enchantment.IEnchantmentModule;
import net.machinemuse.numina.capabilities.module.miningenhancement.IMiningEnhancementModule;
import net.machinemuse.numina.capabilities.module.miningenhancement.MiningEnhancement;
import net.machinemuse.numina.capabilities.module.miningenhancement.MiningEnhancementCapability;
import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class ItemModuleSilkTouch extends AbstractPowerModule {
    public ItemModuleSilkTouch(String regName) {
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
        IMiningEnhancementModule miningEnhancement;
        IEnchantmentModule enchantmentModule;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.MINING_ENHANCEMENT, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);

            this.moduleCap.addBasePropertyDouble(MPSConstants.SILK_TOUCH_ENERGY_CONSUMPTION, 2500, "RF");

            this.miningEnhancement = new Enhancement();
            this.enchantmentModule = new EnchantmentThingie();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == MiningEnhancementCapability.MINING_ENHANCEMENT)
                return MiningEnhancementCapability.MINING_ENHANCEMENT.orEmpty(cap, LazyOptional.of(() -> miningEnhancement));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class Enhancement extends MiningEnhancement {
            /**
             * Called before a block is broken.  Return true to prevent default block harvesting.
             *
             * Note: In SMP, this is called on both client and server sides!
             *
             * @param itemstack The current ItemStack
             * @param pos Block's position in world
             * @param player The Player that is wielding the item
             * @return True to prevent harvesting, false to continue as normal
             */
            @Override
            public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
                if (!player.world.isRemote) {
                    if (getEnergyUsage() > ElectricItemUtils.getPlayerEnergy(player))
                        enchantmentModule.removeEnchantment(itemstack);
                    else {
                        Block block = player.world.getBlockState(pos).getBlock();
                        // fixme!!

//                        if (block.canSilkHarvest(player.world, pos, player.world.getBlockState(pos), player)) {
//                            ElectricItemUtils.drainPlayerEnergy(player, getEnergyUsage());
//                        }
                    }
                }
                return false;
            }

            @Override
            public int getEnergyUsage() {
                return (int) moduleCap.applyPropertyModifiers(MPSConstants.SILK_TOUCH_ENERGY_CONSUMPTION);
            }
        }

        class EnchantmentThingie extends EnchantmentModule {
            @Override
            public Enchantment getEnchantment() {
                return Enchantments.SILK_TOUCH;
            }

            @Override
            public int getLevel(@Nonnull ItemStack itemStack) {
                return 1;
            }
        }
    }
}