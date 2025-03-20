package edu.uob.entity;

import edu.uob.data.EntityData;

public abstract class GameEntity {
    public enum EntityType {ARTEFACT, FURNITURE, LOCATION, CHARACTER, PLAYER}

    private final String name;
    private final String description;
    private final EntityType type;

    public GameEntity(EntityData data, EntityType type) {
        this.name = data.getName();
        this.description = data.getDescription();
        this.type = type;
    }

    /* Getter */
    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public EntityType getType() {
        return this.type;
    }
}