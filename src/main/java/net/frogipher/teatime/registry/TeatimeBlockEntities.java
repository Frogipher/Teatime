package net.frogipher.teatime.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.frogipher.teatime.util.TeatimeIds;
import net.frogipher.teatime.blocks.KettleBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class TeatimeBlockEntities {
    private TeatimeBlockEntities() {}

    public static BlockEntityType<KettleBlockEntity> KETTLE;

    public static void registerAll() {
        KETTLE = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                TeatimeIds.id("kettle"),
                FabricBlockEntityTypeBuilder.create(KettleBlockEntity::new, TeatimeBlocks.KETTLE).build()
        );
    }
}
