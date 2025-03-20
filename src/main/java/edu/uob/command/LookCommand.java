package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.entity.interactableEntity.InteractableEntity;

import java.util.stream.Collectors;

public class LookCommand extends Command {
    public LookCommand(String playerName) {
        super(playerName);
    }

    @Override
    protected String executeCommand(GameManager gameManager) {
        // TODO: executeCommand
        Player player = this.getOrAddPlayer(gameManager);

        // TODO: check if need response or not
        // TODO: check fail message
        return getMessage(player);
    }

    private String getMessage(Player player) {
        StringBuilder result = new StringBuilder();
        Location currLocation = player.getLocation();

        // current location
        result.append("You are in ");
        result.append(currLocation.getDescription());
        result.append(".\n");

        // entities in location
        result.append("You can see:\n");
        result.append(
                currLocation.getInteractableEntities().values().stream()
                        .filter(this::isVisibleEntityInLocation)
                        .map(GameEntity::getDescription)
                        .collect(Collectors.joining("\n"))
        );

        // paths
        result.append("\nYou can access from here:\n");
        for (Location path : currLocation.getPaths().values()) {
            result.append(path.getName());
        }

        return result.toString();
    }

    private boolean isVisibleEntityInLocation(InteractableEntity entity) {
        return entity.getType() != GameEntity.EntityType.ARTEFACT || ((Artefact) entity).getBelongPlayer() == null;
    }
}
