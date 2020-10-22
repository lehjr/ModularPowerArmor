package com.github.lehjr.modularpowerarmor.container.providers;

import com.github.lehjr.modularpowerarmor.container.MPACraftingContainer;
import com.github.lehjr.modularpowerarmor.container.TinkerTableContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class TinkerContainerProvider implements INamedContainerProvider {
    int typeIndex;
    public TinkerContainerProvider(int typeIndex) {
        this.typeIndex = typeIndex;
    }

    @Override
    public ITextComponent getDisplayName() {
        switch(typeIndex) {
            case 0:
                return new TranslationTextComponent("gui.modularpowerarmor.tab.tinker");
            default:
                return new TranslationTextComponent("container.crafting");
        }
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        switch(typeIndex) {
            case 0:
                return new TinkerTableContainer(windowId, playerInventory);
            default:
                return new MPACraftingContainer(windowId, playerInventory);
        }
    }
}