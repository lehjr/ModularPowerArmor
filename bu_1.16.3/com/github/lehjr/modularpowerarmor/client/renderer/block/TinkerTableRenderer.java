package com.github.lehjr.modularpowerarmor.client.renderer.block;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.client.model.block.TinkerTableModel;
import com.github.lehjr.modularpowerarmor.tile_entity.TinkerTableTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;

public class TinkerTableRenderer extends TileEntityRenderer<TinkerTableTileEntity> {
    private static final ResourceLocation textureLocation = new ResourceLocation(MPAConstants.MOD_ID, "textures/models/tinkertable_tx.png");
    TinkerTableModel model = new TinkerTableModel();

    public TinkerTableRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TinkerTableTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        model.render(matrixStackIn, bufferIn.getBuffer(model.getRenderType(textureLocation)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
