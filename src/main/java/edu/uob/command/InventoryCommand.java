package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.InteractableEntity;
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

        if (inventories.isEmpty()) {
            result.append("nothing");
        } else {
            boolean isFirst = true;
            for (String inv : inventories) {
                if (!isFirst) {
                    result.append(", ");
                } else {
                    isFirst = false;
                }
                String info = this.getInventoryInfo(inv);
                result.append(info);
            }
        }

        result.append(" in your inventory.");
        return result.toString();
    }

    private String getInventoryInfo(String inventoryName) {
        InteractableEntity inventory = this.gameManager.getEntity(inventoryName);
        return new StringBuilder()
            .append(inventory.getName())
            .append("(")
            .append(inventory.getDescription())
            .append(")").toString();
    }
}
