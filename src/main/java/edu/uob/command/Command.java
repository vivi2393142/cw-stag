package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.exception.SystemException;
import edu.uob.exception.UserException;

public abstract class Command {
    GameManager gameManager;
    String player;

    public Command(GameManager gameManager, String player) throws SystemException {
        if (gameManager == null) throw new SystemException("Invalid GameManager");

        this.gameManager = gameManager;
        this.player = player;
    }

    public final String execute() throws UserException, SystemException {
        return executeCommand();
    }

    protected abstract String executeCommand()
        throws UserException, SystemException;

    /* Utility */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("player: ").append(this.player);
        return result.toString();
    }
}
