package net.frogipher.teatime.tea;

import java.util.*;
import net.minecraft.item.Item;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

public final class TeaInfusions {
    private TeaInfusions() {}

    private static final Map<Item, List<StatusEffectInstance>> EFFECTS = new HashMap<>();

    public static void add(Item item, StatusEffect effect, int durationTicks, int amplifier) {
        EFFECTS.computeIfAbsent(item, i -> new ArrayList<>())
                .add(new StatusEffectInstance(effect, durationTicks, amplifier));
    }

    public static boolean isIngredient(Item item) {
        return EFFECTS.containsKey(item);
    }

    public static List<StatusEffectInstance> effectsFor(Item item) {
        return EFFECTS.getOrDefault(item, List.of());
    }
}
