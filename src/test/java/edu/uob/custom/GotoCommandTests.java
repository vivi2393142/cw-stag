package edu.uob.custom;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class GotoCommandTests extends BaseSTAGTests {
    // basic goto functionality from starting location
    @Test
    void testBasicGoto() {
        this.assertCommandSucceed("simon: look", "cabin"); // start at cabin
        this.assertCommandSucceed("simon: look", "forest"); // path exists
        this.assertCommandSucceed("simon: goto forest"); // move to forest
        this.assertCommandSucceed("simon: look", "forest"); // confirm new location
        this.assertCommandSucceed("simon: look", "cabin");  // path back
    }

    // case insensitivity of goto command
    @Test
    void testCaseInsensitivity() {
        this.assertCommandSucceed("simon: GOTO forest");
        this.assertCommandSucceed("simon: look", "forest");
        this.assertCommandSucceed("simon: GoTo cabin");
        this.assertCommandSucceed("simon: look", "cabin");
    }

    // goto with invalid or malformed commands
    @Test
    void testInvalidGotoCommands() {
        this.assertCommandFail("simon: goto"); // no destination
        this.assertCommandFail("simon: gotcha forest"); // invalid command
    }

    // goto with multiple locations
    @Test
    @Tag("extended")
    void testMultipleLocations() {
        // two locations with only in path
        this.assertCommandSucceed("simon: goto forest cabin");
        // two locations with two in path
        this.assertCommandSucceed("simon: look", "in a deep dark forest");
        this.assertCommandFail("simon: goto cabin riverbank");
    }

    // goto with multiple players moving independently
    @Test
    void testMultiPlayerMovement() {
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: look", "forest");
        // Joe stays in cabin
        this.assertCommandSucceed("joe: look", "cabin");
        // Simon not visible
        this.assertCommandSucceed("joe: look", "simon", true);
        this.assertCommandSucceed("joe: goto forest");
        // Joe sees Simon
        this.assertCommandSucceed("joe: look", "simon");
    }

    // goto to a location with no direct path
    @Test
    void testNoDirectPath() {
        this.assertCommandSucceed("simon: look", "cabin");
        // no direct path from cabin
        this.assertCommandFail("simon: goto cellar");
        // still in cabin
        this.assertCommandSucceed("simon: look", "cabin");
        // cellar not visible
        this.assertCommandSucceed("simon: look", "cellar", true);
    }

    // goto with decorative words
    @Test
    void testDecorativeWords() {
        this.assertCommandSucceed("simon: please goto forest");
        this.assertCommandSucceed("simon: look", "forest");
        this.assertCommandSucceed("simon: goto back to cabin");
        this.assertCommandSucceed("simon: look", "cabin");
    }

    // goto to current location
    @Test
    void testGotoCurrentLocation() {
        this.assertCommandSucceed("simon: look", "cabin");
        this.assertCommandFail("simon: goto cabin"); // already in cabin
        this.assertCommandSucceed("simon: look", "cabin"); // location unchanged
    }

    // goto to a non-existent location
    @Test
    void testNonExistentLocation() {
        this.assertCommandFail("simon: goto nowhere");
        this.assertCommandSucceed("simon: look", "cabin"); // location unchanged
    }

    // goto after player death (health extension)
    @Test
    @Tag("extended")
    void testGotoAfterDeath() {
        this.gotoCellar("simon");
        this.assertCommandSucceed("simon: attack elf"); // health decreases
        this.assertCommandSucceed("simon: attack elf"); // health decreases
        this.assertCommandSucceed("simon: attack elf"); // health to 0, respawn
        this.assertCommandSucceed("simon: look", "cabin"); // back at start
        this.assertCommandSucceed("simon: goto cellar"); // path still here
    }

    // goto with multiple paths available
    @Test
    @Tag("extended")
    void testMultiplePaths() {
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: look", "cabin");
        this.assertCommandSucceed("simon: look", "riverbank");
        this.assertCommandSucceed("simon: goto riverbank");
        this.assertCommandSucceed("simon: look", "riverbank");
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: look", "forest");
    }

    // goto with invalid player name
    @Test
    void testInvalidPlayerName() {
        this.assertCommandFail("123: goto forest"); // invalid characters
        this.assertCommandSucceed("simon-smith: goto forest");
        this.assertCommandSucceed("simon-smith: look", "forest");
        this.assertCommandSucceed("o'malley: goto forest");
        this.assertCommandSucceed("o'malley: look", "forest");
    }

    // goto when carrying items
    @Test
    void testGotoWithInventory() {
        this.assertCommandSucceed("simon: get potion");
        this.assertCommandSucceed("simon: inv", "potion");
        this.assertCommandSucceed("simon: goto forest");
        // potion moves with Simon
        this.assertCommandSucceed("simon: inv", "potion");
        // not in forest
        this.assertCommandSucceed("simon: look", "potion", true);
    }

    // goto to storeroom (should not be allowed)
    @Test
    void testGotoStoreroom() {
        this.assertCommandFail("simon: goto storeroom");
        // still in cabin
        this.assertCommandSucceed("simon: look", "cabin");
        // not a path
        this.assertCommandSucceed("simon: look", "storeroom", true);
    }

    // goto after dropping an item
    @Test
    void testGotoAfterDrop() {
        this.assertCommandSucceed("simon: get axe");
        this.assertCommandSucceed("simon: drop axe");
        this.assertCommandSucceed("simon: goto forest");
        // axe stays in cabin
        this.assertCommandSucceed("simon: look", "axe", true);
        // Joe sees axe in cabin
        this.assertCommandSucceed("joe: look", "axe");
    }

    // goto with no paths from current location
    @Test
    @Tag("extended")
    void testNoOutgoingPaths() {
        this.assertCommandSucceed("simon: get axe");
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: cut with axe");
        this.assertCommandSucceed("simon: get log");
        this.assertCommandSucceed("simon: goto riverbank");
        this.assertCommandSucceed("simon: bridge on river");
        this.assertCommandSucceed("simon: goto clearing");
        this.assertCommandFail("simon: goto forest"); // No outgoing path
        this.assertCommandSucceed("simon: look", "riverbank"); // only incoming
    }

    // goto with ambiguous or extraneous input
    @Test
    void testAmbiguousInput() {
        this.assertCommandSucceed("simon: goto forest now");
        this.assertCommandSucceed("simon: look", "forest");
        this.assertCommandFail("simon: goto forest riverbank"); // multiple targets
    }

    // goto after another player modifies the path
    @Test
    @Tag("extended")
    void testPathModifiedByOtherPlayer() {
        this.assertCommandSucceed("joe: get axe");
        this.assertCommandSucceed("joe: goto forest");
        this.assertCommandSucceed("joe: cut with axe");
        this.assertCommandSucceed("joe: get log");
        this.assertCommandSucceed("joe: goto riverbank");
        this.assertCommandSucceed("joe: bridge on river");
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: goto riverbank");
        this.assertCommandSucceed("simon: goto clearing");
        this.assertCommandSucceed("simon: look", "clearing");
    }

    @Test
    void testDuplicatedValidSubject() {
        this.assertCommandSucceed("simon: look",
            "in a log cabin");
        this.assertCommandSucceed("simon: look",
            "in a dark forest", true);
        this.assertCommandSucceed("simon: goto forest forest");
        this.assertCommandSucceed("simon: look",
            "in a log cabin", true);
        this.assertCommandSucceed("simon: look",
            "in a dark forest");
    }
}
