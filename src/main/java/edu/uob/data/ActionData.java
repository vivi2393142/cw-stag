package edu.uob.data;

import java.util.HashSet;

public class ActionData {
    private HashSet<String> triggers;
    private HashSet<String> subjects;
    private HashSet<String> consumedEntities;
    private HashSet<String> producedEntities;
    private String narration;

    public ActionData(HashSet<String> triggers, HashSet<String> subjects,
                      HashSet<String> consumedEntities, HashSet<String> producedEntities, String narration) {
        this.triggers = triggers;
        this.subjects = subjects;
        this.consumedEntities = consumedEntities;
        this.producedEntities = producedEntities;
        this.narration = narration;
    }

    public HashSet<String> getTriggers() {
        return this.triggers;
    }

    public HashSet<String> getSubjects() {
        return this.subjects;
    }

    public HashSet<String> getConsumedEntities() {
        return this.consumedEntities;
    }

    public HashSet<String> getProducedEntities() {
        return this.producedEntities;
    }

    public String getNarration() {
        return this.narration;
    }
}
