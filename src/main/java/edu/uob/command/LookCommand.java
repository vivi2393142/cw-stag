package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.exception.SystemException;

import java.util.Objects;

public class LookCommand extends Command {
    public LookCommand(GameManager gameManager, String playerName) throws SystemException {
        super(gameManager, playerName);
    }

    @Override
    protected String executeCommand() {
        Player player = this.gameManager.getPlayer(this.player);
        Location currLocation = this.gameManager.getLocation(player.getLocation());

        return getMessage(currLocation);
    }

    private String getMessage(Location currLocation) {
        StringBuilder result = new StringBuilder();

        this.appendLocationInfo(result, currLocation);
        this.appendEntities(result, currLocation);
        this.appendPaths(result, currLocation);

        return result.toString();
    }

    private void appendLocationInfo(StringBuilder result, Location currLocation) {
        result.append("You are in ")
            .append(currLocation.getDescription())
            .append(".\n");
    }

    private void appendEntities(StringBuilder result, Location currLocation) {
        result.append("You can see:\n");

        // interactable entities
        for (String entityName : currLocation.getInterEntities()) {
            GameEntity entity = this.gameManager.getEntity(entityName);
            if (entity != null) {
                result.append(entity.getDescription());
                result.append("\n");
            }
        }

        // players
        for (String playerName : currLocation.getPlayers()) {
            if (!Objects.equals(playerName, this.player)) {
                result.append("player - ").append(playerName).append("\n");
            }
        }
    }

    private void appendPaths(StringBuilder result, Location currLocation) {
        result.append("You can access from here:\n")
            .append(String.join("\n", currLocation.getPaths()));
    }
}
