package edu.uob;

import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.entity.interactableEntity.InteractableEntity;

import java.util.*;

public class GameManager {
    public static final String STOREROOM_NAME = "storeroom";

    private final HashSet<GameAction> actions;
    private final HashMap<String, Location> locations;
    private final HashMap<String, Player> players;
    private final HashMap<String, InteractableEntity> entities;

    public Location startLocation;

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

    public Location getLocation(String locationName) {return this.locations.get(locationName);}
    public HashMap<String, Location> getLocations() {
        return this.locations;
    }

    public Player getPlayer(String playerName) {return this.players.get(playerName);}
    public HashMap<String, Player> getPlayers() {return this.players;}

    public InteractableEntity getEntity(String entityName) {return this.entities.get(entityName);}
    public HashMap<String, InteractableEntity> getEntities() {return this.entities;}

    public Location getStoreroom() {
        return this.getLocation(STOREROOM_NAME);
    }

    public Location getStartLocation() {return this.startLocation;}

    /* Setter */
    public void setStartLocation(Location location) {
        this.startLocation = location;
    }

    public void addPlayer(String playerName) {
        // TODO: check no reserved keywords
        // 1. create new player
        Player newPlayer = new Player(playerName, this.startLocation.getName());
        // 2. add player to startLocation
        this.startLocation.addPlayer(playerName);
        // 3. add player into gameManager
        this.players.put(playerName, newPlayer);
    }

    public void addLocation(Location location) {
        this.locations.put(location.getName(), location);
    }

    public void addEntity(InteractableEntity entity) {
        this.entities.put(entity.getName(), entity);
    }

    /* Utility: move */
    public void movePlayer(String playerName, String newLocationName) {
        Player player = this.getPlayer(playerName);
        Location currLocation = this.getLocation(player.getLocation());
        Location newLocation = this.getLocation(newLocationName);

        // 1. update player's location
        player.setLocation(newLocationName);
        // 2. remove player from the location
        currLocation.removePlayer(player.getName());
        // 3. add player to new location
        newLocation.addPlayer(player.getName());
    }

    public void moveInterEntity(String entityName, String newLocationName) {
        InteractableEntity entity = this.getEntity(entityName);
        Location currLocation = this.getLocation(entity.getLocation());
        Location newLocation = this.getLocation(newLocationName);

        // 1. update entity's location
        entity.setLocation(newLocation.getName());
        // 2. remove entity from the location
        currLocation.removeInterEntity(entityName);
        // 3. add entity to new location
        newLocation.addInterEntity(entityName);
    }

    /* Utility: get entities in location */
    public Set<GameEntity> getEntSetInLocation(Set<String> names, Location location,
                                               boolean includePlayer, Player includeInvOfPlayer) {
        Set<GameEntity> entities = new HashSet<>();

        // 1. get interactable entities from location
        entities.addAll(this.getInterEntInLocation(names, location));

        // 2. get paths from locations
        entities.addAll(this.getPathsInLocation(names, location));

        // 3. get players from location
        if (includePlayer) entities.addAll(this.getPlayersInLocation(names, location));

        // 4. get from player's inventory
        if (includeInvOfPlayer != null) {
            entities.addAll(this.getTargetInventory(names, includeInvOfPlayer));
        }

        return entities;
    }

    public boolean isAllEntAvailable(Set<String> names, Location location,
                                     boolean includePlayer, Player includeInvOfPlayer) {
        Set<GameEntity> entities = this.getEntSetInLocation(names, location,
            includePlayer, includeInvOfPlayer);
        return entities.size() == names.size();
    }

    public Set<GameEntity> getInterEntInLocation(Set<String> names, Location location) {
        HashSet<String> interEntities = location.getInterEntities();
        return new HashSet<>(this.getEntSetByNames(names, interEntities, InteractableEntity.class));
    }

    public Set<GameEntity> getPathsInLocation(Set<String> names, Location location) {
        return new HashSet<>(this.getEntSetByNames(names, location.getPaths(), Location.class));
    }

    public Set<GameEntity> getPlayersInLocation(Set<String> names, Location location) {
        return new HashSet<>(this.getEntSetByNames(names, location.getPlayers(), Player.class));
    }

    public Set<GameEntity> getTargetInventory(Set<String> names, Player player) {
        return new HashSet<>(this.getEntSetByNames(names, player.getInventories(), Artefact.class));
    }

    private <T extends GameEntity> Set<T> getEntSetByNames(Set<String> names,
                                                           Set<String> availableEntities,
                                                           Class<T> entityClass) {
        Set<T> result = new HashSet<>();
        for (String name : names) {
            this.addValidEntity(result, name, availableEntities, entityClass);
        }
        return result;
    }

    private <T extends GameEntity> Optional<T> getEntityByName(String name, Class<T> entityClass) {
        GameEntity entity = this.getEntity(name);
        if (entityClass.isInstance(entity)) return Optional.of(entityClass.cast(entity));
        return Optional.empty();
    }

    private <T extends GameEntity> void addValidEntity(Set<T> result, String name,
                                                       Set<String> entities,
                                                       Class<T> entityClass) {
        if (!entities.contains(name)) return;
        Optional<T> entityOpt = this.getEntityByName(name, entityClass);

        if (entityOpt.isEmpty()) return;

        T entity = entityOpt.get();
        result.add(entity);
    }
}
