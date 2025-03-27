package edu.uob.entity.interactableEntity;

import edu.uob.data.EntityData;
import edu.uob.EntityType;

public class Character extends InteractableEntity {
    public Character(EntityData data, String locationName) {
        super(data, locationName, EntityType.CHARACTER);
    }
}
