package edu.uob.entity.interactableEntity;

import edu.uob.data.EntityData;
import edu.uob.EntityType;

public class Artefact extends InteractableEntity {
    private String belongPlayer; // indicate artefact is in whose inventory

    public Artefact(EntityData data, String locationName) {
        super(data, locationName, EntityType.ARTEFACT);
        this.belongPlayer = null;
    }

    /* Getter */
    public String getBelongPlayer() {
        return this.belongPlayer;
    }

    /* Update */
    public void setBelongPlayer(String playerName) { // no location when has belongPlayer
        this.belongPlayer = playerName;
        if (playerName != null) this.setLocation(null);
    }

    @Override
    public void setLocation(String locationName) { // no belongPlayer when has location
        super.setLocation(locationName);
        if (locationName != null) this.belongPlayer = null;
    }

    /* Utility */
    @Override
    public String toString() {
        return new StringBuilder(super.toString())
            .append("\n")
            .append("belongPlayer: ").append(this.belongPlayer)
            .toString();
    }
}
