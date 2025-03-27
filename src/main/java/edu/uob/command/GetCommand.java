package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.EntityType;
import edu.uob.ValueUtils;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.entity.interactableEntity.InteractableEntity;
import edu.uob.exception.SystemException;
import edu.uob.exception.UserException;

import java.util.HashSet;
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

    public Artefact getValidArtefact(String possibleArtefacts, HashSet<String> entInLocation)
        throws UserException {
        // check artefact exist
        Set<Artefact> validArtefacts = new HashSet<>();
        for (String entName : entInLocation) {
            String lowerCaseEntName = entName.toLowerCase();
            if (ValueUtils.containIgnoreCase(possibleArtefacts, lowerCaseEntName)) {
                Artefact targetEnt = this.getArtefact(lowerCaseEntName);
                if (targetEnt != null) validArtefacts.add(targetEnt);
            }
        }

        // check if only one valid artefact in list
        int artefactCtn = validArtefacts.size();
        if (artefactCtn == 0) throw new UserException("Cannot find any target artefact");
        if (artefactCtn > 1) throw new UserException("Cannot get multiple artefacts at once");
        return validArtefacts.stream().findFirst().get();
    }

    public Artefact getArtefact(String entName) {
        InteractableEntity artefact = this.gameManager.getEntity(entName);
        if (artefact == null) return null;
        if (artefact.getType() != EntityType.ARTEFACT) return null;
        return (Artefact) artefact;
    }

    /* Utility */
    @Override
    public String toString() {
        return new StringBuilder()
            .append(super.toString()).append("\n")
            .append("artefacts: ")
            .append(this.possibleArtefacts)
            .toString();
    }
}
