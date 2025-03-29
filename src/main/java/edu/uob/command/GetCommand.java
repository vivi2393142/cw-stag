package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.exception.SystemException;
import edu.uob.exception.UserException;

import java.util.Set;

public class GetCommand extends Command {
    String possibleArtefacts;

    public GetCommand(GameManager gameManager, String player, String possibleArtefacts)
        throws SystemException {
        super(gameManager, player);
        this.possibleArtefacts = possibleArtefacts;
    }

    @Override
    protected String executeCommand() throws UserException {
        Player player = this.gameManager.getPlayer(this.player);
        Location currLocation = this.gameManager.getLocation(player.getLocation());
        Artefact artefact = this.getValidArtefact(
            this.possibleArtefacts, currLocation.getInterEntities());

        // 1. add artefact to player's inventories
        player.addInventory(artefact.getName());
        // 2. update artefact's location & belongPlayer
        artefact.setBelongPlayer(this.player);
        // 3. remove artefact from current location
        currLocation.removeInterEntity(artefact.getName());

        return getMessage(artefact);
    }

    private String getMessage(Artefact artefact) {
        return new StringBuilder()
            .append("You picked up a ")
            .append(artefact.getName())
            .toString();
    }

    // get only ony valid artefact in current location from input string
    public Artefact getValidArtefact(String possibleArtefacts, Set<String> entInLocation)
        throws UserException {
        return this.getOnlyOneValidEntity(entInLocation, possibleArtefacts, Artefact.class);
    }

    /* Utility */
    @Override
    public String toString() {
        return new StringBuilder(super.toString())
            .append("\n")
            .append("artefacts: ")
            .append(this.possibleArtefacts)
            .toString();
    }
}
