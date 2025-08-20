package net.frogipher.teatime.util;

import net.minecraft.util.Identifier;

public final class IdentifierUtil {
    private IdentifierUtil() {}
    public static Identifier fromString(String id) {
        return Identifier.tryParse(id);
    }
}
