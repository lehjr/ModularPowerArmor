/*
 * Copyright (c) 2019 MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.powermodule.mining_enhancement;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.legacy.module.IEnchantmentModule;
import com.github.lehjr.mpalib.legacy.module.IMiningEnhancementModule;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class FortuneModule extends PowerModuleBase implements IEnchantmentModule, IMiningEnhancementModule {
    ItemStack book;

    public FortuneModule(EnumModuleTarget moduleTargetIn) {
        super(moduleTargetIn);
        book = new ItemStack(Items.ENCHANTED_BOOK);
        addBasePropertyDouble(MPSModuleConstants.FORTUNE_ENERGY_CONSUMPTION, 500, "RF");
        addTradeoffPropertyDouble(MPSModuleConstants.ENCHANTMENT_LEVEL, MPSModuleConstants.FORTUNE_ENERGY_CONSUMPTION, 9500);
        addIntTradeoffProperty(MPSModuleConstants.ENCHANTMENT_LEVEL, MPSModuleConstants.FORTUNE_ENCHANTMENT_LEVEL, 3, "", 1, 1);
    }

    @Override
    public Enchantment getEnchantment() {
        return Enchantments.FORTUNE;
    }

    @Override
    public int getLevel(@Nonnull ItemStack itemStack) {
        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.FORTUNE_ENCHANTMENT_LEVEL);
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
        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.FORTUNE_ENERGY_CONSUMPTION);
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_FORTUNE_DATANAME;
    }
}