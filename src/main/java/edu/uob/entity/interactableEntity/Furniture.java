package edu.uob.entity.interactableEntity;

import edu.uob.data.EntityData;
import edu.uob.entity.Location;

public class Furniture extends InteractableEntity {
    public Furniture(EntityData data, Location location) {
        super(data, location, EntityType.FURNITURE);
    }

}
