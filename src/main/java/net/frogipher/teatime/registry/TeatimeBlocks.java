package net.frogipher.teatime.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.frogipher.teatime.util.TeatimeIds;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class TeatimeBlocks {
    private TeatimeBlocks() {}

    public static Block KETTLE;

    private static Block register(String path, Block block, boolean withItem) {
        Block b = Registry.register(Registries.BLOCK, TeatimeIds.id(path), block);
        if (withItem) {
            Registry.register(Registries.ITEM, TeatimeIds.id(path),
                    new BlockItem(b, new Item.Settings().maxCount(1)));
        }
        return b;
    }

    public static void registerAll() {
        KETTLE = register(
                "kettle",
                new net.frogipher.teatime.blocks.KettleBlock(
                        net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings.create()
                                .strength(0.8F)
                                .nonOpaque()
                                .sounds(net.minecraft.sound.BlockSoundGroup.COPPER)
                ),
                true
        );
    }
}
