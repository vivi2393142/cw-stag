package edu.uob;

import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.InteractableEntity;

import java.util.HashMap;
import java.util.HashSet;

public class GameManager {
    public static final String STOREROOM_NAME = "storeroom";

    private HashSet<GameAction> actions;
    private HashMap<String, Location> locations;
    private HashMap<String, Player> players;
    private HashMap<String, InteractableEntity> entities;

    private Location startLocation;

    public GameManager() {
        this.actions = new HashSet<>();
        this.locations = new HashMap<>();
        this.players = new HashMap<>();
        this.entities = new HashMap<>();
    }

    /* Getter */
    public HashSet<GameAction> getActions() {
        return this.actions;
    }

    public Location getLocation(String locationName) {return this.locations.get(locationName);}// TODO: check exist?
    public HashMap<String, Location> getLocations() {
        return this.locations;
    }

    public Player getPlayer(String playerName) {return this.players.get(playerName);}// TODO: check exist?
    public HashMap<String, Player> getPlayers() {
        return this.players;
    }

    public InteractableEntity getEntity(String entityName) {return this.entities.get(entityName);} // TODO: check exist?
    public HashMap<String, InteractableEntity> getEntities() {
        return this.entities;
    }

    public Location getStoreroom() {
        return this.getLocation(STOREROOM_NAME); // TODO: check exist?
    }

    public Location getStartLocation() {return this.startLocation;}

    /* Setter */
    public void setStartLocation(Location location) {
        this.startLocation = location;
    }

    public Player addPlayer(String playerName) {
        // TODO: check no reserved keywords
        Player newPlayer = new Player(playerName, this.startLocation);
        this.players.put(playerName, newPlayer);
        return newPlayer;
    }

    public void addLocation(Location location) {
        this.locations.put(location.getName(), location);
    }
}
