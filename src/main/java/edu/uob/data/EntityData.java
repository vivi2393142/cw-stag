package edu.uob.data;

public class EntityData {
    private final String entityName;
    private final String description;

    public EntityData(String name, String description) {
        this.entityName = name;
        this.description = description;
    }

    public String getName() {
        return this.entityName;
    }

    public String getDescription() {
        return this.description;
    }
}