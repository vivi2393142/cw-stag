package edu.uob.command;

import edu.uob.GameAction;
import edu.uob.GameManager;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.InteractableEntity;

import java.util.HashSet;
import java.util.Set;

// context for ActionCommand
public class ActionContext {
    private final GameAction action;
    private final GameManager gameManager;
    private final Player player;
    private final Location currLocation;

    public ActionContext(GameAction action, GameManager gameManager, Player player) {
        this.action = action;
        this.gameManager = gameManager;
        this.player = player;
        this.currLocation = gameManager.getLocation(player.getLocation());
    }

    public Player getPlayer() {return this.player;}
    public Location getCurrentLocation() {return this.currLocation;}

    // get all consumed entities in location for this action
    public Set<GameEntity> getConsumedEntities() {
        return this.gameManager.getEntSetInLocation(this.action.getConsumedEntities(),
            this.currLocation, false, this.player);
    }

    // get all produced entities from valid location e.g. storeroom
    public Set<GameEntity> getProducedEntities() {
        Set<GameEntity> producedEntities = new HashSet<>();
        for (String entityName : this.action.getProducedEntities()) {
            GameEntity targetEnt = this.getProducedEntity(entityName);
            if (targetEnt != null) producedEntities.add(targetEnt);
        }
        return producedEntities;
    }

    // get a produced entity by name from storeroom
    public GameEntity getProducedEntity(String producedEntity) {
        // 1. is a location, get from gameManager
        Location location = this.gameManager.getLocation(producedEntity);
        if (location != null) return location;

        // 2. is other entity, get from storeroom
        InteractableEntity entity = this.gameManager.getEntity(producedEntity);
        if (entity != null &&
            entity.getLocation().equalsIgnoreCase(GameManager.STOREROOM_NAME)
        ) {
            return entity;
        }

        return null;
    }
}
