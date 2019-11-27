package com.github.machinemuse.powersuits.powermodule.armor;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class LeatherPlatingModule extends PowerModuleBase {
    protected final ItemStack leather = new ItemStack(Items.LEATHER);

    public LeatherPlatingModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(this.getDataName(), ItemUtils.copyAndResize(new ItemStack(Items.LEATHER), 2));

        addBasePropertyDouble(MPSModuleConstants.ARMOR_VALUE_PHYSICAL, 3,
                MPSModuleConstants.MODULE_TRADEOFF_PREFIX + MPSModuleConstants.ARMOR_POINTS);
        addBasePropertyDouble(MPALIbConstants.MAXIMUM_HEAT, 75);
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_LEATHER_PLATING__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(leather).getParticleTexture();
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.ARMOR;
    }
}