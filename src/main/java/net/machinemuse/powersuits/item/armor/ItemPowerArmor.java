package net.machinemuse.powersuits.item.armor;

import com.google.common.collect.Multimap;
import net.machinemuse.numina.basemod.NuminaConstants;
import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.capabilities.render.IArmorModelSpecNBT;
import net.machinemuse.numina.capabilities.render.ModelSpecNBTCapability;
import net.machinemuse.numina.client.render.modelspec.EnumSpecType;
import net.machinemuse.powersuits.basemod.MPSRegistryNames;
import net.machinemuse.powersuits.client.model.item.ArmorModelInstance;
import net.machinemuse.powersuits.client.model.item.HighPolyArmor;
import net.machinemuse.powersuits.event.RegisterStuff;
import net.machinemuse.powersuits.network.MPSPackets;
import net.machinemuse.powersuits.network.packets.MusePacketCosmeticInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.UUID;

public class ItemPowerArmor extends ItemElectricArmor {
    public ItemPowerArmor(EquipmentSlotType slots) {
        super(slots, new Item.Properties().group(RegisterStuff.INSTANCE.creativeTab).maxStackSize(1).defaultMaxDamage(0));
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
//    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{
//            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
//            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
//            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
//            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    public static final UUID[] ARMOR_MODIFIERS = new UUID[]{
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()};

//    @Override
//    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slotType) {
//        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slotType);

//        return multimap;
//    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack itemStack) {
//        System.out.println("doing something here");

        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, itemStack);
        if (equipmentSlot != this.slot) {
            return multimap;
        }

//        AtomicDouble armorVal = new AtomicDouble(0);
//        AtomicDouble toughnessVal = new AtomicDouble(0);
//        AtomicDouble knockbackResistance = new AtomicDouble(0);
//
//        itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
//            if (iItemHandler instanceof IModularItem) {
//                // Armor **should** only occupy one slot
//                Pair<Integer, Integer> range = ((IModularItem) iItemHandler).getRangeForCategory(EnumModuleCategory.ARMOR);
//                if (range != null) {
//                    for (int i = range.getLeft(); i < range.getRight(); i++) {
//                        iItemHandler.getStackInSlot(i).getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(pm -> {
//                            if (pm.isAllowed()) {
//                                // physical armor and hybrid energy/physical armor
//                                double armorDouble = pm.applyPropertyModifiers(MPSConstants.ARMOR_VALUE_PHYSICAL);
//                                double knockBack = 0;
//
//                                if (pm instanceof IToggleableModule && ((IToggleableModule) pm).isModuleOnline()) {
//                                    armorDouble += pm.applyPropertyModifiers(MPSConstants.ARMOR_VALUE_ENERGY);
//                                }
//
//                                if (armorDouble > 0) {
//                                    knockBack = pm.applyPropertyModifiers(MPSConstants.KNOCKBACK_RESISTANCE);
//                                    armorVal.getAndAdd(armorDouble);
//                                }
//
//                                if (knockBack > 0) {
//                                    knockbackResistance.getAndAdd(knockBack);
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        });
//
//        multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", armorVal.get(), AttributeModifier.Operation.ADDITION));
//        multimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Knockback resistance", knockbackResistance.get(), AttributeModifier.Operation.ADDITION));
//        multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor toughness", toughnessVal.get(), AttributeModifier.Operation.ADDITION));
//


        multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", 6, AttributeModifier.Operation.ADDITION));
        multimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Knockback resistance", 0.25, AttributeModifier.Operation.ADDITION));
        multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor toughness", 2.5, AttributeModifier.Operation.ADDITION));

        return multimap;
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack armor, Entity entity, EquipmentSlotType equipmentSlotType, String type) {
        if (type == "overlay") { // this is to allow a tint to be applied tot the armor
            return NuminaConstants.BLANK_ARMOR_MODEL_PATH;
        }
        return armor.getCapability(ModelSpecNBTCapability.RENDER).map(spec->
                spec instanceof IArmorModelSpecNBT ?
                        ((IArmorModelSpecNBT) spec).getArmorTexture() :
                        NuminaConstants.BLANK_ARMOR_MODEL_PATH).orElse(NuminaConstants.BLANK_ARMOR_MODEL_PATH);
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel _default) {
        if (!(entityLiving instanceof PlayerEntity)) {
            return _default;
        }


        return itemStack.getCapability(ModelSpecNBTCapability.RENDER).map(spec-> {

            CompoundNBT renderTag = spec.getMuseRenderTag();
            PlayerEntity player = (PlayerEntity) entityLiving;

            // only triggered by this client's player looking at their own equipped armor
            if (renderTag == null || renderTag.isEmpty() && player == Minecraft.getInstance().player) {
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    if (player.inventory.getStackInSlot(i).equals(itemStack)) {
                        renderTag = spec.getDefaultRenderTag();
                        if (renderTag != null && !renderTag.isEmpty()) {
                            spec.setMuseRenderTag(renderTag, NuminaConstants.TAG_RENDER);
                            MPSPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketCosmeticInfo(i, NuminaConstants.TAG_RENDER, renderTag));
                        }
                        break;
                    }
                }
            }

            if (spec.getMuseRenderTag() != null &&
                    (spec.getSpecType() == EnumSpecType.ARMOR_SKIN || spec.getSpecType() == EnumSpecType.NONE)) {
                return _default;
            }

            BipedModel model = ArmorModelInstance.getInstance();
            if (slot == EquipmentSlotType.CHEST && itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(iItemHandler ->
                    iItemHandler instanceof IModularItem && ((IModularItem) iItemHandler)
                            .isModuleOnline(new ResourceLocation(MPSRegistryNames.MODULE_ACTIVE_CAMOUFLAGE__REGNAME))).orElse(false)) {
//                System.out.println("setting active camoflage on");

                ((HighPolyArmor) model).setVisibleSection(null);
            } else {
                if (renderTag != null) {
                    ((HighPolyArmor) model).setVisibleSection(slot);
                    ((HighPolyArmor) model).setRenderSpec(renderTag);
                }

//                System.out.println("render tag: " + renderTag);
            }
            return model;
        }).orElse(_default);
    }

    @Override
    public boolean showDurabilityBar(final ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map( energyCap-> energyCap.getMaxEnergyStored() > 0).orElse(false);
    }

    @Override
    public double getDurabilityForDisplay(final ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map( energyCap-> 1 - energyCap.getEnergyStored() / (double) energyCap.getMaxEnergyStored()).orElse(1D);
    }
}
