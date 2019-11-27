package com.github.machinemuse.powersuits.powermodule.tool;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by User: Andrew
 * Date: 4/21/13
 * Time: 2:02 PM
 */
public class GrafterModule extends PowerModuleBase {
    private static ItemStack grafter = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("forestry", "grafter")), 1);

    public GrafterModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), grafter);
        addBasePropertyDouble(MPSModuleConstants.GRAFTER_ENERGY_CONSUMPTION, 10000, "RF");
        addBasePropertyDouble(MPSModuleConstants.GRAFTER_HEAT_GENERATION, 20);
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.TOOL;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_GRAFTER__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(grafter).getParticleTexture();
    }
}