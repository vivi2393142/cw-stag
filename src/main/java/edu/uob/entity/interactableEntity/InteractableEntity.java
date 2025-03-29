package edu.uob.entity.interactableEntity;

import edu.uob.data.EntityData;
import edu.uob.EntityType;
import edu.uob.entity.GameEntity;

public class InteractableEntity extends GameEntity {
    private String location;

    public InteractableEntity(EntityData data, String locationName, EntityType type) {
        super(data, type);
        if (type == EntityType.LOCATION || type == EntityType.PLAYER) {
            throw new IllegalArgumentException("InteractableEntity type is wrong");
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
        return new StringBuilder(super.toString())
            .append("\n")
            .append("location: ").append(this.location)
            .toString();
    }
}
