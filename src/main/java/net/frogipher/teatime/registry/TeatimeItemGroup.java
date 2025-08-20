package net.frogipher.teatime.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.frogipher.teatime.util.TeatimeIds;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public final class TeatimeItemGroup {
    private TeatimeItemGroup() {}

    public static ItemGroup TEATIME;

    public static void register() {
        TEATIME = Registry.register(Registries.ITEM_GROUP, TeatimeIds.id("teatime"),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.teatime"))
                        .icon(() -> new ItemStack(TeatimeItems.TEA))
                        .entries((ctx, entries) -> {
                            entries.add(TeatimeBlocks.KETTLE.asItem());
                            entries.add(TeatimeItems.TEACUP);
                            entries.add(TeatimeItems.TEA);
                        })
                        .build()
        );
    }
}
