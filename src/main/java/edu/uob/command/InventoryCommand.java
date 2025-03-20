package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.Artefact;

import java.util.HashMap;

public class InventoryCommand extends Command {
    public InventoryCommand(String playerName) {
        super(playerName);
    }

    @Override
    protected String executeCommand(GameManager gameManager) {
        // TODO: executeCommand
        Player player = this.getOrAddPlayer(gameManager);
        HashMap<String, Artefact> inventories = player.getInventories();

        return getMessage(inventories);
    }

    private String getMessage(HashMap<String, Artefact> inventories) {
        StringBuilder result = new StringBuilder();
        result.append("You have ");

        if (inventories.isEmpty()) result.append("nothing");
        else result.append(String.join(",", inventories.keySet()));

        result.append(" in your inventory.");
        return result.toString();
    }
}
