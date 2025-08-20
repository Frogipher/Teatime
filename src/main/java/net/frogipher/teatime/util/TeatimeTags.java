package net.frogipher.teatime.util;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class TeatimeTags {
    private TeatimeTags() {}

    public static final TagKey<Block> HEAT_SOURCES =
            TagKey.of(RegistryKeys.BLOCK, TeatimeIds.id("heat_sources"));
}
