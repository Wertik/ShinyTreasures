package space.devport.wertik.treasures.system.struct.effect.struct.sound;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.item.Amount;
import space.devport.utils.utility.ParseUtil;
import space.devport.utils.xseries.XSound;

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

        XSound sound = ParseUtil.parseEnum(section.getString("sound"), XSound.class);

        if (sound == null)
            return null;

        ConsoleOutput.getInstance().debug("Loaded sound at " + configuration.getFile().getName() + "@" + section.getCurrentPath());
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
