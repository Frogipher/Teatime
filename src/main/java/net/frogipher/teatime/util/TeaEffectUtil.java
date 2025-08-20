package net.frogipher.teatime.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;

public final class TeaEffectUtil {
    private TeaEffectUtil() {}

    public static final String NBT_EFFECTS = "Effects";

    public static void applyEffectsFromTea(NbtCompound tag, LivingEntity entity) {
        if (tag == null || !tag.contains(NBT_EFFECTS, NbtElement.LIST_TYPE)) return;
        NbtList list = tag.getList(NBT_EFFECTS, NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound e = list.getCompound(i);
            String idStr = e.getString("id");
            StatusEffect effect = Registries.STATUS_EFFECT.get(IdentifierUtil.fromString(idStr));
            if (effect != null) {
                int dur = e.getInt("duration");
                int amp = e.getInt("amplifier");
                entity.addStatusEffect(new StatusEffectInstance(effect, dur, amp, false, true, true));
            }
        }
    }

    public static void applyEffectsFromTea(ItemStack teaStack, LivingEntity entity) {
        applyEffectsFromTea(teaStack.getNbt(), entity);
    }
}
