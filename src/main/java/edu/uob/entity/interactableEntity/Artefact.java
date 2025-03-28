package edu.uob.entity.interactableEntity;

import edu.uob.data.EntityData;
import edu.uob.EntityType;

public class Artefact extends InteractableEntity {
    private String belongPlayer;

    public Artefact(EntityData data, String locationName) {
        super(data, locationName, EntityType.ARTEFACT);
        this.belongPlayer = null;
    }

    /* Getter */
    public String getBelongPlayer() {
        return this.belongPlayer;
    }

    /* Update */
    public void setBelongPlayer(String playerName) {
        this.belongPlayer = playerName;
        if (playerName != null) this.setLocation(null);
    }

    @Override
    public void setLocation(String locationName) {
        super.setLocation(locationName);
        if (locationName != null) this.belongPlayer = null;
    }

    /* Utility */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(super.toString()).append("\n");
        result.append("belongPlayer: ").append(this.belongPlayer);
        return result.toString();
    }
}
