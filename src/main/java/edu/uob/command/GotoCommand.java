package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.ValueUtils;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.exception.SystemException;
import edu.uob.exception.UserException;

import java.util.HashSet;
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
        Player player = this.gameManager.getPlayer(this.player);
        Set<String> paths = this.gameManager.getLocation(player.getLocation()).getPaths();
        Location path = this.getValidLocation(this.possibleLocations, paths);
        if (path == null) throw new UserException("Your destination is not available");

        this.gameManager.movePlayer(this.player, path.getName());
        return getMessage();
    }

    private String getMessage() throws SystemException {
        LookCommand lookCommand = new LookCommand(this.gameManager, this.player);
        return lookCommand.executeCommand();
    }

    public Location getValidLocation(String possibleLocations, Set<String> paths)
        throws UserException {
        // check location path exist
        Set<Location> validLocations = new HashSet<>();
        for (String path : paths) {
            String lowerCasePath = path.toLowerCase();
            if (ValueUtils.containIgnoreCase(possibleLocations, lowerCasePath)) {
                Location targetPath = this.gameManager.getLocation(path);
                if (targetPath != null) validLocations.add(targetPath);
            }
        }

        // check if only one valid inventory in list
        int locationCtn = validLocations.size();
        if (locationCtn == 0) throw new UserException("Cannot find any target path available");
        if (locationCtn > 1) throw new UserException("Cannot have multiple destinations");
        return validLocations.stream().findFirst().get();
    }

    /* Utility */
    @Override
    public String toString() {
        return new StringBuilder()
            .append(super.toString()).append("\n")
            .append("possibleLocations: ").append(this.possibleLocations.toString())
            .toString();
    }
}
