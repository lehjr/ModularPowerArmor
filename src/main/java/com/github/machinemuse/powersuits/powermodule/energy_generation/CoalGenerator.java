package com.github.machinemuse.powersuits.powermodule.energy_generation;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MuseIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import com.github.machinemuse.powersuits.utils.modulehelpers.CoalGenHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Created by Eximius88 on 1/16/14.
 */
public class CoalGenerator extends PowerModuleBase implements IPlayerTickModule, IToggleableModule {
    public CoalGenerator(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), new ItemStack(Blocks.FURNACE));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.controlCircuit, 1));

        addBasePropertyDouble(MPSModuleConstants.MAX_COAL_STORAGE, 128);
        addBasePropertyDouble(MPSModuleConstants.HEAT_GENERATION, 2.5);
        addBasePropertyDouble(MPSModuleConstants.ENERGY_PER_COAL, 300);
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {

        // TODO: add charging code, change to more generic combustion types... maybe add GUI

        IInventory inv = player.inventory;
        int coalNeeded = (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(item, MPSModuleConstants.MAX_COAL_STORAGE) - CoalGenHelper.getCoalLevel(item);
        if (coalNeeded > 0) {
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() == Items.COAL) {
                    int loopTimes = coalNeeded < stack.getCount() ? coalNeeded : stack.getCount();
                    for (int i2 = 0; i2 < loopTimes; i2++) {
                        CoalGenHelper.setCoalLevel(item, CoalGenHelper.getCoalLevel(item) + 1);
                        player.inventory.decrStackSize(i, 1);
                        if (stack.getCount() == 0) {
                            player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                        }
                    }


                    if (ModuleManager.INSTANCE.getOrSetModularPropertyDouble(item, MPSModuleConstants.MAX_COAL_STORAGE) - CoalGenHelper.getCoalLevel(item) < 1) {
                        i = inv.getSizeInventory() + 1;
                    }
                }
            }
        }


    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.ENERGY_GENERATION;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_COAL_GEN__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.coalGenerator;
    }
}