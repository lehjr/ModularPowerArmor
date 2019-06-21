package net.machinemuse.powersuits.item.module.environmental;

import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class FluidTankModule extends AbstractPowerModule {
    public FluidTankModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {



        return null;
    }


    /*
    package net.machinemuse.powersuits.powermodule.armor;

import net.machinemuse.api.IModularItem;
import net.machinemuse.api.ModuleManager;
import net.machinemuse.api.moduletrigger.IPlayerTickModule;
import net.machinemuse.powersuits.item.ItemComponent;
import net.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.machinemuse.utils.MuseCommonStrings;
import net.machinemuse.utils.MuseHeatUtils;
import net.machinemuse.utils.MuseItemUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;

import java.util.List;

/**
 * Created by User: Andrew2448
 * 4:35 PM 6/21/13
 */
    public class WaterTankModule extends PowerModuleBase implements IPlayerTickModule {
        public static final String MODULE_WATER_TANK = "Water Tank";
        public static final String WATER_TANK_SIZE = "Tank Size";
        public static final String ACTIVATION_PERCENT = "Heat Activation Percent";
        final ItemStack bucketWater = new ItemStack(Items.WATER_BUCKET);

        public WaterTankModule(List<IModularItem> validItems) {
            super(validItems);
            addBaseProperty(WATER_TANK_SIZE, 200);
            addBaseProperty(MuseCommonStrings.WEIGHT, 1000);
            addBaseProperty(ACTIVATION_PERCENT, 0.5);
            addTradeoffProperty("Activation Percent", ACTIVATION_PERCENT, 0.5, "%");
            addTradeoffProperty("Tank Size", WATER_TANK_SIZE, 800, " buckets");
            addTradeoffProperty("Tank Size", MuseCommonStrings.WEIGHT, 4000, "g");
            addInstallCost(bucketWater);
            addInstallCost(new ItemStack(Blocks.GLASS, 8));
            addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.controlCircuit, 2));
        }

        @Override
        public TextureAtlasSprite getIcon(ItemStack item) {
            return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(bucketWater).getParticleTexture();
        }

        @Override
        public String getCategory() {
            return MuseCommonStrings.CATEGORY_ENVIRONMENTAL;
        }

        @Override
        public String getDataName() {
            return MODULE_WATER_TANK;
        }

        @Override
        public String getUnlocalizedName() {
            return "waterTank";
        }











        }

        @Override
        public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
        }
    }
     */
}
