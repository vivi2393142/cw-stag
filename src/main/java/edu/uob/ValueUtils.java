package edu.uob;

import java.util.HashSet;
import java.util.Set;

public class ValueUtils {
    private ValueUtils() {}

    public static boolean containsIgnoreCase(String input, String target) {
        if (input == null || target == null) return false;
        String lowerCaseInput = input.toLowerCase();
        String lowerCaseTarget = target.toLowerCase();
        return lowerCaseInput.matches(
            new StringBuilder()
                .append(".*\\b").append(lowerCaseTarget).append("\\b.*")
                .toString()
        );
    }

    public static boolean containsIgnoreCase(String input, Keyword keyword) {
        return containsIgnoreCase(input, keyword.name());
    }

    public static boolean matchesAnyKeyword(String input) {
        return matchesAnyKeyword(input, new HashSet<>());
    }

    public static boolean matchesAnyKeyword(String input, Set<Keyword> withoutKeywords) {
        if (input == null) return false;
        for (Keyword keyword : Keyword.values()) {
            if (!withoutKeywords.contains(keyword) && keyword.name().equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInteractableEntity(EntityType type) {
        return type == EntityType.ARTEFACT
               || type == EntityType.FURNITURE
               || type == EntityType.CHARACTER;
    }
}
