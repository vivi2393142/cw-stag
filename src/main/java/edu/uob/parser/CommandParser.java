package edu.uob.parser;

import edu.uob.*;
import edu.uob.command.*;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;
import edu.uob.entity.Player;
import edu.uob.entity.interactableEntity.Artefact;
import edu.uob.entity.interactableEntity.InteractableEntity;
import edu.uob.exception.SystemException;
import edu.uob.exception.UserException;

import java.util.*;

public class CommandParser {
    GameManager gameManager;

    public CommandParser(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public Command parse(String commandStr) throws UserException, SystemException {
        // 1-1. get player name
        int colonIdx = commandStr.indexOf(":");
        if (colonIdx == -1) throw new UserException("Command should be start with player name");
        String playerName = commandStr.substring(0, colonIdx);
        this.validatePlayer(playerName);

        // TODO: handle add player duplicated error
        // TODO: handle add player with keywords error
        // 1-2. if player is new, add it
        this.addPlayerIfNotExisted(playerName);

        // 2. get and trim reset command
        String restCommand = commandStr.substring(colonIdx + 1).toLowerCase();
        if (restCommand.isEmpty()) throw new UserException("Command cannot be empty");

        // 3. try every command
        Command[] attempts = {
            tryParseInv(restCommand, playerName), tryParseGet(restCommand, playerName),
            tryParserDrop(restCommand, playerName), tryParserLook(restCommand, playerName),
            tryParseHealth(restCommand, playerName), tryParserGoto(restCommand, playerName),
            tryParseAction(restCommand, playerName)
        };
        for (Command command : attempts) {
            if (command != null) return command;
        }

        throw new UserException("Cannot match any command, please check again");
    }

    /* Parse Attempt */
    private InventoryCommand tryParseInv(String restCommand, String player) throws SystemException {
        if (!(ValueUtils.containIgnoreCase(restCommand, Keyword.INV) ||
              ValueUtils.containIgnoreCase(restCommand, Keyword.INVENTORY))
        ) {
            return null;
        }
        return new InventoryCommand(this.gameManager, player);
    }

    private GetCommand tryParseGet(String restCommand, String player)
        throws SystemException {
        if (!ValueUtils.containIgnoreCase(restCommand, Keyword.GET)) return null;
        return new GetCommand(this.gameManager, player, restCommand);
    }

    private DropCommand tryParserDrop(String restCommand, String player)
        throws SystemException {
        if (!ValueUtils.containIgnoreCase(restCommand, Keyword.DROP)) return null;
        return new DropCommand(this.gameManager, player, restCommand);
    }

    private LookCommand tryParserLook(String restCommand, String player)
        throws SystemException {
        if (!ValueUtils.containIgnoreCase(restCommand, Keyword.LOOK)) return null;
        return new LookCommand(this.gameManager, player);
    }


    private GotoCommand tryParserGoto(String restCommand, String player) throws SystemException {
        if (!ValueUtils.containIgnoreCase(restCommand, Keyword.GOTO)) return null;
        return new GotoCommand(this.gameManager, player, restCommand);
    }

    private HealthCommand tryParseHealth(String restCommand, String player)
        throws SystemException {
        if (!ValueUtils.containIgnoreCase(restCommand, Keyword.HEALTH)) return null;
        return new HealthCommand(this.gameManager, player);
    }

    private ActionCommand tryParseAction(String restCommand, String player)
        throws SystemException {
        HashSet<GameAction> actions = this.gameManager.getActions();

        for (GameAction action : actions) {
            if (this.matchAction(action, restCommand, player)) {
                return new ActionCommand(this.gameManager, player, action);
            }
        }
        return null;
    }

    /* Action */
    private boolean matchAction(GameAction action, String restCommand, String playerName) {
        Player player = this.gameManager.getPlayer(playerName);

        // 1. valid command: at least one trigger
        if (!this.hasMatchingTrigger(action, restCommand)) return false;

        // 2. valid command: subjects
        if (!this.matchSubjectsInCommand(action, restCommand, player)) return false;

        // 3. check produce available
        return this.gameManager.isAllEntAvailable(
            action.getProducedEntities(), this.gameManager.getStoreroom(), false, null);
    }

    private boolean hasMatchingTrigger(GameAction action, String cmd) {
        for (String trigger : action.getTriggers()) {
            if (ValueUtils.containIgnoreCase(cmd, trigger)) return true;
        }
        return false;
    }

    private boolean matchSubjectsInCommand(GameAction action, String cmd, Player player) {
        boolean hasSubject = false;

        // 2-1. get all subjects in command
        HashMap<String, GameEntity> combinedEntities = new HashMap<>();
        combinedEntities.putAll(this.gameManager.getEntities());
        combinedEntities.putAll(this.gameManager.getLocations());

        for (GameEntity entity : combinedEntities.values()) {
            String target = entity.getName();
            if (ValueUtils.containIgnoreCase(cmd, target)) {
                // 2-2. all subjects list in command must match to action
                if (!action.getSubjects().contains(target)) return false;
                // 2-3. subject must be available
                if (!this.isAvailableEntity(entity, player)) return false;
                hasSubject = true;
            }
        }

        // 2-4. at least one subject in command
        return hasSubject;
    }

    private boolean isAvailableEntity(GameEntity entity, Player player) {
        EntityType type = entity.getType();
        if (type == EntityType.LOCATION) return isAvailableEntity((Location) entity, player);
        else if (type == EntityType.PLAYER) return false;
        else return isAvailableEntity((InteractableEntity) entity, player);
    }

    private boolean isAvailableEntity(InteractableEntity targetEntity, Player player) {
        // 1. entity belong to player
        if (targetEntity.getType() == EntityType.ARTEFACT) {
            Artefact artefact = (Artefact) targetEntity;
            boolean isBelong = artefact.getBelongPlayer().equalsIgnoreCase(player.getName());
            if (isBelong) return true;
        }

        // 2. entity in current location
        return targetEntity.getLocation().equalsIgnoreCase(player.getLocation());
    }

    private boolean isAvailableEntity(Location targetPath, Player player) {
        Location currLocation = this.gameManager.getLocation(player.getLocation());
        for (String p : currLocation.getPaths()) {
            if (targetPath.getName().equalsIgnoreCase(p)) return true;
        }
        return false;
    }

    /* Validation */
    private void validatePlayer(String playerName) throws UserException {
        if (playerName.isEmpty()) {
            throw new UserException("Player name cannot be empty");
        }
        if (matchAnyKeyword(playerName)) {
            String message = new StringBuilder()
                .append("Cannot use keyword as player's name: ")
                .append(playerName).toString();
            throw new UserException(message);
        }
        if (!playerName.matches("^[a-zA-Z'\\-\\s]+$")) {
            String message = new StringBuilder()
                .append("player names can only consist of uppercase & lowercase letters,")
                .append("spaces, apostrophes & hyphens").toString();
            throw new UserException(message);
        }
    }

    /* Utility */
    public void addPlayerIfNotExisted(String playerName) {
        Player currPlayer = this.gameManager.getPlayer(playerName);
        if (currPlayer == null) this.gameManager.addPlayer(playerName);
    }

    private boolean matchAnyKeyword(String input) {
        if (input == null) return false;
        for (Keyword keyword : Keyword.values()) {
            if (keyword.name().equalsIgnoreCase(input)) return true;
        }
        return false;
    }
}
