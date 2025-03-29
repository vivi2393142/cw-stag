package edu.uob.entity;

import edu.uob.EntityType;
import edu.uob.data.EntityData;

public abstract class GameEntity {
    private final String entityName;
    private final String description;
    private final EntityType entityType;

    public GameEntity(EntityData data, EntityType type) {
        this.entityName = data.getName();
        this.description = data.getDescription();
        this.entityType = type;
    }

    /* Getter */
    public String getName() {
        return this.entityName;
    }
    public String getDescription() {
        return this.description;
    }
    public EntityType getType() {
        return this.entityType;
    }

    /* Utility */
    @Override
    public String toString() {
        return new StringBuilder()
            .append("type: ").append(this.entityType).append("\n")
            .append("name: ").append(this.entityName).append("\n")
            .append("description: ").append(this.description)
            .toString();
    }
}