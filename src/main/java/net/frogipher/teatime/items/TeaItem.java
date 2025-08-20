package net.frogipher.teatime.items;

import java.util.LinkedHashMap;
import java.util.Map;

import net.frogipher.teatime.blocks.KettleBlockEntity;
import net.frogipher.teatime.registry.TeatimeItems;
import net.frogipher.teatime.util.TeaEffectUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.text.MutableText;

public class TeaItem extends Item {
    public TeaItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override public int getMaxUseTime(ItemStack stack) { return 32; }
    @Override public UseAction getUseAction(ItemStack stack) { return UseAction.DRINK; }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            TeaEffectUtil.applyEffectsFromTea(stack, user);
        }

        if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
            stack.decrement(1);
            if (stack.isEmpty()) return new ItemStack(TeatimeItems.TEACUP);

            ItemStack cup = new ItemStack(TeatimeItems.TEACUP);
            if (!player.getInventory().insertStack(cup)) player.dropItem(cup, false);
        }
        return stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, java.util.List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return;

        if (nbt.contains(KettleBlockEntity.NBT_INGREDIENTS, NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList(KettleBlockEntity.NBT_INGREDIENTS, NbtElement.STRING_TYPE);
            if (list.isEmpty()) return;

            tooltip.add(Text.translatable("tooltip.teatime.ingredients").formatted(Formatting.GRAY));

            Map<Identifier, Integer> counts = new LinkedHashMap<>();
            for (int i = 0; i < list.size(); i++) {
                Identifier id = Identifier.tryParse(list.getString(i));
                if (id != null) counts.merge(id, 1, Integer::sum);
            }

            for (Map.Entry<Identifier, Integer> e : counts.entrySet()) {
                var item = Registries.ITEM.get(e.getKey());
                Text name = new ItemStack(item).getName();
                int c = e.getValue();

                MutableText line = Text.literal("• ").formatted(Formatting.DARK_GRAY);
                line.append(name.copy().formatted(Formatting.WHITE));
                if (c > 1) {
                    line.append(Text.literal(" x" + c).formatted(Formatting.GRAY));
                }
                tooltip.add(line);
            }
        }
    }

}
