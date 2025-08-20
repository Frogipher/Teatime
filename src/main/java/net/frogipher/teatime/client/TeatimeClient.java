package net.frogipher.teatime.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.frogipher.teatime.registry.TeatimeBlocks;
import net.minecraft.client.render.RenderLayer;

public final class TeatimeClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(TeatimeBlocks.KETTLE, RenderLayer.getCutout());

    }
}
