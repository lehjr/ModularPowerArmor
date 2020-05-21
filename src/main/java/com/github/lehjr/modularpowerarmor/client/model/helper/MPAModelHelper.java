package com.github.lehjr.modularpowerarmor.client.model.helper;

import com.github.lehjr.mpalib.capabilities.render.modelspec.ModelRegistry;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.ArrayList;

public class MPAModelHelper {
    // One pass just to register the textures called from texture stitch event
    // another to register the models called from model bake event (second run)

    // TODO: extract to subdir of config dir and scan for others ... also rename this


    public static void loadArmorModels(@Nullable TextureStitchEvent.Pre event, ModelLoader bakery) {
        ArrayList<String> resourceList = new ArrayList<String>() {{
            add("/assets/modularpowerarmor/modelspec/armor2.xml");
            add("/assets/modularpowerarmor/modelspec/default_armor.xml");
            add("/assets/modularpowerarmor/modelspec/default_armorskin.xml");
            add("/assets/modularpowerarmor/modelspec/armor_skin2.xml");
            add("/assets/modularpowerarmor/modelspec/default_powerfist.xml");
        }};

        for (String resourceString : resourceList) {
            parseSpecFile(resourceString, event, bakery);
        }
//
//        URL resource = MPSModelHelper.class.getResource("/assets/modularpowerarmor/models/item/armor/modelspec.xml");
//        ModelSpecXMLReader.INSTANCE.parseFile(resource, event);
//        URL otherResource = MPSModelHelper.class.getResource("/assets/modularpowerarmor/models/item/armor/armor2.xml");
//        ModelSpecXMLReader.INSTANCE.parseFile(otherResource, event);

//        ModelPowerFistHelper.INSTANCE.loadPowerFistModels(event);

        ModelRegistry.getInstance().getNames().forEach(name->System.out.println("modelregistry name: " + name));
    }

    public static void parseSpecFile(String resourceString, @Nullable TextureStitchEvent.Pre event, ModelLoader bakery) {
        URL resource = MPAModelHelper.class.getResource(resourceString);
        ModelSpecXMLReader.INSTANCE.parseFile(resource, event, bakery);
    }
}