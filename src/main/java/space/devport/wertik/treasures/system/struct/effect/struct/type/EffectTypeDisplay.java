package space.devport.wertik.treasures.system.struct.effect.struct.type;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import space.devport.utils.xseries.particles.ParticleDisplay;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.struct.effect.struct.BlockEffect;

public interface EffectTypeDisplay {

    void display(TreasurePlugin plugin, Location start, Location end, BlockEffect effect, ConfigurationSection section, ParticleDisplay display);
}
