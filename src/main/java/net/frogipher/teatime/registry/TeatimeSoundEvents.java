package net.frogipher.teatime.registry;

import net.frogipher.teatime.util.TeatimeIds;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public final class TeatimeSoundEvents {
    private TeatimeSoundEvents() {}

    public static SoundEvent KETTLE_WHISTLE;

    public static void registerAll() {
        KETTLE_WHISTLE = Registry.register(Registries.SOUND_EVENT, TeatimeIds.id("kettle_whistle"),
                SoundEvent.of(TeatimeIds.id("kettle_whistle")));
    }
}
