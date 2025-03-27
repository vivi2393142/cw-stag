package edu.uob;

public class ValueUtils {
    private ValueUtils() {}

    public static boolean containIgnoreCase(String input, String target) {
        if (input == null || target == null) return false;
        String lowerCaseInput = input.toLowerCase();
        String lowerCaseTarget = target.toLowerCase();
        return lowerCaseInput.matches(
            new StringBuilder()
                .append(".*\\b").append(lowerCaseTarget).append("\\b.*")
                .toString()
        );
    }

    public static boolean containIgnoreCase(String input, Keyword keyword) {
        return containIgnoreCase(input, keyword.name());
    }
}
