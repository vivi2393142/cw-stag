package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.exception.SystemException;
import edu.uob.exception.UserException;

import java.util.Set;

public class GotoCommand extends Command {
    String possibleLocations;

    public GotoCommand(GameManager gameManager, String player, String possibleLocations)
        throws SystemException {
        super(gameManager, player);
        this.possibleLocations = possibleLocations;
    }

    @Override
    protected String executeCommand() throws SystemException, UserException {
        // 1. initialize and get target location
        Player player = this.gameManager.getPlayer(this.player);
        Set<String> paths = this.gameManager.getLocation(player.getLocation()).getPaths();
        Location path = this.getValidLocation(this.possibleLocations, paths);
        if (path == null) throw new UserException("Your destination is not available");

        // 2. move player to new location
        this.gameManager.movePlayer(this.player, path.getName());
        return this.getMessage();
    }

    private String getMessage() throws SystemException {
        LookCommand lookCommand = new LookCommand(this.gameManager, this.player);
        return lookCommand.executeCommand();
    }

    // get only ony valid location from input string
    public Location getValidLocation(String possibleLocations, Set<String> paths)
        throws UserException {
        return this.getOnlyOneValidEntity(paths, possibleLocations, Location.class);
    }

    /* Utility */
    @Override
    public String toString() {
        return new StringBuilder(super.toString())
            .append("\n")
            .append("possibleLocations: ").append(this.possibleLocations)
            .toString();
    }
}
