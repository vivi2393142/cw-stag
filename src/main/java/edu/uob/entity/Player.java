package edu.uob.entity;

import edu.uob.EntityType;
import edu.uob.data.EntityData;

import java.util.HashSet;

public class Player extends GameEntity {
    public static final int MAX_HEALTH = 3;
    public static final int MIN_HEALTH = 0;

    private final HashSet<String> inventories;
    private int health;
    private String location;

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
        this.inventories.add(inventoryName);
    }

    /* Deletion */
    public void removeInventory(String inventoryName) {
        this.inventories.remove(inventoryName);
    }

    public void removeAllInventory() {
        this.inventories.clear();
    }

    /* Utility */
    @Override
    public String toString() {
        return new StringBuilder(super.toString())
            .append("\n")
            .append("inventories: ").append(this.inventories.toString()).append("\n")
            .append("health: ").append(this.health).append("\n")
            .append("location: ").append(this.location)
            .toString();
    }
}
