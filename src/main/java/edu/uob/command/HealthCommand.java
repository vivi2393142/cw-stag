package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.entity.Player;
import edu.uob.exception.SystemException;

public class HealthCommand extends Command {
    public HealthCommand(GameManager gameManager, String playerName) throws SystemException {
        super(gameManager, playerName);
    }

    @Override
    protected String executeCommand() {
        Player player = this.gameManager.getPlayer(this.player);
        return Integer.toString(player.getHealth());
    }
}
