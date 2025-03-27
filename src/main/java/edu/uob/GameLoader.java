package edu.uob;

import edu.uob.data.EntityData;
import edu.uob.data.PathData;
import edu.uob.data.InteractableEntityData;
import edu.uob.entity.Location;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.entity.interactableEntity.Character;
import edu.uob.entity.interactableEntity.Furniture;
import edu.uob.entity.interactableEntity.InteractableEntity;
import edu.uob.parser.ActionsParser;
import edu.uob.parser.EntitiesParser;

import java.io.File;

public class GameLoader {
    private GameManager gameManager;

    public GameLoader(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void load(File entitiesFile, File actionsFile) {
        try {
            EntityData requireRoom = new EntityData(GameManager.STOREROOM_NAME,
                "Storage for any entities not placed in the game");
            EntitiesParser entityParser = new EntitiesParser(entitiesFile, requireRoom);
            ActionsParser actionsParser = new ActionsParser(actionsFile);

            EntityData[] loadedLocations = entityParser.getLocations();
            PathData[] loadedPaths = entityParser.getPaths();
            InteractableEntityData[] loadedEntities = entityParser.getEntities();
            GameAction[] loadedActions = actionsParser.getActions();

            this.load(loadedLocations, loadedPaths, loadedEntities, loadedActions);
        } catch (Exception e) {
            // TODO: handle error
        }
    }


    public void load(EntityData[] loadedLocations, PathData[] loadedPaths,
                     InteractableEntityData[] loadedEntities, GameAction[] loadedActions) {
        this.loadLocations(loadedLocations, loadedPaths);
        this.loadInterEntities(loadedEntities);
        this.loadActions(loadedActions);
    }


    /* Load Locations */
    public void loadLocations(EntityData[] loadedLocations, PathData[] loadedPaths) {
        // load locations
        boolean isFirst = true;
        for (EntityData loadedLocation : loadedLocations) {
            Location newLocation = this.loadLocation(loadedLocation);
            if (isFirst) {
                this.gameManager.setStartLocation(newLocation);
                isFirst = false;
            }
        }

        // load path
        for (PathData loadedPath : loadedPaths) this.loadPath(loadedPath);
    }

    public Location loadLocation(EntityData data) {
        Location newLocation = new Location(data);
        this.gameManager.addLocation(newLocation);
        return newLocation;
    }

    public void loadPath(PathData data) {
        Location fromLocation = this.gameManager.getLocation(data.getFromLocationName());
        fromLocation.addPath(data.getToLocationName());
    }

    /* Load Entities */
    public void loadInterEntities(InteractableEntityData[] loadedEntities) {
        for (InteractableEntityData entity : loadedEntities) this.loadInterEntity(entity);
    }

    public void loadInterEntity(InteractableEntityData data) {
        // TODO: add comment
        String entityName = data.getName();
        String targetLocationName = data.getLocationName();
        Location targetLocation = this.gameManager.getLocation(data.getLocationName());
        InteractableEntity newEntity = switch (data.getType()) {
            case CHARACTER -> new Character(data, targetLocationName);
            case ARTEFACT -> new Artefact(data, targetLocationName);
            case FURNITURE -> new Furniture(data, targetLocationName);
            // TODO: handle error
            default -> throw new IllegalArgumentException("Invalid entity type");
        };

        this.gameManager.addEntity(newEntity);
        targetLocation.addInterEntity(entityName);
    }

    /* Load Actions */
    public void loadActions(GameAction[] loadedActions) {
        for (GameAction loadedAction : loadedActions) loadAction(loadedAction);
    }

    public void loadAction(GameAction loadedAction) {
        this.gameManager.getActions().add(loadedAction);
    }
}
