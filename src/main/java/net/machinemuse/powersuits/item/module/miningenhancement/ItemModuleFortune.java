package net.machinemuse.powersuits.item.module.miningenhancement;

import net.machinemuse.numina.module.EnumModuleCategory;
import net.machinemuse.numina.module.EnumModuleTarget;
import net.machinemuse.numina.module.IEnchantmentModule;
import net.machinemuse.numina.module.IMiningEnhancementModule;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class ItemModuleFortune extends AbstractPowerModule implements IEnchantmentModule, IMiningEnhancementModule {
//    ItemStack book;

    public ItemModuleFortune(String regName) {
        super(regName, EnumModuleTarget.TOOLONLY, EnumModuleCategory.CATEGORY_MINING_ENHANCEMENT);
//        book = new ItemStack(Items.ENCHANTED_BOOK);
//        addBasePropertyDouble(MPSModuleConstants.FORTUNE_ENERGY_CONSUMPTION, 500, "RF");
//        addTradeoffPropertyDouble(MPSModuleConstants.ENCHANTMENT_LEVEL, MPSModuleConstants.FORTUNE_ENERGY_CONSUMPTION, 9500);
//        addIntTradeoffProperty(MPSModuleConstants.ENCHANTMENT_LEVEL, MPSModuleConstants.FORTUNE_ENCHANTMENT_LEVEL, 3, "", 1, 1);
    }

    @Override
    public Enchantment getEnchantment() {
        return Enchantments.FORTUNE;
    }

    @Override
    public int getLevel(@Nonnull ItemStack itemStack) {
        return 0;
//        return (int) moduleCap.applyPropertyModifiers(MPSModuleConstants.FORTUNE_ENCHANTMENT_LEVEL);
    }

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
//        if (!player.world.isRemote) {
//            if (getEnergyUsage(itemstack) > ElectricItemUtils.getPlayerEnergy(player))
//                removeEnchantment(itemstack);
//            else
//                ElectricItemUtils.drainPlayerEnergy(player, getEnergyUsage(itemstack));
//        }
        return false;
    }

    @Override
    public int getEnergyUsage() {
        return 0;
//        return (int) moduleCap.applyPropertyModifiers(MPSModuleConstants.FORTUNE_ENERGY_CONSUMPTION);
    }
}