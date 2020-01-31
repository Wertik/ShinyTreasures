package space.devport.wertik.simpletreasures;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import space.devport.wertik.simpletreasures.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Treasure {

    private int id;

    private Location location;

    private List<String> finders = new ArrayList<>();

    private List<String> specificCommands = new ArrayList<>();

    public List<String> getSpecificCommands() {
        return specificCommands;
    }

    public void setSpecificCommands(List<String> specificCommands) {
        this.specificCommands = specificCommands;
    }

    public void addSpecificCommand(String cmd) {
        this.specificCommands.add(cmd);
    }

    public Treasure(int id, Location location) {
        this.id = id;
        this.location = location;
    }

    public Treasure(int id, Location location, List<String> finders) {
        this(id, location);
        this.finders = finders;
    }

    @Override
    public String toString() {

        StringBuilder strB = new StringBuilder();

        strB.append((Utils.locationToString(location))).append(":");
        strB.append(Utils.listToString(finders)).append(":");

        specificCommands.forEach(cmd -> strB.append(cmd).append(";"));

        return strB.toString();
    }

    public static Treasure fromString(int id, String str) {

        Treasure t =  new Treasure(id,
                Utils.stringToLocation(str.split(":")[0]),
                str.split(":").length > 1 ? Utils.stringToList(str.split(":")[1]) : new ArrayList<>());

        for (String cmd : str.split(":")[2].split(";"))
            t.addSpecificCommand(cmd);

        return t;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void addFinder(Player player) {
        if (!finders.contains(player.getUniqueId().toString()))
            finders.add(player.getUniqueId().toString());
    }

    public boolean foundAlready(Player player) {
        return finders.contains(player.getUniqueId().toString());
    }

    public List<String> getFinders() {
        return finders;
    }

    public void setFinders(List<String> finders) {
        this.finders = finders;
    }
}
