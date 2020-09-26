package space.devport.wertik.treasures.commands;

import org.bukkit.command.CommandSender;
import space.devport.utils.text.StringUtil;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;

public class CommandParser {

    private final TreasurePlugin plugin;

    public CommandParser(TreasurePlugin plugin) {
        this.plugin = plugin;
    }

    public TreasureTemplate parseTemplate(CommandSender sender, String arg) {
        TreasureTemplate template = plugin.getTemplateManager().getTemplate(arg);

        if (template == null) {
            //TODO
            sender.sendMessage(StringUtil.color("&cInvalid template."));
            return null;
        }

        return template;
    }
}