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
    final GameManager gameManager;

    public CommandParser(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public Command parseCmd(String cmdStr) throws UserException, SystemException {
        String lowerCasedCmd = cmdStr.toLowerCase();
        // 1-1. get player name
        int colonIdx = lowerCasedCmd.indexOf(":");
        if (colonIdx == -1) throw new UserException("Command should be start with player name");
        String player = lowerCasedCmd.substring(0, colonIdx).trim();
        this.validatePlayer(player);
        // 1-2. if player is new, add it
        this.addPlayerIfNotExisted(player);

        // 2. get and trim reset command
        int afterColonIdx = colonIdx + 1;
        String restCms = lowerCasedCmd.substring(afterColonIdx).toLowerCase();
        if (restCms.isEmpty()) throw new UserException("Command cannot be empty");

        // 3. get all keywords
        Set<Keyword> matchKeywords = this.getAllMatchKeywords(restCms);

        // 4. try every command
        return this.tryParseAny(matchKeywords, restCms, player);
    }

    /* Parse Attempt */
    private Command tryParseAny(Set<Keyword> matchKeywords, String restCms, String player)
        throws SystemException, UserException {
        if (this.matchesInvKeyword(matchKeywords)) return this.tryParseInv(player);

        int matchCtn = matchKeywords.size();
        if (matchCtn > 1) { // check no multiple keywords
            throw new UserException("Cannot match multiple command keywords, please check again");
        } else if (matchCtn == 1) { // try parse to built-in command
            if (matchKeywords.contains(Keyword.GET)) return this.tryParseGet(restCms, player);
            if (matchKeywords.contains(Keyword.DROP)) return this.tryParserDrop(restCms, player);
            if (matchKeywords.contains(Keyword.LOOK)) return this.tryParserLook(player);
            if (matchKeywords.contains(Keyword.HEALTH)) return this.tryParseHealth(player);
            if (matchKeywords.contains(Keyword.GOTO)) return this.tryParserGoto(restCms, player);
        } else { // try parse to custom actions
            Command actionCommand = this.tryParseAction(restCms, player);
            if (actionCommand != null) return actionCommand;
        }

        // fail if no command match
        throw new UserException("Cannot match any command, please check again");
    }

    private InventoryCommand tryParseInv(String player) throws SystemException {
        return new InventoryCommand(this.gameManager, player);
    }

    private GetCommand tryParseGet(String restCommand, String player)
        throws SystemException {
        return new GetCommand(this.gameManager, player, restCommand);
    }

    private DropCommand tryParserDrop(String restCommand, String player)
        throws SystemException {
        return new DropCommand(this.gameManager, player, restCommand);
    }

    private LookCommand tryParserLook(String player)
        throws SystemException {
        return new LookCommand(this.gameManager, player);
    }

    private GotoCommand tryParserGoto(String restCommand, String player) throws SystemException {
        return new GotoCommand(this.gameManager, player, restCommand);
    }

    private HealthCommand tryParseHealth(String player)
        throws SystemException {
        return new HealthCommand(this.gameManager, player);
    }

    private ActionCommand tryParseAction(String restCommand, String player)
        throws UserException, SystemException {
        HashSet<GameAction> actions = this.gameManager.getActions();

        // can only match one action, if more than one will fail
        ActionCommand matchedActionCommand = null;
        for (GameAction action : actions) {
            if (this.matchesAction(action, restCommand, player)) {
                if (matchedActionCommand == null) {
                    matchedActionCommand = new ActionCommand(this.gameManager, player, action);
                } else {
                    throw new UserException("Cannot match multiple actions at the same time");
                }
            }
        }
        return matchedActionCommand;
    }

    /* Action */
    private boolean matchesAction(GameAction action, String restCommand, String playerName) {
        Player player = this.gameManager.getPlayer(playerName);
        // 1. valid command: at least one trigger
        if (!this.hasMatchingTrigger(action, restCommand)) return false;
        // 2. valid command: subjects
        if (!this.matchesSubjectsInCommand(action, restCommand, player)) return false;
        // 3. check subjects available
        if (!this.isSubjectSetAvailable(action.getSubjects(), player)) return false;
        // 4. check produce available
        return this.isProduceSetAvailable(action.getProducedEntities());
    }

    // check if command contain any trigger keyword
    private boolean hasMatchingTrigger(GameAction action, String cmd) {
        for (String trigger : action.getTriggers()) {
            if (ValueUtils.containsIgnoreCase(cmd, trigger)) return true;
        }
        return false;
    }

    // get all mentioned subjects from command
    private boolean matchesSubjectsInCommand(GameAction action, String cmd, Player player) {
        boolean hasSubject = false;

        // 1. get all subjects in command
        HashMap<String, GameEntity> combinedEntities = new HashMap<>();
        combinedEntities.putAll(this.gameManager.getEntities());
        combinedEntities.putAll(this.gameManager.getLocations());

        for (GameEntity entity : combinedEntities.values()) {
            String target = entity.getName();
            if (ValueUtils.containsIgnoreCase(cmd, target)) {
                // 2. all subjects list in command must match to action
                if (!action.getSubjects().contains(target)) return false;
                // 3. subject must be available
                if (!this.isAvailableEntity(entity, player)) return false;
                hasSubject = true;
            }
        }

        return hasSubject; // 4. at least one subject in command
    }

    // check if subject set is available
    private boolean isSubjectSetAvailable(Set<String> subjects, Player player) {
        for (String subject : subjects) {
            if (!this.isSubjectAvailable(subject, player)) return false;
        }
        return true;
    }

    // check if subject is available
    private boolean isSubjectAvailable(String subject, Player player) {
        Location currLocation = this.gameManager.getLocation(player.getLocation());

        // 1. is in location as an entity
        boolean isEntInLocation = currLocation.getInterEntities().contains(subject);
        if (isEntInLocation) return true;

        // 2. is in location as a path
        boolean isPathInLocation = currLocation.getPaths().contains(subject);
        if (isPathInLocation) return true;

        // 3. is in player's inventory
        return player.getInventories().contains(subject);
    }

    // check if produce subject set is available (from storeroom)
    private boolean isProduceSetAvailable(Set<String> producedEntities) {
        for (String producedEntity : producedEntities) {
            if (!this.isProduceAvailable(producedEntity)) return false;
        }
        return true;
    }

    // check if produce subject is available (from storeroom)
    private boolean isProduceAvailable(String producedEntity) {
        // 1. is any of location, or
        Location targetLocation = this.gameManager.getLocation(producedEntity);
        if (targetLocation != null) return true;
        // 2. is in storeroom
        return this.gameManager.getStoreroom().getInterEntities().contains(producedEntity);
    }

    // check is game entity available according to the current player
    private boolean isAvailableEntity(GameEntity entity, Player player) {
        EntityType type = entity.getType();
        if (type == EntityType.LOCATION) return this.isAvailableEntity((Location) entity, player);
        else if (type == EntityType.PLAYER) return false;
        else return this.isAvailableEntity((InteractableEntity) entity, player);
    }

    // check is interactable entity available according to the current player
    private boolean isAvailableEntity(InteractableEntity targetEntity, Player player) {
        if (targetEntity == null) return false;

        // 1. entity belong to player
        if (targetEntity.getType() == EntityType.ARTEFACT) {
            Artefact artefact = (Artefact) targetEntity;
            String belongPlayer = artefact.getBelongPlayer();
            if (belongPlayer != null && !belongPlayer.isEmpty()) {
                boolean isBelong = belongPlayer.equalsIgnoreCase(player.getName());
                if (isBelong) return true;
            }
        }

        // 2. entity in current location
        String locationName = targetEntity.getLocation();
        return locationName != null && locationName.equalsIgnoreCase(player.getLocation());
    }

    // check is location available according to the current player
    private boolean isAvailableEntity(Location targetPath, Player player) {
        Location currLocation = this.gameManager.getLocation(player.getLocation());
        for (String p : currLocation.getPaths()) {
            if (targetPath.getName().equalsIgnoreCase(p)) return true;
        }
        return false;
    }

    /* Validation */
    private void validatePlayer(String playerName) throws UserException {
        // 1. player name cannot be empty
        if (playerName.isEmpty()) throw new UserException("Player name cannot be empty");
        // 2. no keyword in player's name
        if (ValueUtils.matchesAnyKeyword(playerName)) {
            String message = new StringBuilder("Cannot use keyword as player's name: ")
                .append(playerName).toString();
            throw new UserException(message);
        }
        // 3. consist of uppercase and lowercase letters, spaces, apostrophes and hyphens
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

    private Set<Keyword> getAllMatchKeywords(String input) {
        Set<Keyword> matchKeywords = new HashSet<>();
        for (Keyword keyword : Keyword.values()) {
            if (ValueUtils.containsIgnoreCase(input, keyword)) matchKeywords.add(keyword);
        }
        return matchKeywords;
    }

    // inventory has two keywords, check if match any of them
    private boolean matchesInvKeyword(Set<Keyword> matchKeywords) {
        int size = matchKeywords.size();
        boolean hasInv = matchKeywords.contains(Keyword.INV);
        boolean hasInventory = matchKeywords.contains(Keyword.INVENTORY);
        return ((size == 1 && (hasInv || hasInventory)) || (size == 2 && hasInv && hasInventory));
    }
}
