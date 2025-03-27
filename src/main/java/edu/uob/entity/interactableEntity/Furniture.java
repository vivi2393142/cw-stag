package edu.uob.entity.interactableEntity;

import edu.uob.data.EntityData;
import edu.uob.EntityType;

public class Furniture extends InteractableEntity {
    public Furniture(EntityData data, String locationName) {
        super(data, locationName, EntityType.FURNITURE);
    }
}
