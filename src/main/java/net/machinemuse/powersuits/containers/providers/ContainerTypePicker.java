package net.machinemuse.powersuits.containers.providers;

import net.machinemuse.powersuits.basemod.MPSObjects;
import net.minecraft.inventory.container.ContainerType;

import javax.annotation.Nullable;

public class ContainerTypePicker {
        @Nullable
        public static ContainerType<?> getContainerType(int typeIndex) {
            switch (typeIndex) {
                case 0:
                    return MPSObjects.MODULAR_ITEM_CONTAINER_CONTAINER_TYPE;
                case 1:
                    return MPSObjects.MODULE_CONFIG_CONTAINER_TYPE;
                case 2:
                    return MPSObjects.KEY_CONFIG_CONTAINER_TYPE;
                case 3:
                    return MPSObjects.COSMETIC_CONFIG_CONTAINER_TYPE;
                case 4:
                    return MPSObjects.MPS_CRAFTING_CONTAINER_TYPE;
                default:
                    return null;
            }
        }
}
