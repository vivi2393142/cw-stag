package edu.uob.command;

import edu.uob.GameAction;
import edu.uob.GameManager;
import edu.uob.data.ActionData;
import edu.uob.entity.Player;

public class ActionCommand extends Command {
    ActionData actionData;

    public ActionCommand(String playerName, ActionData actionData) {
        super(playerName);
        this.actionData = actionData;
    }

    @Override
    protected String executeCommand(GameManager gameManager) {
        // TODO: executeCommand
        Player player = this.getOrAddPlayer(gameManager);
        GameAction action = new GameAction(this.actionData);

        // TODO: check if need response or not
        // TODO: check fail message
        return action.execute(gameManager, player);
    }
}
