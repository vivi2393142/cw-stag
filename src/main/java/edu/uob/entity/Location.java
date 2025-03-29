package edu.uob.entity;

import edu.uob.EntityType;
import edu.uob.data.EntityData;

import java.util.HashSet;

public class Location extends GameEntity {
    private final HashSet<String> interactableEntities;
    private final HashSet<String> players;
    private final HashSet<String> paths;

    public Location(EntityData data) {
        super(data, EntityType.LOCATION);

        this.interactableEntities = new HashSet<>();
        this.players = new HashSet<>();
        this.paths = new HashSet<>();
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
    public void addInterEntity(String entityName) {this.interactableEntities.add(entityName);}

    public void addPlayer(String playerName) {this.players.add(playerName);}

    public void addPath(String pathName) {this.paths.add(pathName);}

    /* Deletion */
    public void removeInterEntity(String name) {this.interactableEntities.remove(name);}

    public void removePlayer(String playerName) {this.players.remove(playerName);}

    public void removePath(String pathName) {
        this.paths.remove(pathName);
    }

    /* Utility */
    @Override
    public String toString() {
        return new StringBuilder(super.toString())
            .append("\n")
            .append("entities: ").append(this.interactableEntities.toString()).append("\n")
            .append("players: ").append(this.players.toString()).append("\n")
            .append("paths: ").append(this.paths.toString())
            .toString();
    }
}
