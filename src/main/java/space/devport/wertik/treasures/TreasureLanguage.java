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
        addDefault("Treasure.Disabled", "&cThis treasure is currently disabled.");
        addDefault("Treasure.No-Permission", "&cYou don't have permissions to open the treasure.");

        addDefault("Treasure.Admin.Removed", "&7Treasure removed.");

        addDefault("Commands.No-Treasures", "&cNo treasures placed yet.");

        addDefault("Commands.Invalid-Template", "&cTemplate &f%param% &cdoes not exist.");
        addDefault("Commands.Invalid-Tool", "&cTool &f%param% &cdoes not exist.");

        addDefault("Commands.Tools.Create.In-Session-Already", "&cYou are already in a session!");
        addDefault("Commands.Tools.Create.Already-Exists", "&cTool with the name &f%param% &calready exists.");
        addDefault("Commands.Tools.Create.Opening-Editor", "&7&oOpening chat editor...");

        addDefault("Commands.Tools.Delete.Done", "&7Tool &f%tool% &7deleted.");

        addDefault("Commands.Tools.Get.Done", "&7Tool &f%tool% &7given.");

        addDefault("Commands.Tools.List.No-Tools", "&cNo tools created yet.");
        addDefault("Commands.Tools.List.Header", "&8&m    &6 Loaded tools");
        addDefault("Commands.Tools.List.Line", "&8 - &e%toolName% &7( &f%rootTemplate%, %count% &7)");

        addDefault("Commands.Tools.Load.Could-Not", "&cCould not load tool with the name &f%param%");
        addDefault("Commands.Tools.Load.Done", "&7Loaded tool &f%tool% &7successfully.");
        addDefault("Commands.Tools.Load.Done-All", "&7Loaded all tools successfully.");

        addDefault("Commands.Tools.Reset.Tool-Done-All", "&7Successfully reset first finds of all tools.");
        addDefault("Commands.Tools.Reset.Tool-Done", "&7Tool first finds of &f%tool% &7reset successfully.");
        addDefault("Commands.Tools.Reset.Template-Done-All", "&7Successfully reset first finds of all templates.");
        addDefault("Commands.Tools.Reset.Template-Done", "&7Template first finds of &f%template% &7reset successfully.");
        addDefault("Commands.Tools.Reset.Done-All", "&7Successfully reset first finds of all tools and templates.");

        addDefault("Commands.Treasures.Delete.Invalid-Treasure", "&cThere is no treasure uuid that starts with &f%param%");
        addDefault("Commands.Treasures.Delete.Multiple-Results", "&cThere are multiple results starting with &f%param%",
                "&cUse -m to remove all of them, or provide more characters.");
        addDefault("Commands.Treasures.Delete.Done", "&7Deleted treasure &f%uuid% &7and removed all user references to it.");
        addDefault("Commands.Treasures.Delete.Done-Multiple", "&7Deleted &f%count% &7treasures and all user references to them.");

        addDefault("Commands.Treasures.List.Page-Not-Number", "&cPage has to be a positive number.");
        addDefault("Commands.Treasures.List.Not-Enough-For-Page", "&cNot enough treasures to display this page.");
        addDefault("Commands.Treasures.List.Header", "&8&m    &6 Treasures");
        addDefault("Commands.Treasures.List.Line", "&8 - &f%uuid% &7( %location%, %tool%, %rootTemplate% &7)");

        addDefault("Commands.Treasures.Purge-Invalid.No-Invalids", "&cNo invalid treasures.");
        addDefault("Commands.Treasures.Purge-Invalid.Removing", "&7&oRemoving &f&o%count% &7&otreasures...");
        addDefault("Commands.Treasures.Purge-Invalid.Done", "&7Done.");

        addDefault("Commands.Treasures.Teleport.Invalid-Treasure", "&cThere's not treasure with the UUID of &f%param%");
        addDefault("Commands.Treasures.Teleport.Multiple", "&cThere are more treasures start with &f%param%");
        addDefault("Commands.Treasures.Teleport.Teleporting", "&7&oTeleporting...");

        addDefault("Editor.Info.Header", "&6Chat editor options:");
        addDefault("Editor.Info.Material", "&ematerial &7(material) &8- &7Set the material for this tool.");
        addDefault("Editor.Info.List-Commands", "&elistcommands &8- &7List commands.");
        addDefault("Editor.Info.Add-Command", "&eaddcommand &7<command> &8- &7Add a command to the tool.");
        addDefault("Editor.Info.Remove-Command", "&eremovecommand &7<startOfTheCommand> &8- &7Remove a command.");
        addDefault("Editor.Info.Root-Template", "&eroottemplate &7<templateName> &8- &7Root tool to a template.");
        addDefault("Editor.Info.Footer", "&7Use &eexit &7or &ecancel &7to... exit the session without saving.",
                "&7Use &asave &7or &afinish &7to save & exit safely.");

        addDefault("Editor.Invalid-Argument", "&cArgument &f%param% &cis invalid.");
        addDefault("Editor.Not-Enough-Arguments", "&cNot enough arguments.");

        addDefault("Editor.Save.Done", "&7&oSaving and exiting...", "&7You can get your tool with &f/tt get &e%tool%");
        addDefault("Editor.Cancel.Done", "&7You're out. Everything was lost.");

        addDefault("Editor.List-Commands.No-Commands", "&cThere are no commands attached.");
        addDefault("Editor.List-Commands.Header", "&7Commands ( &f%count% &7) :");
        addDefault("Editor.List-Commands.Line", "&8 - &f%command%");

        addDefault("Editor.Remove-Command.No-Command", "&cSpecify a command to remove.");
        addDefault("Editor.Remove-Command.No-Commands", "&cThere are no commands attached.");
        addDefault("Editor.Remove-Command.Done", "&7Removed command &f%command%");

        addDefault("Editor.Add-Command.Done", "&7Added command &f%command%");

        addDefault("Editor.Material.Info", "&7Material: &f%material%");
        addDefault("Editor.Material.Invalid", "&cMaterial &f%param% &cis invalid.");
        addDefault("Editor.Material.Done", "&7Material set to &f%material%");

        addDefault("Editor.Root-Template.Info", "&7Root template: &f%template%");
        addDefault("Editor.Root-Template.Invalid", "&cTemplate &f%param% &cis invalid.");
        addDefault("Editor.Root-Template.Done", "&7Rooted tool to template &f%template%");
    }
}
