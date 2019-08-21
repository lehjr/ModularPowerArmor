package net.machinemuse.powersuits.containers.providers;

import net.machinemuse.powersuits.containers.ModeChangingContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;

public class RadialModeContainerProvider implements INamedContainerProvider {
    public RadialModeContainerProvider() {
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("RadialModeSelection");
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity player) {
        return new ModeChangingContainer(windowID, playerInventory);
    }
}