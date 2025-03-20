package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.entity.interactableEntity.InteractableEntity;

public class GetCommand extends Command {
    String artefactName;

    public GetCommand(String playerName, String artefactName) {
        super(playerName);
        this.artefactName = artefactName;
    }

    @Override
    protected String executeCommand(GameManager gameManager) {
        // TODO: executeCommand
        Player player = this.getOrAddPlayer(gameManager);
        Artefact artefact = this.getArtefact(gameManager);

        artefact.setBelongPlayer(player);
        player.addInventory(artefact);

        // TODO: check if need response or not
        // TODO: check fail message
        return getMessage(artefact);
    }

    private String getMessage(Artefact artefact) {
        StringBuilder result = new StringBuilder();

        result.append("You picked up a ");
        result.append(artefact.getName());

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
