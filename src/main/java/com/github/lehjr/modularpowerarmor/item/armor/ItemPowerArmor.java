package com.github.lehjr.modularpowerarmor.item.armor;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.RegistryNames;
import com.github.lehjr.modularpowerarmor.client.model.item.armor.ArmorModelInstance;
import com.github.lehjr.modularpowerarmor.client.model.item.armor.HighPolyArmor;
import com.github.lehjr.modularpowerarmor.client.model.item.armor.IArmorModel;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.network.MPAPackets;
import com.github.lehjr.modularpowerarmor.network.packets.CosmeticInfoPacket;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.render.IArmorModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.client.render.modelspec.EnumSpecType;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * Describes the 4 different modular armor pieces - head, torso, legs, feet.
 *
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 11/4/16.
 */
public abstract class ItemPowerArmor extends ItemElectricArmor implements ISpecialArmor {
    public static final UUID[] ARMOR_MODIFIERS = new UUID[]{
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()};

    public ItemPowerArmor(String regName, String unlocalizedName, int renderIndex, EntityEquipmentSlot entityEquipmentSlot) {
        super(ItemArmor.ArmorMaterial.IRON, renderIndex, entityEquipmentSlot);
        this.setRegistryName(regName);
        this.setTranslationKey(new StringBuilder(Constants.MODID).append(".").append(unlocalizedName).toString());
        this.setMaxStackSize(1);
        this.setCreativeTab(MPAConfig.INSTANCE.mpsCreativeTab);
        this.setMaxDamage(0);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    /**
     * This just a method that determines whether or not otherwise unhandled damage sources are handled by the armor
     * <p>
     * Note, slot is equivilant to the EntityEquipmentSlot index (not slotIndex)
     */
    @Override
    public boolean handleUnblockableDamage(EntityLivingBase entity, @Nonnull ItemStack armor, DamageSource source, double damage, int slot) {
//            System.out.println("damage source: " + source.damageType);
//            System.out.println("slot: " + slot);

        if (source == null || source == HeatUtils.overheatDamage) {
            return false;
        }

        if (source.damageType.equals("electricity") || source.damageType.equals("radiation") || source.damageType.equals("sulphuric_acid")) {
            return java.util.Optional.ofNullable(armor
                    .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
                if (iItemHandler instanceof IModularItem) {
                    return ((IModularItem) iItemHandler).isModuleInstalled(new ResourceLocation(RegistryNames.MODULE_HAZMAT__REGNAME));
                }
                return false;
            }).orElse(false);
        }

        // Fixme: needs to check for Oxygen... needs to check for

//        // Galacticraft
//        if (slot == 3 && source.getDamageType().equals("oxygen_suffocation"))
//            return ModuleManager.INSTANCE.itemHasModule(armor, MPSModuleConstants.MODULE_AIRTIGHT_SEAL__REGNAME);









        // this still needs tweaking (extra planets)
        if (source.getDamageType().equals("pressure")) {
            if (slot == 3) {
                return java.util.Optional.ofNullable(armor
                        .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
                    if (iItemHandler instanceof IModularItem) {
                        return ((IModularItem) iItemHandler).isModuleInstalled(new ResourceLocation(RegistryNames.MODULE_AIRTIGHT_SEAL__REGNAME)) &&
                                ((IModularItem) iItemHandler).isModuleInstalled(new ResourceLocation(RegistryNames.MODULE_HAZMAT__REGNAME));
                    }
                    return false;
                }).orElse(false);
            } else {
                return java.util.Optional.ofNullable(armor
                        .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
                    if (iItemHandler instanceof IModularItem) {
                        ((IModularItem) iItemHandler).isModuleInstalled(new ResourceLocation(RegistryNames.MODULE_HAZMAT__REGNAME));
                    }
                    return false;
                }).orElse(false);
            }
        }

        if (source.getDamageType().equals("cryotheum"))
            return HeatUtils.getPlayerHeat((EntityPlayer) entity) > 0;


        // TODO: Galacticraft "thermal", "sulphuric_acid", "pressure"
        // TODO: Advanced Rocketry: CapabilitySpaceArmor.PROTECTIVEARMOR;


//        System.out.println("damage source: " + source.getDamageType() + " not protected. Damage ammount:" + damage );
        return false;
    }

    /**
     * Inherited from ISpecialArmor, allows us to customize how the armor
     * handles being damaged.
     */
    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            DamageSource overheatDamage = HeatUtils.overheatDamage;

//            if (source == null || source.equals(overheatDamage)) {
//                if (source != null)
//                    System.out.println("overheat damage: " + damage);
//
//
//                return;
//            }

            // cool the player instead of applying damage. Only fires if player has heat
            if (source.getDamageType().equals("cryotheum") && entity.world.isRemote)
                HeatUtils.coolPlayer(player, damage * 10);


            // FIXME: heat needs to either be applied here or in the player uodate handler through environment
            // isFireDamage includes heat related damage sources such as lava
            if (source.isFireDamage()) {

//                System.out.println("heat damage: " + damage);

//                if (!source.equals(DamageSource.ON_FIRE) ||
//                        HeatUtils.getPlayerHeat(player) < HeatUtils.getPlayerMaxHeat(player))
//                HeatUtils.heatPlayer(player, damage);

                HeatUtils.heatPlayer(player, damage * 5); // FIXME: this value needs tweaking. 10 too high, 1 too low
            } else {
                double enerConsum = java.util.Optional.ofNullable(stack
                        .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
                    if (iItemHandler instanceof IModularItem) {
                        double energyConsumption = 0D;
                        for (int i = 0; i < iItemHandler.getSlots(); i++) {
                            ItemStack module = iItemHandler.getStackInSlot(i);
                            energyConsumption += java.util.Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null))
                                    .map(pm-> pm.applyPropertyModifiers(Constants.ARMOR_ENERGY_CONSUMPTION)).orElse(0D);
                        }
                    }
                    return 0D;
                }).orElse(0D);

                double drain = enerConsum * damage;
                ElectricItemUtils.drainPlayerEnergy((EntityPlayer) entity, (int) drain);
            }
        }
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        double absorbRatio = 0.25; // 25% for each armor piece;
        int absorbMax = (int) (25 * damage);
        int priority = 0;

        // Fire damage is just heat based damage like fire or lava
        if (source.isFireDamage() && !(source.equals(HeatUtils.overheatDamage))) { //  heat damage is only 1 point ?
//            System.out.println("heat damage: " + damage);

            return new ISpecialArmor.ArmorProperties(priority, absorbRatio, absorbMax);
        }

        // hazmat handled hazards
        if (java.util.Optional.ofNullable(armor
                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
            if (iItemHandler instanceof IModularItem) {
                ((IModularItem) iItemHandler).isModuleInstalled(new ResourceLocation(RegistryNames.MODULE_HAZMAT__REGNAME));
            }
            return false;
        }).orElse(false) &&
                (source.damageType.equals("electricity") ||
                        source.damageType.equals("radiation") ||
                        source.damageType.equals("sulphuric_acid"))) {
            return new ISpecialArmor.ArmorProperties(priority, absorbRatio, absorbMax);
        }

        double armorDouble;
        if (player instanceof EntityPlayer) {
            armorDouble = this.getArmorDouble((EntityPlayer) player, armor);
        } else {
            armorDouble = 2.0;
        }

        absorbMax = (int) armorDouble * 75;
        if (source.isUnblockable()) {
            absorbMax = 0;
            absorbRatio = 0.0;
        } else {
            absorbRatio = 0.04 * armorDouble;
        }

        return new ISpecialArmor.ArmorProperties(priority, absorbRatio, absorbMax);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

        if (slot == this.armorType) {
            multimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()], SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), 0.25, 0));
            if (java.util.Optional.ofNullable(stack
                    .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
                if (iItemHandler instanceof IModularItem) {
                    return ((IModularItem) iItemHandler).isModuleInstalled(new ResourceLocation(RegistryNames.MODULE_DIAMOND_PLATING__REGNAME)) ||
                            ((IModularItem) iItemHandler).isModuleInstalled(new ResourceLocation(RegistryNames.MODULE_ENERGY_SHIELD__REGNAME));
                }
                return false;
            }).orElse(false)) {
                multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()], "Armor toughness", 2.5, 0));
            }
        }
        return multimap;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return (int) this.getArmorDouble(player, armor);
    }

    public double getArmorDouble(EntityPlayer player, ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
                .map(iItemHandler -> {
                    double totalArmor = 0D;
                    if (iItemHandler instanceof IModularItem) {
                        double energy = ElectricItemUtils.getPlayerEnergy(player);
                        AtomicDouble physArmor = new AtomicDouble(0D);
                        AtomicDouble enerArmor = new AtomicDouble(0D);
                        AtomicDouble enerConsum = new AtomicDouble(0D);
                        for (int i = 0; i < iItemHandler.getSlots(); i++) {
                            Optional.ofNullable(iItemHandler.getStackInSlot(i).getCapability(PowerModuleCapability.POWER_MODULE, null))
                                    .ifPresent(pm->{
                                        physArmor.getAndAdd(pm.applyPropertyModifiers(Constants.ARMOR_VALUE_PHYSICAL));
                                        enerArmor.getAndAdd(pm.applyPropertyModifiers(Constants.ARMOR_VALUE_ENERGY));
                                        enerConsum.getAndAdd(pm.applyPropertyModifiers(Constants.ARMOR_ENERGY_CONSUMPTION));
                                    });
                        }
                        totalArmor += physArmor.get();
                        if (energy > enerConsum.get()) {
                            totalArmor += enerArmor.get();
                        }
                        totalArmor = Math.min(MPAConfig.INSTANCE.getMaximumArmorPerPiece(), totalArmor);
                        return totalArmor;
                    }
                    return totalArmor;
                }).orElse(0D);
    }

    @Override
    public boolean showDurabilityBar(final ItemStack stack) {
        int capacity = 0;
        final IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage != null)
            capacity = energyStorage.getMaxEnergyStored();

        if (capacity > 0)
            return true;
        return false;
    }

    @Override
    public double getDurabilityForDisplay(final ItemStack stack) {
        final IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return 1 - (energyStorage != null ? energyStorage.getEnergyStored() / (float) energyStorage.getMaxEnergyStored() : 0);
    }

    // Cosmetics ----------------------------------------------------------------------------------

    @Override
    public boolean hasColor(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack armor, Entity entity, EntityEquipmentSlot equipmentSlotType, String type) {
        if (type == "overlay") { // this is to allow a tint to be applied tot the armor
            return MPALIbConstants.BLANK_ARMOR_MODEL_PATH;
        }
        return Optional.ofNullable(armor.getCapability(ModelSpecNBTCapability.RENDER, null)).map(spec->
                spec instanceof IArmorModelSpecNBT ?
                        ((IArmorModelSpecNBT) spec).getArmorTexture() :
                        MPALIbConstants.BLANK_ARMOR_MODEL_PATH).orElse(MPALIbConstants.BLANK_ARMOR_MODEL_PATH);
    }


    @Override
    public boolean hasOverlay(ItemStack stack) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack armor, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        if (!(entityLiving instanceof EntityPlayer)) {
            return _default;
        }


        return Optional.ofNullable(armor.getCapability(ModelSpecNBTCapability.RENDER, null)).map(spec-> {

            NBTTagCompound renderTag = spec.getMuseRenderTag();
            EntityPlayer player = (EntityPlayer) entityLiving;

            // only triggered by this client's player looking at their own equipped armor
            if (renderTag == null || renderTag.isEmpty() && player == Minecraft.getMinecraft().player) {
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    if (player.inventory.getStackInSlot(i).equals(armor)) {
                        renderTag = spec.getDefaultRenderTag();
                        if (renderTag != null && !renderTag.isEmpty()) {
                            spec.setMuseRenderTag(renderTag, MPALIbConstants.TAG_RENDER);
                            MPAPackets.INSTANCE.sendToServer(new CosmeticInfoPacket(i, MPALIbConstants.TAG_RENDER, renderTag));
                        }
                        break;
                    }
                }
            }

            if (spec.getMuseRenderTag() != null &&
                    (spec.getSpecType() == EnumSpecType.ARMOR_SKIN || spec.getSpecType() == EnumSpecType.NONE)) {
                return _default;
            }

            ModelBiped model = ArmorModelInstance.getInstance();
            if (Optional.ofNullable(armor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler ->
                    iItemHandler instanceof IModularItem && ((IModularItem) iItemHandler)
                            .isModuleOnline(new ResourceLocation(RegistryNames.MODULE_ACTIVE_CAMOUFLAGE__REGNAME))).orElse(false)) {
//                System.out.println("setting active camoflage on");

                ((IArmorModel) model).setVisibleSection(null);
            } else {
                if (renderTag != null) {
                    ((HighPolyArmor) model).setVisibleSection(armorSlot);
                    ((HighPolyArmor) model).setRenderSpec(renderTag);
                }
            }
            return model;
        }).orElse(_default);
    }
}