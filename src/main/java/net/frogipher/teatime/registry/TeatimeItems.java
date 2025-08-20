package net.frogipher.teatime.registry;

import net.frogipher.teatime.items.TeaItem;
import net.frogipher.teatime.util.TeatimeIds;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class TeatimeItems {
    private TeatimeItems() {}

    public static Item TEACUP;
    public static Item TEA;

    private static Item register(String path, Item item) {
        return Registry.register(Registries.ITEM, TeatimeIds.id(path), item);
    }

    public static void registerAll() {
        TEACUP = register("teacup", new Item(new Item.Settings().maxCount(16)));
        TEA    = register("tea", new TeaItem(new Item.Settings().maxCount(16)));
    }
}
