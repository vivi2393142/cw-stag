package edu.uob.entity;

import edu.uob.EntityType;
import edu.uob.data.EntityData;

public abstract class GameEntity {
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

    /* Utility */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("type: ").append(this.type);
        result.append("name: ").append(this.name).append("\n");
        result.append("description: ").append(this.description);
        return result.toString();
    }
}