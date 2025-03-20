package edu.uob.data;

import edu.uob.entity.GameEntity;

public class InteractableEntityData extends EntityData {
    private String locationName;
    private GameEntity.EntityType type;

    public InteractableEntityData(String name, String description, String locationName, GameEntity.EntityType type) {
        super(name, description);
        this.locationName = locationName;
        this.type = type;
    }

    public String getLocationName() {
        return this.locationName;
    }

    public GameEntity.EntityType getType() {
        return this.type;
    }
}