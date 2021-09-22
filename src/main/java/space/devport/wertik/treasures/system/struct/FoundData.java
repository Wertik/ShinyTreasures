package space.devport.wertik.treasures.system.struct;

import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;

@Log
public class FoundData {

    private final Map<String, Boolean> foundTools = new HashMap<>();
    private final Map<String, Boolean> foundTemplates = new HashMap<>();

    public boolean hasToolBeenFound(String name) {
        return this.foundTools.getOrDefault(name, false);
    }

    public boolean hasTemplateBeenFound(String name) {
        return this.foundTemplates.getOrDefault(name, false);
    }

    public void setToolFound(String name) {
        this.foundTools.put(name, true);
        log.fine("Tool " + name + " set to found.");
    }

    public void setTemplateFound(String name) {
        this.foundTemplates.put(name, true);
        log.fine("Template " + name + " set to found.");
    }

    public void resetTool(String name) {
        this.foundTools.put(name, false);
        log.fine("Tool " + name + " found status reset.");
    }

    public void resetTemplate(String name) {
        this.foundTemplates.put(name, false);
        log.fine("Template " + name + " found status reset.");
    }

    public void reset() {
        resetTools();
        resetTemplates();
    }

    public void resetTools() {
        this.foundTools.clear();
    }

    public void resetTemplates() {
        this.foundTemplates.clear();
    }
}