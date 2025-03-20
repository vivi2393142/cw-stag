package edu.uob.data;

public class EntityData {
    private String name;
    private String description;

    public EntityData(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }
}