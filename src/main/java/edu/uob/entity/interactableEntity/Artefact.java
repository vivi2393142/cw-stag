package edu.uob.entity.interactableEntity;

import edu.uob.data.EntityData;
import edu.uob.entity.Location;
import edu.uob.entity.Player;

public class Artefact extends InteractableEntity {
    private Player belongPlayer;

    public Artefact(EntityData data, Location location) {
        super(data, location, EntityType.ARTEFACT);
        this.belongPlayer = null;
    }

    /* Getter */
    public Player getBelongPlayer() {
        return this.belongPlayer;
    }

    /* Update */
    public void setBelongPlayer(Player player) {
        this.belongPlayer = player;
    }
}
