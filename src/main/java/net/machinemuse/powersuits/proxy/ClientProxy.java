package net.machinemuse.powersuits.proxy;

import net.machinemuse.numina.client.model.obj.MuseOBJLoader;
import net.machinemuse.powersuits.basemod.ModularPowersuits;
import net.machinemuse.powersuits.client.event.ModelBakeEventHandler;
import net.machinemuse.powersuits.client.event.RenderEventHandler;
import net.machinemuse.powersuits.client.render.entity.EntityRendererLuxCapacitorEntity;
import net.machinemuse.powersuits.client.render.entity.EntityRendererPlasmaBolt;
import net.machinemuse.powersuits.client.render.entity.EntityRendererSpinningBlade;
import net.machinemuse.powersuits.entity.LuxCapacitorEntity;
import net.machinemuse.powersuits.entity.PlasmaBoltEntity;
import net.machinemuse.powersuits.entity.SpinningBladeEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void setupClient(FMLClientSetupEvent event) {


    }
}
