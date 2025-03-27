package edu.uob.command;

import edu.uob.EntityType;
import edu.uob.GameManager;
import edu.uob.ValueUtils;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.entity.interactableEntity.InteractableEntity;
import edu.uob.exception.SystemException;
import edu.uob.exception.UserException;

import java.util.HashSet;
import java.util.Set;

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

        return getMessage(inventory, player.getLocation());
    }

    private String getMessage(Artefact inventory, String locationName) {
        return new StringBuilder()
            .append("You have drop ")
            .append(inventory.getName())
            .append(" to ")
            .append(locationName).toString();
    }

    public Artefact getValidInventory(String possibleInventories, Player player)
        throws UserException {
        // check inventory exist
        Set<Artefact> validInventories = new HashSet<>();
        for (String envName : player.getInventories()) {
            String lowerCaseEnvName = envName.toLowerCase();
            if (ValueUtils.containIgnoreCase(possibleInventories, lowerCaseEnvName)) {
                Artefact targetEnt = this.getInventory(envName);
                if (targetEnt != null) validInventories.add(targetEnt);
            }
        }

        // check if only one valid inventory in list
        int invCtn = validInventories.size();
        if (invCtn == 0) throw new UserException("Cannot find any target inventory");
        if (invCtn > 1) throw new UserException("Cannot get multiple inventories at once");
        return validInventories.stream().findFirst().get();
    }

    public Artefact getInventory(String envName) {
        InteractableEntity inv = this.gameManager.getEntity(envName);
        if (inv == null) return null;
        if (inv.getType() != EntityType.ARTEFACT) return null;
        return (Artefact) inv;
    }

    /* Utility */
    @Override
    public String toString() {
        return new StringBuilder()
            .append(super.toString()).append("\n")
            .append("inventories: ")
            .append(this.possibleInventories).toString();
    }
}
