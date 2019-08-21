package net.machinemuse.powersuits.containers.providers;

import net.machinemuse.powersuits.containers.MPSCraftingContainer;
import net.machinemuse.powersuits.containers.ModularItemContainer;
import net.machinemuse.powersuits.containers.TinkerTableContainer;
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
                return new TranslationTextComponent("gui.powersuits.tab.module_install");
            case 1:
                return new TranslationTextComponent("gui.powersuits.tab.tinker");
            case 2:
                return new TranslationTextComponent("gui.powersuits.tab.keybinds");
            case 3:
                return new TranslationTextComponent("gui.powersuits.tab.visual");
            default:
                return new TranslationTextComponent("container.crafting");
        }
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        if (typeIndex == 0)
            return new ModularItemContainer(windowId, playerInventory);
        if (typeIndex > 0 && typeIndex < 4 )
            return new TinkerTableContainer(windowId, playerInventory, this.typeIndex);
        return new MPSCraftingContainer(windowId, playerInventory);
    }
}