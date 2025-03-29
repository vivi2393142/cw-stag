package edu.uob.data;

import edu.uob.EntityType;

public class InteractableEntityData extends EntityData {
    private final String locationName;
    private final EntityType entityType;

    public InteractableEntityData(String name, String description, String locationName,
                                  EntityType type) {
        super(name, description);
        this.locationName = locationName;
        this.entityType = type;
    }

    public String getLocationName() {
        return this.locationName;
    }

    public EntityType getType() {
        return this.entityType;
    }
}