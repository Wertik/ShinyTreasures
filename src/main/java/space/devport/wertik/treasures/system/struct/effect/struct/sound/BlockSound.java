package space.devport.wertik.treasures.system.struct.effect.struct.sound;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.configuration.Configuration;
import space.devport.dock.lib.xseries.XSound;
import space.devport.dock.struct.Amount;
import space.devport.dock.util.ParseUtil;

@Log
public class BlockSound {

    @Getter
    @Setter
    private XSound sound;

    @Setter
    private Amount pitch;

    @Setter
    private Amount volume;

    public BlockSound(XSound sound, Amount pitch, Amount volume) {
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
    }

    public static BlockSound load(Configuration configuration, ConfigurationSection section) {

        if (section == null)
            return null;

        Amount pitch = configuration.getAmount(section.getCurrentPath() + ".pitch", new Amount(1));
        Amount volume = configuration.getAmount(section.getCurrentPath() + ".volume", new Amount(1));

        XSound sound = ParseUtil.parseEnum(section.getString("sound"), XSound.class).orElse(null);

        if (sound == null)
            return null;

        log.fine("Loaded sound at " + configuration.getFile().getName() + "@" + section.getCurrentPath());
        return new BlockSound(sound, pitch, volume);
    }

    public void play(@NotNull Location location) {
        sound.play(location, (float) getPitch(), (float) getVolume());
    }

    public double getPitch() {
        return this.pitch.getDouble();
    }

    public double getVolume() {
        return this.volume.getDouble();
    }
}
