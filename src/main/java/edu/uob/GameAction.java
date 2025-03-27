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
    public Set<String> getTriggers() {return triggers;}
    public Set<String> getSubjects() {return subjects;}
    public int getConsumedHealth() {return consumedHealth;}
    public int getProducedHealth() {return producedHealth;}
    public Set<String> getConsumedEntities() {return consumedEntities;}
    public Set<String> getProducedEntities() {return producedEntities;}
    public String getNarration() {return narration;}

    /* Utility */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("triggers: ").append(this.triggers.toString()).append("\n");
        result.append("subjects: ").append(this.subjects.toString()).append("\n");
        result.append("consumedHealth: ").append(this.consumedHealth).append("\n");
        result.append("producedHealth: ").append(this.producedHealth).append("\n");
        result.append("consumed: ").append(this.consumedEntities.toString()).append("\n");
        result.append("produced: ").append(this.producedEntities.toString()).append("\n");
        result.append("narration: ").append(this.narration);
        return result.toString();
    }
}