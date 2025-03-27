package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.entity.Player;
import edu.uob.exception.SystemException;

import java.util.HashSet;

public class InventoryCommand extends Command {
    public InventoryCommand(GameManager gameManager, String playerName) throws SystemException {
        super(gameManager, playerName);
    }

    @Override
    protected String executeCommand() {
        Player player = this.gameManager.getPlayer(this.player);
        HashSet<String> inventories = player.getInventories();

        return getMessage(inventories);
    }

    private String getMessage(HashSet<String> inventories) {
        StringBuilder result = new StringBuilder().append("You have ");

        if (inventories.isEmpty()) result.append("nothing");
        else result.append(String.join(",", inventories));

        result.append(" in your inventory.");
        return result.toString();
    }
}
