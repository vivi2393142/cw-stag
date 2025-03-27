package edu.uob.entity;

import edu.uob.EntityType;
import edu.uob.data.EntityData;

import java.util.HashSet;

public class Player extends GameEntity {
    public static final int MAX_HEALTH = 3;
    public static final int MIN_HEALTH = 0;

    private HashSet<String> inventories;
    private int health;
    private String location;

    // TODO: check if player need description
    public Player(String name, String locationName) {
        super(new EntityData(name, ""), EntityType.PLAYER);

        this.inventories = new HashSet<>();
        this.health = MAX_HEALTH;
        this.location = locationName;
    }

    /* Getter */
    public HashSet<String> getInventories() {
        return this.inventories;
    }

    public int getHealth() {
        return this.health;
    }

    public String getLocation() {
        return this.location;
    }

    /* Update */

    public void addHealth(int addedHealth) {
        this.health = Math.min(MAX_HEALTH, this.health + addedHealth);
    }

    public void deductHealth(int deductedHealth) {
        this.health = Math.max(MIN_HEALTH, this.health - deductedHealth);
    }

    public void resetHealth() {
        this.health = MAX_HEALTH;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /* Creation */
    public void addInventory(String inventoryName) {
        this.inventories.add(inventoryName); // TODO: check existe?
    }

    /* Deletion */
    public void removeInventory(String inventoryName) {
        this.inventories.remove(inventoryName); // TODO: check existe?
    }

    /* Utility */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(super.toString()).append("\n");
        result.append("inventories: ").append(this.inventories.toString()).append("\n");
        result.append("health: ").append(this.health).append("\n");
        result.append("location: ").append(this.location);
        return result.toString();
    }
}
