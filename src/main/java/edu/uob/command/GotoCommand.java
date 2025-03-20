package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.entity.Location;
import edu.uob.entity.Player;

public class GotoCommand extends Command {
    String newLocationName;

    public GotoCommand(String playerName, String newLocationName) {
        super(playerName);
        this.newLocationName = newLocationName;
    }

    @Override
    protected String executeCommand(GameManager gameManager) {
        // TODO: executeCommand
        Player player = this.getOrAddPlayer(gameManager);
        Location currLocation = player.getLocation();
        Location targetLocation = currLocation.getPath(this.newLocationName);

        player.moveLocation(targetLocation);

        // TODO: check if need response or not
        // TODO: check fail message
        return getMessage(gameManager);
    }

    private String getMessage(GameManager gameManager) {
        LookCommand lookCommand = new LookCommand(this.playerName);
        return lookCommand.executeCommand(gameManager);
    }
}
