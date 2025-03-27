package edu.uob.entity.interactableEntity;

import edu.uob.data.EntityData;
import edu.uob.EntityType;
import edu.uob.entity.GameEntity;

public class InteractableEntity extends GameEntity {
    private String location;

    public InteractableEntity(EntityData data, String locationName, EntityType type) {
        super(data, type);
        if (type == EntityType.LOCATION || type == EntityType.PLAYER) {
            throw new IllegalArgumentException("InteractableEntity should have type Artefact, Character or Furniture");
        }
        this.location = locationName;
    }

    /* Getter */
    public String getLocation() {
        return this.location;
    }

    /* Update */
    public void setLocation(String locationName) {
        this.location = locationName;
    }

    /* Utility */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(super.toString()).append("\n");
        result.append("location: ").append(this.location);
        return result.toString();
    }
}
