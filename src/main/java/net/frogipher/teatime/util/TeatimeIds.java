package net.frogipher.teatime.util;

import net.frogipher.teatime.Teatime;
import net.minecraft.util.Identifier;

public final class TeatimeIds {
    private TeatimeIds() {}
    public static Identifier id(String path) {
        return new Identifier(Teatime.MOD_ID, path);
    }
}
