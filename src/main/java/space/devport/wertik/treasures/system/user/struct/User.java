package space.devport.wertik.treasures.system.user.struct;

import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {

    @Getter
    private final UUID uniqueID;

    private final Set<UUID> foundTreasures = new HashSet<>();

    public User(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    public Set<UUID> getFoundTreasures() {
        return Collections.unmodifiableSet(this.foundTreasures);
    }

    public int getFindCount() {
        return this.foundTreasures.size();
    }

    public void addFind(UUID uniqueID) {
        this.foundTreasures.add(uniqueID);
    }

    public void removeFind(UUID uniqueID) {
        this.foundTreasures.remove(uniqueID);
    }

    public boolean hasFound(UUID uniqueID) {
        return this.foundTreasures.contains(uniqueID);
    }
}
