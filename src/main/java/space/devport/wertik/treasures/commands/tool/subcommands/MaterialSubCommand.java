package space.devport.wertik.treasures.commands.tool.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.utils.text.StringUtil;
import space.devport.utils.xseries.XMaterial;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;
import space.devport.wertik.treasures.system.editor.struct.EditSession;

public class MaterialSubCommand extends TreasureSubCommand {

    public MaterialSubCommand(TreasurePlugin plugin) {
        super(plugin, "material");
        this.preconditions = new Preconditions()
                .playerOnly();
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        Player player = (Player) sender;
        EditSession session = getPlugin().getEditorManager().getSession(player);

        if (session == null) {
            //TODO no session
            sender.sendMessage(StringUtil.color("&cNo session."));
            return CommandResult.FAILURE;
        }

        XMaterial material = XMaterial.matchXMaterial(args[0]).orElse(null);

        if (material == null || material.parseMaterial() == null) {
            //TODO
            sender.sendMessage(StringUtil.color("&cInvalid material."));
            return CommandResult.FAILURE;
        }

        session.getTool().getTemplate().setMaterial(material.parseMaterial());
        //TODO
        sender.sendMessage(StringUtil.color("&aMaterial set."));
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% material <material>";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Set the material.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(1);
    }
}