package net.frogipher.teatime.advancement;

import net.frogipher.teatime.Teatime;
import net.frogipher.teatime.util.TeatimeIds;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class TeatimeAdvancements {
    private TeatimeAdvancements() {}

    public static final Identifier ROOT           = TeatimeIds.id("root");
    public static final Identifier EXTRACTED_TEA  = TeatimeIds.id("extracted_tea");
    public static final Identifier TRIPLE_POUR    = TeatimeIds.id("triple_pour");
    public static final Identifier PLACE_KETTLE   = TeatimeIds.id("place_kettle");

    private static final String CRITERION = "served";

    private static void grant(ServerPlayerEntity player, Identifier id) {
        Advancement adv = player.getServer().getAdvancementLoader().get(id);
        if (adv == null) {
            Teatime.LOGGER.warn("[TeaTime] Missing advancement JSON: {}", id);
            return;
        }
        boolean ok = player.getAdvancementTracker().grantCriterion(adv, CRITERION);
        if (!ok) {
            Teatime.LOGGER.debug("[TeaTime] Grant failed or already complete: {} ({})", id, CRITERION);
        }
    }

    public static void onServe(ServerPlayerEntity player, int count) {
        grant(player, ROOT);
        grant(player, EXTRACTED_TEA);
        if (count >= 3) {
            grant(player, TRIPLE_POUR);
        }
    }

    public static void onKettlePlaced(ServerPlayerEntity player) {
        grant(player, ROOT);
        grant(player, PLACE_KETTLE);
    }
}
