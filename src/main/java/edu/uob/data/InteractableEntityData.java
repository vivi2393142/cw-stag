package edu.uob.data;

import edu.uob.EntityType;

public class InteractableEntityData extends EntityData {
    private String locationName;
    private EntityType type;

    public InteractableEntityData(String name, String description, String locationName,
                                  EntityType type) {
        super(name, description);
        this.locationName = locationName;
        this.type = type;
    }

    public String getLocationName() {
        return this.locationName;
    }

    public EntityType getType() {
        return this.type;
    }
}