package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.entity.Player;

public abstract class Command {
    String playerName;

    public Command(String playerName) {
        this.playerName = playerName;
    }

    public final String execute(GameManager gameManager) {
//        try {
        return executeCommand(gameManager);
//        } catch (Exception e) {
//            StringBuilder builder = new StringBuilder();
//            builder.append("Internal Server Error: ");
//            builder.append(e.getMessage());
//            return builder.toString();
//        }
    }

    protected abstract String executeCommand(GameManager gameManager);

    // TODO: handle add player duplicated error
    // TODO: handle add playerName with keywords error
    public Player getOrAddPlayer(GameManager gameManager) {
        Player currPlayer = gameManager.getPlayer(this.playerName);
        if (currPlayer == null) {
            return gameManager.addPlayer(this.playerName);
        } else {
            return currPlayer;
        }
    }
}
