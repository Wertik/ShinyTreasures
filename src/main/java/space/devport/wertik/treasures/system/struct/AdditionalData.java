package space.devport.wertik.treasures.system.struct;

import space.devport.utils.ConsoleOutput;

import java.util.HashMap;
import java.util.Map;

public class AdditionalData {

    private final Map<String, Boolean> foundTools = new HashMap<>();
    private final Map<String, Boolean> foundTemplates = new HashMap<>();

    public boolean hasToolBeenFound(String name) {
        return this.foundTools.containsKey(name) && this.foundTools.get(name);
    }

    public boolean hasTemplateBeenFound(String name) {
        return this.foundTemplates.containsKey(name) && this.foundTemplates.get(name);
    }

    public void setToolFound(String name) {
        this.foundTools.put(name, true);
        ConsoleOutput.getInstance().debug("Tool " + name + " set to found.");
    }

    public void setTemplateFound(String name) {
        this.foundTemplates.put(name, true);
        ConsoleOutput.getInstance().debug("Template " + name + " set to found.");
    }

    public void resetTool(String name) {
        this.foundTools.put(name, false);
        ConsoleOutput.getInstance().debug("Tool " + name + " found status reset.");
    }

    public void resetTemplate(String name) {
        this.foundTemplates.put(name, false);
        ConsoleOutput.getInstance().debug("Template " + name + " found status reset.");
    }

    public void resetTools() {
        this.foundTools.clear();
    }

    public void resetTemplates() {
        this.foundTemplates.clear();
    }
}