package edu.uob.entity.interactableEntity;

import edu.uob.data.EntityData;
import edu.uob.entity.Location;

public class Character extends InteractableEntity {
    public Character(EntityData data, Location location) {
        super(data, location, EntityType.CHARACTER);
    }

}
