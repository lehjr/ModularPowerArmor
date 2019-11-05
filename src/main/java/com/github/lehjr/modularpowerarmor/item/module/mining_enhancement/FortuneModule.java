package com.github.lehjr.modularpowerarmor.item.module.mining_enhancement;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.legacy.module.IEnchantmentModule;
import com.github.lehjr.mpalib.legacy.module.IMiningEnhancementModule;
import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class FortuneModule extends AbstractPowerModule implements IEnchantmentModule, IMiningEnhancementModule {
    ItemStack book;

    public FortuneModule(EnumModuleTarget moduleTargetIn) {
        super(moduleTargetIn);
        book = new ItemStack(Items.ENCHANTED_BOOK);
        addBasePropertyDouble(ModuleConstants.FORTUNE_ENERGY_CONSUMPTION, 500, "RF");
        addTradeoffPropertyDouble(ModuleConstants.ENCHANTMENT_LEVEL, ModuleConstants.FORTUNE_ENERGY_CONSUMPTION, 9500);
        addIntTradeoffProperty(ModuleConstants.ENCHANTMENT_LEVEL, ModuleConstants.FORTUNE_ENCHANTMENT_LEVEL, 3, "", 1, 1);
    }

    @Override
    public Enchantment getEnchantment() {
        return Enchantments.FORTUNE;
    }

    @Override
    public int getLevel(@Nonnull ItemStack itemStack) {
        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, ModuleConstants.FORTUNE_ENCHANTMENT_LEVEL);
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(book).getParticleTexture();
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
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        if (!player.world.isRemote) {
            if (getEnergyUsage(itemstack) > ElectricItemUtils.getPlayerEnergy(player))
                removeEnchantment(itemstack);
            else
                ElectricItemUtils.drainPlayerEnergy(player, getEnergyUsage(itemstack));
        }
        return false;
    }

    @Override
    public int getEnergyUsage(@Nonnull ItemStack itemStack) {
        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, ModuleConstants.FORTUNE_ENERGY_CONSUMPTION);
    }

    @Override
    public String getDataName() {
        return ModuleConstants.MODULE_FORTUNE_DATANAME;
    }
}