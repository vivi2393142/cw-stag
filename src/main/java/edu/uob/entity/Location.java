package edu.uob.entity;

import edu.uob.data.EntityData;
import edu.uob.entity.interactableEntity.InteractableEntity;

import java.util.HashMap;

public class Location extends GameEntity {
    private HashMap<String, InteractableEntity> interactableEntities;
    private HashMap<String, Player> players;
    private HashMap<String, Location> paths;

    public Location(EntityData data) {
        super(data, EntityType.LOCATION);

        this.interactableEntities = new HashMap<>();
        this.players = new HashMap<>();
        this.paths = new HashMap<>(); // TODO: should load after creation
    }

    /* Getter */
    public HashMap<String, InteractableEntity> getInteractableEntities() {
        return this.interactableEntities;
    }

    public HashMap<String, Player> getPlayers() {
        return this.players;
    }

    public HashMap<String, Location> getPaths() {
        return this.paths;
    }


    public Location getPath(String targetLocationName) {
        return this.paths.get(targetLocationName);
    }

    /* Creation */
    public void addInteractableEntity(InteractableEntity entity) {
        this.interactableEntities.put(entity.getName(), entity); // TODO: handle error
    }

    public void addPlayer(Player player) {
        this.players.put(player.getName(), player); // TODO: handle error
    }

    public void addPath(Location path) { // TODO: check to add by Location[] or String[]
        this.paths.put(path.getName(), path); // TODO: handle error
    }

    public void addPath(Location[] paths) {
        for (Location path : paths) {
            this.addPath(path);
        }
    }

    /* Deletion */
    public void removeInteractableEntity(String entityName) { // TODO: check exist or not?
        this.interactableEntities.remove(entityName); // TODO: handle error
    }

    public void removeInteractableEntity(InteractableEntity entity) { // TODO: check exist or not?
        this.removeInteractableEntity(entity.getName()); // TODO: handle error
    }

    public void removePlayer(String playerName) { // TODO: check exist or not?
        this.players.remove(playerName); // TODO: handle error
    }

    public void removePlayer(Player player) { // TODO: check exist or not?
        this.removePlayer(player.getName()); // TODO: handle error
    }

    public void removePath(String pathName) { // TODO: check exist or not?
        this.paths.remove(pathName); // TODO: handle error
    }

    public void removePath(Location path) { // TODO: check exist or not?
        this.removePath(path.getName()); // TODO: handle error
    }
}
