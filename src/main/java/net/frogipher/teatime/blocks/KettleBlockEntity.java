package net.frogipher.teatime.blocks;

import java.util.ArrayList;
import java.util.List;

import net.frogipher.teatime.registry.TeatimeBlockEntities;
import net.frogipher.teatime.tea.TeaInfusions;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KettleBlockEntity extends BlockEntity {
    public static final String NBT_EFFECTS     = "Effects";
    public static final String NBT_INGREDIENTS = "Ingredients";

    private static final long BREW_TICKS          = 20L * 30L;
    private static final long BOIL_SOUND_INTERVAL = 30L;
    private static final long WHISTLE_INTERVAL    = 40L;
    private static final float BOIL_VOLUME        = 0.6F;

    private boolean hasWater      = false;
    private boolean brewed        = false;
    private long brewStartTick    = -1L;
    private long lastBoilSoundTick= 0L;
    private long lastWhistleTick  = 0L;
    private int servingsRemaining = 0;

    private final List<Item> ingredients = new ArrayList<>();
    private final NbtList compiledEffects = new NbtList();

    public KettleBlockEntity(BlockPos pos, BlockState state) {
        super(TeatimeBlockEntities.KETTLE, pos, state);
    }

    public boolean hasWater() { return hasWater; }
    public void setWater(boolean v) { this.hasWater = v; markDirty(); }
    public boolean isBrewed() { return brewed; }

    public void addIngredient(Item item) {
        ingredients.add(item);
        markDirty();
    }

    public void maybeStart(World world, BlockPos pos) {
        if (!hasWater || ingredients.isEmpty()) return;
        if (brewStartTick < 0 && KettleBlock.isHeated(world, pos)) {
            brewStartTick = world.getTime();
            markDirty();
        }
    }

    public void primeBoil(long now) {
        this.lastBoilSoundTick = now - BOIL_SOUND_INTERVAL;
    }

    public NbtList copyCompiledEffects() {
        NbtList copy = new NbtList();
        for (int i = 0; i < compiledEffects.size(); i++) {
            copy.add(compiledEffects.getCompound(i).copy());
        }
        return copy;
    }

    public NbtList copyIngredientIdList() {
        NbtList list = new NbtList();
        for (Item it : ingredients) {
            list.add(NbtString.of(Registries.ITEM.getId(it).toString()));
        }
        return list;
    }

    public int getServingsRemaining() { return servingsRemaining; }
    public void decrementServings(int n) {
        servingsRemaining = Math.max(0, servingsRemaining - n);
        markDirty();
    }

    public void reset() {
        hasWater = false;
        brewed = false;
        brewStartTick = -1L;
        lastBoilSoundTick = 0L;
        lastWhistleTick = 0L;
        servingsRemaining = 0;
        ingredients.clear();
        compiledEffects.clear();
        markDirty();
    }

    public static void tickServer(World world, BlockPos pos, BlockState state, KettleBlockEntity be) {
        boolean heated   = KettleBlock.isHeated(world, pos);
        boolean hasWater = be.hasWater;

        boolean shouldLit = heated && hasWater;
        if (state.get(KettleBlock.LIT) != shouldLit) {
            world.setBlockState(pos, state.with(KettleBlock.LIT, shouldLit), 3);
            state = world.getBlockState(pos);
        }

        if (be.brewed && be.servingsRemaining > 0) {
            if (world.getTime() - be.lastWhistleTick >= WHISTLE_INTERVAL) {
                KettleBlock.whistle(world, pos);
                be.lastWhistleTick = world.getTime();
            }
            return;
        }

        if (shouldLit && !be.brewed && world.getTime() - be.lastBoilSoundTick >= BOIL_SOUND_INTERVAL) {
            world.playSound(null, pos, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT,
                    SoundCategory.BLOCKS, BOIL_VOLUME, 0.85F + world.random.nextFloat() * 0.2F);
            if (world.random.nextInt(4) == 0) {
                world.playSound(null, pos, SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP,
                        SoundCategory.BLOCKS, 0.35F, 0.9F + world.random.nextFloat() * 0.2F);
            }
            be.lastBoilSoundTick = world.getTime();
        }

        if (!(hasWater && !be.ingredients.isEmpty() && heated)) {
            be.brewStartTick = -1L; // pause
            return;
        }

        if (be.brewStartTick < 0) be.brewStartTick = world.getTime();

        long elapsed = world.getTime() - be.brewStartTick;
        if (elapsed >= BREW_TICKS) be.finishBrew(world, pos);
    }

    private void finishBrew(World world, BlockPos pos) {
        compiledEffects.clear();

        for (Item item : ingredients) {
            for (StatusEffectInstance inst : TeaInfusions.effectsFor(item)) {
                NbtCompound e = new NbtCompound();
                e.putString("id", Registries.STATUS_EFFECT.getId(inst.getEffectType()).toString());
                e.putInt("duration", inst.getDuration());
                e.putInt("amplifier", inst.getAmplifier());
                compiledEffects.add(e);
            }
        }

        brewed = true;
        servingsRemaining = 3;
        brewStartTick = -1L;

        lastWhistleTick = world.getTime() - WHISTLE_INTERVAL;
        KettleBlock.whistle(world, pos);

        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) { super.writeNbt(nbt); }
    @Override
    public void readNbt(NbtCompound nbt) { super.readNbt(nbt); }
}
