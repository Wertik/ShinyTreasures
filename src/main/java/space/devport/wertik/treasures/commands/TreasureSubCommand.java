package space.devport.wertik.treasures.commands;

import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.wertik.treasures.TreasurePlugin;

public abstract class TreasureSubCommand extends SubCommand {

    protected final TreasurePlugin plugin;

    public TreasureSubCommand(TreasurePlugin plugin, String name) {
        super(name);
        this.plugin = plugin;
        setPermissions();
    }

    @Override
    public abstract @Nullable String getDefaultUsage();

    @Override
    public abstract @Nullable String getDefaultDescription();

    @Override
    public abstract @Nullable ArgumentRange getRange();
}