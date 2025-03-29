package edu.uob.command;

import edu.uob.EntityType;
import edu.uob.GameAction;
import edu.uob.GameManager;
import edu.uob.ValueUtils;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.entity.interactableEntity.InteractableEntity;
import edu.uob.exception.SystemException;

import java.util.*;

// handle custom action command
public class ActionCommand extends Command {
    private final GameAction action;

    public ActionCommand(GameManager gameManager, String player, GameAction action)
        throws SystemException {
        super(gameManager, player);
        this.action = action;
    }

    protected String executeCommand() {
        Player player = this.gameManager.getPlayer(this.player);
        ActionContext context = new ActionContext(this.action, this.gameManager, player);

        // perform action steps
        this.consumeEntities(context);
        this.produceEntities(context);
        this.consumeHealth(context);
        this.produceHealth(context);
        boolean isDied = this.checkPlayerDied(context);

        return this.getMessage(isDied);
    }

    protected String getMessage(boolean isDied) {
        StringBuilder result = new StringBuilder(this.action.getNarration());
        if (isDied) {
            result
                .append("\nyou died and lost all of your items, ")
                .append("you must return to the start of the game");
        }
        return result.toString();
    }

    // handle consume in command
    private void consumeEntities(ActionContext context) {
        Location currLocation = context.getCurrentLocation();
        Set<GameEntity> consumedEntities = context.getConsumedEntities();

        // loop through consumedEntities
        for (GameEntity entity : consumedEntities) {
            EntityType type = entity.getType();
            if (type == EntityType.LOCATION) { // if location, remove path
                Location path = (Location) entity;
                currLocation.removePath(path.getName());
            } else if (ValueUtils.isInteractableEntity(type)) { // if not, move to storeroom
                InteractableEntity interEntity = (InteractableEntity) entity;
                this.gameManager.moveInterEntity(interEntity.getName(), GameManager.STOREROOM_NAME);
                this.removeInvIfApplicable(interEntity, context.getPlayer());
            }
        }
    }

    // handle produce in command
    private void produceEntities(ActionContext context) {
        Location currLocation = context.getCurrentLocation();
        Set<GameEntity> producedEntities = context.getProducedEntities();

        // loop through producedEntities
        for (GameEntity entity : producedEntities) {
            EntityType type = entity.getType();
            if (type == EntityType.LOCATION) { // if location, add path to current location
                Location location = (Location) entity;
                currLocation.addPath(location.getName());
            } else if (ValueUtils.isInteractableEntity(type)) { // if not, get it from storeroom
                InteractableEntity interEntity = (InteractableEntity) entity;
                this.gameManager.moveInterEntity(interEntity.getName(), currLocation.getName());
            }
        }
    }

    private void consumeHealth(ActionContext context) {
        context.getPlayer().deductHealth(this.action.getConsumedHealth());
    }

    private void produceHealth(ActionContext context) {
        context.getPlayer().addHealth(this.action.getProducedHealth());
    }

    private boolean checkPlayerDied(ActionContext context) {
        // 1. get player and check health
        Player player = context.getPlayer();
        if (player.getHealth() != 0) return false;

        // 2. if died, drop all inventories
        this.dropPlayerInventories(player);

        // 3. move player to start location
        this.gameManager.movePlayer(
            player.getName(), this.gameManager.getStartLocation().getName()
        );
        player.resetHealth();
        return true;
    }

    // move all artefacts from player's inventory to current location
    private void dropPlayerInventories(Player player) {
        Location currLocation = this.gameManager.getLocation(player.getLocation());
        for (String inv : player.getInventories()) {
            InteractableEntity entity = this.gameManager.getEntity(inv);
            if (entity.getType() == EntityType.ARTEFACT) {
                Artefact inventory = (Artefact) entity;
                // 1. update artefact's location & belongPlayer
                inventory.setLocation(player.getLocation());
                // 2. add artefact to current location
                currLocation.addInterEntity(inventory.getName());
            }
        }
        // 3. remove all artefacts from player's inventories
        player.removeAllInventory();
    }

    private void removeInvIfApplicable(InteractableEntity entity, Player player) {
        if (entity.getType() != EntityType.ARTEFACT) return;

        Artefact artefact = (Artefact) entity;
        if (Objects.equals(artefact.getBelongPlayer(), player.getName())) {
            artefact.setBelongPlayer(null);
        }
        player.removeInventory(artefact.getName());
    }

    /* Utility */
    @Override
    public String toString() {
        return new StringBuilder()
            .append(super.toString()).append("\n")
            .append("action: ").append(this.action.toString())
            .toString();
    }
}
