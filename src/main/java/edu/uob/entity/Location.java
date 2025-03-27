package edu.uob.entity;

import edu.uob.EntityType;
import edu.uob.data.EntityData;

import java.util.HashSet;

public class Location extends GameEntity {
    private HashSet<String> interactableEntities;
    private HashSet<String> players;
    private HashSet<String> paths;

    public Location(EntityData data) {
        super(data, EntityType.LOCATION);

        this.interactableEntities = new HashSet<>();
        this.players = new HashSet<>();
        this.paths = new HashSet<>(); // TODO: should load after creation
    }

    /* Getter */
    public HashSet<String> getInterEntities() {
        return this.interactableEntities;
    }
    public HashSet<String> getPaths() {
        return this.paths;
    }
    public HashSet<String> getPlayers() {
        return this.players;
    }

    /* Creation */
    // TODO: handle error
    public void addInterEntity(String entityName) {this.interactableEntities.add(entityName);}

    public void addPlayer(String playerName) {
        this.players.add(playerName); // TODO: handle error
    }

    public void addPath(String pathName) { // TODO: check to add by Location[] or String[]
        this.paths.add(pathName); // TODO: handle error
    }

    public void addPaths(String[] paths) {
        for (String pathName : paths) this.addPath(pathName);
    }

    /* Deletion */
    // TODO: check exist or not?
    // TODO: handle error
    public void removeInterEntity(String name) {this.interactableEntities.remove(name);}

    // TODO: check exist or not?
    // TODO: handle error
    public void removePlayer(String playerName) {this.players.remove(playerName);}

    public void removePath(String pathName) { // TODO: check exist or not?
        this.paths.remove(pathName); // TODO: handle error
    }

    /* Utility */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(super.toString()).append("\n");
        result.append("entities: ").append(this.interactableEntities.toString()).append("\n");
        result.append("players: ").append(this.players.toString()).append("\n");
        result.append("paths: ").append(this.paths.toString());
        return result.toString();
    }
}
