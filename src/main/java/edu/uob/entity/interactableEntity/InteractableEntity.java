package edu.uob.entity.interactableEntity;

import edu.uob.data.EntityData;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;

public class InteractableEntity extends GameEntity {
    private Location location;

    public InteractableEntity(EntityData data, Location location, GameEntity.EntityType type) {
        super(data, type);
        if (type == GameEntity.EntityType.LOCATION || type == EntityType.PLAYER) {
            throw new IllegalArgumentException("InteractableEntity should have type Artefact, Character or Furniture");
        }
        this.location = location;
    }

    /* Getter */
    public Location getLocation() {
        return this.location;
    }

    /* Update */
    public void setLocation(Location location) {
        this.location = location;
    }

    public void moveLocation(Location newLocation) {
        this.location.removeInteractableEntity(this);
        this.location = newLocation;
        newLocation.addInteractableEntity(this);
    }
}
