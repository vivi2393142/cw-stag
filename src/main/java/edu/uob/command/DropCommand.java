package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.exception.SystemException;
import edu.uob.exception.UserException;

public class DropCommand extends Command {
    String possibleInventories;

    public DropCommand(GameManager gameManager, String player, String possibleInventories)
        throws SystemException {
        super(gameManager, player);
        this.possibleInventories = possibleInventories;
    }

    @Override
    protected String executeCommand() throws UserException {
        Player player = this.gameManager.getPlayer(this.player);
        Location currLocation = this.gameManager.getLocation(player.getLocation());
        Artefact inventory = this.getValidInventory(this.possibleInventories, player);
        // 1. remove artefact from player's inventories
        player.removeInventory(inventory.getName());
        // 2. update artefact's location & belongPlayer
        inventory.setLocation(player.getLocation());
        // 3. add artefact to current location
        currLocation.addInterEntity(inventory.getName());

        return this.getMessage(inventory, player.getLocation());
    }

    private String getMessage(Artefact inventory, String locationName) {
        return new StringBuilder("You have drop ")
            .append(inventory.getName())
            .append(" to ")
            .append(locationName).toString();
    }

    // get only ony valid inventory from input string
    public Artefact getValidInventory(String possibleInventories, Player player)
        throws UserException {
        return this.getOnlyOneValidEntity(player.getInventories(),
            possibleInventories, Artefact.class);
    }

    /* Utility */
    @Override
    public String toString() {
        return new StringBuilder(super.toString())
            .append("\n")
            .append("inventories: ")
            .append(this.possibleInventories).toString();
    }
}
