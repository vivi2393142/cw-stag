package edu.uob;

import edu.uob.data.ActionData;
import edu.uob.data.EntityData;
import edu.uob.data.PathData;
import edu.uob.data.InteractableEntityData;
import edu.uob.entity.Location;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.entity.interactableEntity.Character;
import edu.uob.entity.interactableEntity.Furniture;
import edu.uob.entity.interactableEntity.InteractableEntity;

import java.io.File;

public class GameLoader {
    private GameManager gameManager;

    public GameLoader(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void load(File entitiesFile, File actionsFile) {
        EntityData[] loadedLocations = {}; // TODO: get from file
        PathData[] loadedPaths = {};
        InteractableEntityData[] loadedEntities = {};  // TODO: get from file
        ActionData[] loadedActions = {};  // TODO: get from file

        this.load(loadedLocations, loadedPaths, loadedEntities, loadedActions);
    }


    public void load(EntityData[] loadedLocations, PathData[] loadedPaths, InteractableEntityData[] loadedEntities,
                     ActionData[] loadedActions) {
        this.loadLocations(loadedLocations, loadedPaths);
        this.loadInteractableEntities(loadedEntities);
        this.loadActions(loadedActions);
    }


    /* load locations */
    public void loadLocations(EntityData[] loadedLocations, PathData[] loadedPaths) {
        // TODO: verify storeroom
        boolean isFirst = true;
        for (EntityData loadedEntity : loadedLocations) {
            Location newLocation = this.loadLocation(loadedEntity);
            if (isFirst) {
                this.gameManager.setStartLocation(newLocation);
                isFirst = false;
            }
        }
        for (PathData loadedPath : loadedPaths) this.loadPath(loadedPath);
    }

    public Location loadLocation(EntityData data) {
        Location newLocation = new Location(data);
        this.gameManager.addLocation(newLocation);
        return newLocation;
    }

    public void loadPath(PathData data) {
        Location fromLocation = this.gameManager.getLocation(data.getFromLocationName());
        Location toLocation = this.gameManager.getLocation(data.getToLocationName());
        fromLocation.addPath(toLocation);
    }

    /* load entities */
    public void loadInteractableEntities(InteractableEntityData[] loadedEntities) {
        for (InteractableEntityData loadedEntity : loadedEntities) this.loadInteractableEntity(loadedEntity);
    }

    public void loadInteractableEntity(InteractableEntityData data) {
        Location targetLocation = this.gameManager.getLocation(data.getLocationName());
        InteractableEntity newEntity = switch (data.getType()) {
            case CHARACTER -> new Character(data, targetLocation);
            case ARTEFACT -> new Artefact(data, targetLocation);
            case FURNITURE -> new Furniture(data, targetLocation);
            // TODO: handle error
            default -> throw new IllegalArgumentException("Invalid entity type for InteractableEntity.");
        };

        this.gameManager.getEntities().put(data.getName(), newEntity);
        targetLocation.addInteractableEntity(newEntity);
    }

    /* load actions */
    public void loadActions(ActionData[] loadedActions) {
        for (ActionData loadedAction : loadedActions) loadAction(loadedAction);
    }

    public void loadAction(ActionData loadedAction) {
        GameAction newAction = new GameAction(loadedAction);
        this.gameManager.getActions().add(newAction);
    }
}
