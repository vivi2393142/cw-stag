package edu.uob;

import edu.uob.data.ActionData;
import edu.uob.entity.*;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.entity.interactableEntity.InteractableEntity;

import java.util.HashMap;
import java.util.HashSet;

public class GameAction {
    private final HashSet<String> triggers;
    private final HashSet<String> subjects; // can be any kind of entities
    private final HashSet<String> consumedEntities; // can be any kind of entities
    private final HashSet<String> producedEntities; // can be any kind of entities
    private final String narration;

    public GameAction(ActionData data) {
        this.triggers = data.getTriggers();
        this.subjects = data.getSubjects();
        this.consumedEntities = data.getConsumedEntities();
        this.producedEntities = data.getProducedEntities();
        this.narration = data.getNarration(); // TODO: add to functions
    }

    public String execute(GameManager gameManager, Player player) {
        Context context = new Context(gameManager, player);
        boolean isValid = this.validateAndUpdateContext(context);
        if (!isValid) return "Fail to execute because ..."; // TODO: adjust error message

        this.consumeEntities(context);
        this.produceEntities(context);
        return this.narration;
    }

    /* Utility */
    private boolean validateAndUpdateContext(Context context) {
        EntityColl subjectColl = this.getEntityCollByNames(this.subjects, context);
        EntityColl consumingColl = this.getEntityCollByNames(this.consumedEntities, context);
        EntityColl producingColl = this.getEntityCollByNames(this.producedEntities, context, false);

        boolean isExecutable =
                this.subjects.size() == subjectColl.getTotalCtn() &&
                this.consumedEntities.size() == consumingColl.getTotalCtn() &&
                this.producedEntities.size() == producingColl.getTotalCtn();
        if (!isExecutable) return false; // return false if any entity is missing

        context.setSubjectColl(subjectColl);
        context.setConsumingColl(consumingColl);
        context.setProducingColl(producingColl);
        return true;
    }

    private void consumeEntities(Context context) {
        // consume entities: update entity location to storeroom, remove from location, add to storeroom
        for (InteractableEntity entity : context.getConsumingColl().getInteractableEntities()) {
            entity.moveLocation(context.getStoreroom());
            this.removeInvIfNeeded(entity, context.getPlayer());
        }
        // consume locations: remove path
        for (Location path : context.getConsumingColl().getLocations()) {
            context.getCurrLocation().removePath(path);
        }
    }

    private void produceEntities(Context context) {
        // produce entities: update entity location to current, remove from storeroom, add to current location
        for (InteractableEntity entity : context.getProducingColl().getInteractableEntities()) {
            entity.moveLocation(context.getCurrLocation());
        }
        // produce locations: add path
        for (Location path : context.getProducingColl().getLocations()) {
            context.getCurrLocation().addPath(path);
        }
    }

    private EntityColl getEntityCollByNames(HashSet<String> names, Context context) {
        return this.getEntityCollByNames(names, context, true);
    }

    private EntityColl getEntityCollByNames(
            HashSet<String> names, Context context, boolean includedPlayers) {
        EntityColl coll = new EntityColl();
        Location currLocation = context.getCurrLocation();
        Player player = context.getPlayer();

        coll.addInteractableEntities(this.getEntitiesByNames(names, currLocation.getInteractableEntities(), player));
        coll.addLocations(this.getEntitiesByNames(names, currLocation.getPaths(), player));
        if (includedPlayers) coll.addPlayers(this.getEntitiesByNames(names, currLocation.getPlayers(), player));
        return coll;
    }

    private <T extends GameEntity> HashSet<T> getEntitiesByNames(
            HashSet<String> names, HashMap<String, T> entities, Player player) {
        HashSet<T> targetEntities = new HashSet<>();
        for (String entityName : names) {
            T targetEntity = entities.get(entityName);
            if (targetEntity != null &&
                (targetEntity.getType() != GameEntity.EntityType.ARTEFACT ||
                 ((Artefact) targetEntity).getBelongPlayer() == player)
            ) {
                targetEntities.add(targetEntity);
            }
        }
        return targetEntities;
    }

    private void removeInvIfNeeded(InteractableEntity entity, Player player) {
        if (entity.getType() == GameEntity.EntityType.ARTEFACT) {
            Artefact consumedInv = (Artefact) entity;
            if (consumedInv.getBelongPlayer() == player) {
                consumedInv.setBelongPlayer(null);
                player.removeInventory(consumedInv);
            }
        }
    }

    /* Nested Classes */
    private static class Context {
        private final GameManager gameManager;
        private final Player player;
        private EntityColl subjectColl;
        private EntityColl consumingColl;
        private EntityColl producingColl;

        public Context(GameManager gameManager, Player player) {
            this.gameManager = gameManager;
            this.player = player;
            this.subjectColl = new EntityColl();
            this.consumingColl = new EntityColl();
            this.producingColl = new EntityColl();
        }

        public Player getPlayer() {return player;}
        public Location getCurrLocation() {return player.getLocation();}
        public Location getStoreroom() {return gameManager.getStoreroom();}
        public void setSubjectColl(EntityColl coll) {this.subjectColl = coll;}
        public EntityColl getConsumingColl() {return consumingColl;}
        public void setConsumingColl(EntityColl coll) {this.consumingColl = coll;}
        public EntityColl getProducingColl() {return producingColl;}
        public void setProducingColl(EntityColl coll) {this.producingColl = coll;}
    }

    private static class EntityColl {
        private final HashSet<InteractableEntity> interactableEntities;
        private final HashSet<Location> locations;
        private final HashSet<Player> players;

        public EntityColl() {
            this.interactableEntities = new HashSet<>();
            this.locations = new HashSet<>();
            this.players = new HashSet<>();
        }

        public HashSet<InteractableEntity> getInteractableEntities() {return interactableEntities;}
        public void addInteractableEntities(HashSet<InteractableEntity> entities) {
            this.interactableEntities.addAll(entities);
        }
        public HashSet<Location> getLocations() {return locations;}
        public void addLocations(HashSet<Location> locations) {this.locations.addAll(locations);}
        public void addPlayers(HashSet<Player> players) {this.players.addAll(players);}
        public int getTotalCtn() {return interactableEntities.size() + locations.size() + players.size();}
    }
}
