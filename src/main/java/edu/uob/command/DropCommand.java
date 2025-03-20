package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.entity.interactableEntity.InteractableEntity;

public class DropCommand extends Command {
    String artefactName;

    public DropCommand(String playerName, String artefactName) {
        super(playerName);
        this.artefactName = artefactName;
    }

    @Override
    protected String executeCommand(GameManager gameManager) {
        // TODO: executeCommand
        Player player = this.getOrAddPlayer(gameManager);
        Artefact artefact = this.getArtefact(gameManager);

        artefact.setBelongPlayer(null);
        player.removeInventory(artefact);

        // TODO: check if need response or not
        // TODO: check fail message
        return getMessage(artefact, player.getLocation());
    }

    private String getMessage(Artefact artefact, Location location) {
        StringBuilder result = new StringBuilder();

        result.append("You have drop ");
        result.append(artefact.getName());
        result.append(" to ");
        result.append(location.getName());

        return result.toString();
    }

    public Artefact getArtefact(GameManager gameManager) {
        InteractableEntity artefact = gameManager.getEntity(this.artefactName);
        if (artefact == null) {
            // TODO: handle error
            throw new IllegalArgumentException("not found");
        } else if (artefact.getType() != GameEntity.EntityType.ARTEFACT) {
            // TODO: handle error
            throw new IllegalArgumentException("not right type");
        } else {
            return (Artefact) artefact;
        }
    }
}
