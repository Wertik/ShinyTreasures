package space.devport.wertik.treasures;

import space.devport.utils.DevportPlugin;
import space.devport.utils.text.language.LanguageDefaults;

public class TreasureLanguage extends LanguageDefaults {

    public TreasureLanguage(DevportPlugin plugin) {
        super(plugin);
    }

    @Override
    public void setDefaults() {
        addDefault("Treasure.Found-Already", "&cYou found this treasure already!");
    }
}
