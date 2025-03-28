package edu.uob.command;

import edu.uob.EntityType;
import edu.uob.GameAction;
import edu.uob.GameManager;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.entity.interactableEntity.InteractableEntity;
import edu.uob.exception.SystemException;
import edu.uob.exception.UserException;

import java.util.*;

public class ActionCommand extends Command {
    private final GameAction action;

    public ActionCommand(GameManager gameManager, String player, GameAction action)
        throws SystemException {
        super(gameManager, player);
        this.action = action;
    }

    @Override
    protected String executeCommand() throws SystemException, UserException {
        Player player = this.gameManager.getPlayer(this.player);
        ActionContext context = new ActionContext(this.gameManager, player);

        // 1. perform action steps
        this.consumeEntities(context);
        this.produceEntities(context);
        this.consumeHealth(context);
        this.produceHealth(context);
        boolean isDied = this.checkPlayerDied(context);

        // 2. build result
        StringBuilder result = new StringBuilder(this.action.getNarration());
        if (isDied) {
            result
                .append("\nyou died and lost all of your items, ")
                .append("you must return to the start of the game");
        }
        return result.toString();
    }

    private void consumeEntities(ActionContext context) {
        Location currLocation = context.getCurrentLocation();
        Set<GameEntity> consumedEntities = context.getConsumedEntities();

        for (GameEntity entity : consumedEntities) {
            EntityType type = entity.getType();
            if (type == EntityType.LOCATION) {
                Location path = (Location) entity;
                currLocation.removePath(path.getName());
            } else if (type == EntityType.ARTEFACT
                       || type == EntityType.FURNITURE
                       || type == EntityType.CHARACTER
            ) {
                InteractableEntity interEntity = (InteractableEntity) entity;
                this.gameManager.moveInterEntity(interEntity.getName(), GameManager.STOREROOM_NAME);
                this.removeInvIfApplicable(interEntity, context.getPlayer());
            }
        }
    }

    private void produceEntities(ActionContext context) {
        Location currLocation = context.getCurrentLocation();
        Set<GameEntity> producedEntities = context.getProducedEntities();

        for (GameEntity entity : producedEntities) {
            EntityType type = entity.getType();
            if (type == EntityType.LOCATION) {
                Location location = (Location) entity;
                currLocation.addPath(location.getName());
            } else if (type == EntityType.ARTEFACT
                       || type == EntityType.FURNITURE
                       || type == EntityType.CHARACTER
            ) {
                InteractableEntity interEntity = (InteractableEntity) entity;
                this.gameManager.moveInterEntity(interEntity.getName(), currLocation.getName());
            }
        }
    }

    private void consumeHealth(ActionContext context) {
        context.getPlayer().deductHealth(action.getConsumedHealth());
    }

    private void produceHealth(ActionContext context) {
        context.getPlayer().addHealth(action.getProducedHealth());
    }

    private boolean checkPlayerDied(ActionContext context) throws UserException, SystemException {
        Player player = context.getPlayer();
        if (player.getHealth() != 0) return false;

        for (String inv : player.getInventories()) {
            DropCommand dropCommand = new DropCommand(this.gameManager, player.getName(), inv);
            dropCommand.execute();
        }
        this.gameManager.movePlayer(
            player.getName(), this.gameManager.getStartLocation().getName()
        );
        player.resetHealth();
        return true;
    }

    private void removeInvIfApplicable(InteractableEntity entity, Player player) {
        if (entity.getType() != EntityType.ARTEFACT) return;
        
        Artefact artefact = (Artefact) entity;
        if (Objects.equals(artefact.getBelongPlayer(), player.getName())) {
            artefact.setBelongPlayer(null);
        }
        player.removeInventory(artefact.getName());
    }

    private class ActionContext {
        private final GameManager gameManager;
        private final Player player;
        private final Location currLocation;

        public ActionContext(GameManager gameManager, Player player) {
            this.gameManager = gameManager;
            this.player = player;
            this.currLocation = gameManager.getLocation(player.getLocation());
        }

        public Player getPlayer() {return this.player;}
        public Location getCurrentLocation() {return this.currLocation;}

        public Set<GameEntity> getConsumedEntities() {
            return this.gameManager.getEntSetInLocation(action.getConsumedEntities(),
                this.currLocation, false, this.player);
        }

        public Set<GameEntity> getProducedEntities() {
            Set<GameEntity> producedEntities = new HashSet<>();
            for (String entityName : action.getProducedEntities()) {
                GameEntity targetEnt = this.getProducedEntity(entityName);
                if (targetEnt != null) producedEntities.add(targetEnt);
            }
            return producedEntities;
        }

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

    /* Utility */
    @Override
    public String toString() {
        return new StringBuilder()
            .append(super.toString()).append("\n")
            .append("action: ").append(this.action.toString())
            .toString();
    }
}
