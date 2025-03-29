package edu.uob;

import java.util.Set;

public class GameAction {
    private final Set<String> triggers;
    private final Set<String> subjects;
    private final int consumedHealth;
    private final int producedHealth;
    private final Set<String> consumedEntities;
    private final Set<String> producedEntities;
    private final String narration;

    public GameAction(Set<String> triggers, Set<String> subjects, int consumedHealth, int producedHealth,
                      Set<String> consumedEntities, Set<String> producedEntities, String narration) {
        this.triggers = triggers;
        this.subjects = subjects;
        this.consumedHealth = consumedHealth;
        this.producedHealth = producedHealth;
        this.consumedEntities = consumedEntities;
        this.producedEntities = producedEntities;
        this.narration = narration != null ? narration : "No description available.";
    }

    /* Getter */
    public Set<String> getTriggers() {return this.triggers;}
    public Set<String> getSubjects() {return this.subjects;}
    public int getConsumedHealth() {return this.consumedHealth;}
    public int getProducedHealth() {return this.producedHealth;}
    public Set<String> getConsumedEntities() {return this.consumedEntities;}
    public Set<String> getProducedEntities() {return this.producedEntities;}
    public String getNarration() {return this.narration;}

    /* Utility */
    @Override
    public String toString() {
        return new StringBuilder()
            .append("triggers: ").append(this.triggers.toString()).append("\n")
            .append("subjects: ").append(this.subjects.toString()).append("\n")
            .append("consumedHealth: ").append(this.consumedHealth).append("\n")
            .append("producedHealth: ").append(this.producedHealth).append("\n")
            .append("consumed: ").append(this.consumedEntities.toString()).append("\n")
            .append("produced: ").append(this.producedEntities.toString()).append("\n")
            .append("narration: ").append(this.narration)
            .toString();
    }
}