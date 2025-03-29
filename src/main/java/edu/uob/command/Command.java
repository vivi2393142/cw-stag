package edu.uob.command;

import edu.uob.GameManager;
import edu.uob.ValueUtils;
import edu.uob.entity.GameEntity;
import edu.uob.exception.SystemException;
import edu.uob.exception.UserException;

import java.util.HashSet;
import java.util.Set;

public abstract class Command {
    GameManager gameManager;
    String player;

    public Command(GameManager gameManager, String player) throws SystemException {
        if (gameManager == null) throw new SystemException("Invalid GameManager");

        this.gameManager = gameManager;
        this.player = player;
    }

    public final String execute() throws UserException, SystemException {
        return this.executeCommand();
    }

    protected abstract String executeCommand() throws UserException, SystemException;

    /* Utility */
    public <T extends GameEntity> T getOnlyOneValidEntity(Set<String> entityNames,
                                                          String possibleArtefact,
                                                          Class<T> entityClass)
        throws UserException {
        // 1. check inventory exist
        Set<T> validEntities =
            this.getEntitiesFromString(entityNames, possibleArtefact, entityClass);
        // 2. check if only one valid inventory in list
        return this.getOnlyOneEntity(validEntities);
    }

    protected <T extends GameEntity> Set<T> getEntitiesFromString(
        Set<String> entityNames, String input, Class<T> entityClass) {
        Set<T> validEntities = new HashSet<>();
        for (String name : entityNames) {
            if (ValueUtils.containsIgnoreCase(input, name)) { // check if in possible list
                // get instance from manager
                T targetEnt = this.gameManager.getEntityByName(name, entityClass);
                if (targetEnt != null) validEntities.add(targetEnt);
            }
        }
        return validEntities;
    }

    private <T extends GameEntity> T getOnlyOneEntity(Set<T> validEntSet) throws UserException {
        int entCtn = validEntSet.size();
        if (entCtn == 0) throw new UserException("Cannot find any target entity");
        if (entCtn > 1) throw new UserException("Cannot get multiple entities at once");
        return validEntSet.stream().findFirst().get();
    }

    @Override
    public String toString() {
        return new StringBuilder("player: ")
            .append(this.player).toString();
    }
}
