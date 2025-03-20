package edu.uob.entity;

import edu.uob.data.EntityData;
import edu.uob.entity.interactableEntity.Artefact;

import java.util.HashMap;

public class Player extends GameEntity {
    public static final int MAX_HEALTH = 3;
    public static final int MIN_HEALTH = 0;

    private HashMap<String, Artefact> inventories;
    private int health;
    private Location location;

    // TODO: check if player need description
    public Player(String name, Location location) {
        super(new EntityData(name, ""), EntityType.PLAYER);

        this.inventories = new HashMap<>();
        this.health = MAX_HEALTH;
        this.location = location;
    }

    /* Getter */
    public HashMap<String, Artefact> getInventories() {
        return this.inventories;
    }

    public int getHealth() {
        return this.health;
    }

    public Location getLocation() {
        return this.location;
    }

    /* Update */
    public void addHealth(int addedHealth) {
        this.health = Math.max(MAX_HEALTH, this.health + addedHealth);
    }

    public void deductHealth(int deductedHealth) {
        this.health = Math.min(MIN_HEALTH, this.health - deductedHealth);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void moveLocation(Location newLocation) {
        this.location.removePlayer(this);
        this.location = newLocation;
        newLocation.addPlayer(this);

        for (Artefact inventory : this.inventories.values()) {
            inventory.moveLocation(newLocation);
        }
    }

    /* Creation */
    public void addInventory(Artefact inventory) {
        this.inventories.put(inventory.getName(), inventory); // TODO: check existe?
    }

    /* Deletion */
    public void removeInventory(String inventoryName) {
        this.inventories.remove(inventoryName); // TODO: check existe?
    }

    public void removeInventory(Artefact inventory) {
        this.inventories.remove(inventory.getName()); // TODO: check existe?
    }
}
