package edu.uob;

import edu.uob.data.EntityData;
import edu.uob.data.PathData;
import edu.uob.data.InteractableEntityData;
import edu.uob.entity.Location;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.entity.interactableEntity.Character;
import edu.uob.entity.interactableEntity.Furniture;
import edu.uob.entity.interactableEntity.InteractableEntity;
import edu.uob.exception.UserException;
import edu.uob.parser.ActionsParser;
import edu.uob.parser.EntitiesParser;

import java.io.File;

public class GameLoader {
    private final GameManager gameManager;

    public GameLoader(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    // load from file then parse into gameManager
    public void loadGame(File entitiesFile, File actionsFile) throws UserException {
        try {
            // 1. initialize storeroom and parses
            EntityData requireRoom = new EntityData(GameManager.STOREROOM_NAME,
                "Storage for any entities not placed in the game");
            EntitiesParser entityParser = new EntitiesParser(entitiesFile, requireRoom);
            ActionsParser actionsParser = new ActionsParser(actionsFile);

            // 2. apply parsed data
            EntityData[] loadedLocations = entityParser.getLocations();
            PathData[] loadedPaths = entityParser.getPaths();
            InteractableEntityData[] loadedEntities = entityParser.getEntities();
            GameAction[] loadedActions = actionsParser.getActions();

            // 3. oad from formated data
            this.loadGame(loadedLocations, loadedPaths, loadedEntities, loadedActions);
        } catch (Exception e) {
            throw new UserException("Fail to load file, please check file format again.");
        }
    }

    // load every part of data needed for gameManager
    public void loadGame(EntityData[] loadedLocations, PathData[] loadedPaths,
                         InteractableEntityData[] loadedEntities, GameAction[] loadedActions) {
        this.loadLocations(loadedLocations, loadedPaths);
        this.loadInterEntities(loadedEntities);
        this.loadActions(loadedActions);
    }

    /* Load Locations */
    public void loadLocations(EntityData[] loadedLocations, PathData[] loadedPaths) {
        // 1. load locations
        boolean isFirst = true;
        for (EntityData loadedLocation : loadedLocations) {
            Location newLocation = this.loadLocation(loadedLocation);
            if (isFirst) {
                this.gameManager.setStartLocation(newLocation);
                isFirst = false;
            }
        }

        // 2. load path
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
        // 1. get entity info
        String entityName = data.getName();
        String targetLocationName = data.getLocationName();
        Location targetLocation = this.gameManager.getLocation(data.getLocationName());

        // 2. initialize entity instance
        EntityType type = data.getType();
        InteractableEntity newEntity = null;
        if (type == EntityType.CHARACTER) newEntity = new Character(data, targetLocationName);
        if (type == EntityType.ARTEFACT) newEntity = new Artefact(data, targetLocationName);
        if (type == EntityType.FURNITURE) newEntity = new Furniture(data, targetLocationName);
        if (type == null) throw new IllegalArgumentException("Invalid entity type");

        // 3. load per entity to gameManager
        this.gameManager.addEntity(newEntity);
        targetLocation.addInterEntity(entityName);
    }

    /* Load Actions */
    public void loadActions(GameAction[] loadedActions) {
        for (GameAction loadedAction : loadedActions) this.loadAction(loadedAction);
    }

    public void loadAction(GameAction loadedAction) {
        this.gameManager.getActions().add(loadedAction);
    }
}
