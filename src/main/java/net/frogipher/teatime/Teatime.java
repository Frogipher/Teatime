package net.frogipher.teatime;

import static net.minecraft.item.Items.*;
import static net.minecraft.entity.effect.StatusEffects.*;

import net.fabricmc.api.ModInitializer;
import net.frogipher.teatime.registry.*;
import net.frogipher.teatime.tea.TeaInfusions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Teatime implements ModInitializer {
    public static final String MOD_ID = "teatime";
    public static final Logger LOGGER = LoggerFactory.getLogger("TeaTime");

    @Override
    public void onInitialize() {
        LOGGER.info("[TeaTime] init…");

        TeatimeSoundEvents.registerAll();
        TeatimeBlocks.registerAll();
        TeatimeItems.registerAll();
        TeatimeItemGroup.register();
        TeatimeBlockEntities.registerAll();

        registerDefaultInfusions();

        LOGGER.info("[TeaTime] ready");
    }

    private static void registerDefaultInfusions() {
        final int TEN_S  = 20 * 10;
        final int FIVE_S = 20 * 5;
        final int ONE_S = 20 * 1;

        TeaInfusions.add(ALLIUM, FIRE_RESISTANCE, TEN_S, 0);
        TeaInfusions.add(AZURE_BLUET, BLINDNESS, TEN_S, 0);
        TeaInfusions.add(BLUE_ORCHID, SATURATION, TEN_S, 0);
        TeaInfusions.add(DANDELION, SATURATION, TEN_S, 0);
        TeaInfusions.add(CORNFLOWER, JUMP_BOOST, TEN_S, 0);
        TeaInfusions.add(LILY_OF_THE_VALLEY, POISON, TEN_S, 0);
        TeaInfusions.add(OXEYE_DAISY, REGENERATION, TEN_S, 0);
        TeaInfusions.add(POPPY, NIGHT_VISION, TEN_S, 0);
        TeaInfusions.add(TORCHFLOWER, NIGHT_VISION, TEN_S, 0);
        TeaInfusions.add(RED_TULIP, WEAKNESS, TEN_S, 0);
        TeaInfusions.add(ORANGE_TULIP, WEAKNESS, TEN_S, 0);
        TeaInfusions.add(WHITE_TULIP, WEAKNESS, TEN_S, 0);
        TeaInfusions.add(PINK_TULIP, WEAKNESS, TEN_S, 0);
        TeaInfusions.add(WITHER_ROSE, WITHER, TEN_S, 0);

        TeaInfusions.add(SUNFLOWER, LUCK, ONE_S, 0);
        TeaInfusions.add(PEONY, REGENERATION, TEN_S, 0);
        TeaInfusions.add(LILAC, RESISTANCE, TEN_S, 0);
        TeaInfusions.add(ROSE_BUSH, STRENGTH, TEN_S, 0);

        TeaInfusions.add(BROWN_MUSHROOM, SLOWNESS, TEN_S, 0);
        TeaInfusions.add(RED_MUSHROOM, NAUSEA, TEN_S, 0);
        TeaInfusions.add(CRIMSON_FUNGUS, FIRE_RESISTANCE, TEN_S, 0);
        TeaInfusions.add(WARPED_FUNGUS, FIRE_RESISTANCE, TEN_S, 0);
        TeaInfusions.add(CRIMSON_ROOTS, HASTE, TEN_S, 0);
        TeaInfusions.add(WARPED_ROOTS, NIGHT_VISION, TEN_S, 0);

        TeaInfusions.add(HANGING_ROOTS, SATURATION, FIVE_S, 0);
        TeaInfusions.add(GLOW_LICHEN, NIGHT_VISION, TEN_S, 0);
        TeaInfusions.add(SWEET_BERRIES, SPEED, TEN_S, 0);
        TeaInfusions.add(GLOW_BERRIES, GLOWING, TEN_S, 0);
        TeaInfusions.add(SPORE_BLOSSOM, JUMP_BOOST, TEN_S, 0);
        TeaInfusions.add(DRIED_KELP, WATER_BREATHING, TEN_S, 0);
        TeaInfusions.add(HONEYCOMB, REGENERATION, TEN_S, 0);
        TeaInfusions.add(SEA_PICKLE, MINING_FATIGUE, TEN_S, 0);
        TeaInfusions.add(PINK_PETALS, LUCK, TEN_S, 0);
        TeaInfusions.add(CHORUS_FLOWER, LEVITATION, TEN_S, 0);
        TeaInfusions.add(CHORUS_FRUIT, LEVITATION, FIVE_S, 0);
        TeaInfusions.add(SUGAR, SPEED, FIVE_S, 0);
        TeaInfusions.add(APPLE, REGENERATION, TEN_S, 0);
        TeaInfusions.add(GOLDEN_APPLE, ABSORPTION, TEN_S, 0);
        TeaInfusions.add(MELON_SLICE, SATURATION, TEN_S, 0);
        TeaInfusions.add(PUMPKIN, RESISTANCE, TEN_S, 0);
        TeaInfusions.add(CARROT, NIGHT_VISION, TEN_S, 0);
        TeaInfusions.add(GOLDEN_CARROT, SATURATION, TEN_S, 0);
        TeaInfusions.add(POTATO, HASTE, TEN_S, 0);
        TeaInfusions.add(BEETROOT, HEALTH_BOOST, FIVE_S, 0);
        TeaInfusions.add(COCOA_BEANS, STRENGTH, TEN_S, 0);
        TeaInfusions.add(PITCHER_PLANT, POISON, TEN_S, 0);
        TeaInfusions.add(ROTTEN_FLESH, HUNGER, TEN_S, 0);
        TeaInfusions.add(SPIDER_EYE, POISON, TEN_S, 0);
        TeaInfusions.add(FERMENTED_SPIDER_EYE, UNLUCK, TEN_S, 0);
        TeaInfusions.add(CACTUS, INSTANT_DAMAGE, ONE_S, 0);
        TeaInfusions.add(GHAST_TEAR, FIRE_RESISTANCE, TEN_S, 0);
        TeaInfusions.add(MILK_BUCKET, STRENGTH, TEN_S, 0);
        TeaInfusions.add(HONEY_BOTTLE, SPEED, TEN_S, 0);
    }
}
