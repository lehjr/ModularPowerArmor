package net.machinemuse.powersuits.client.gui;

import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.text.ITextComponent;

public class PortableCraftingGui extends CraftingScreen {// ==> extends ContainerScreen<WorkbenchContainer> implements IRecipeShownListener
    public PortableCraftingGui(WorkbenchContainer workbenchContainer, PlayerInventory playerInventory, ITextComponent title) {
        super(workbenchContainer, playerInventory, title);
    }
}