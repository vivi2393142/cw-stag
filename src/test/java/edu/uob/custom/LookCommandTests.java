package edu.uob.custom;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class LookCommandTests extends BaseSTAGTests {
    // basic look functionality in starting location
    @Test
    void testBasicLook() {
        this.assertCommandSucceed("simon: look", "cabin"); // starting location
        this.assertCommandSucceed("simon: look", "axe");   // artefact in cabin
        this.assertCommandSucceed("simon: look", "potion");
        this.assertCommandSucceed("simon: look", "trapdoor");
        this.assertCommandSucceed("simon: look", "forest"); // path to forest
    }

    // case insensitivity of look command
    @Test
    void testCaseInsensitivity() {
        this.assertCommandSucceed("simon: LOOK", "cabin");
        this.assertCommandSucceed("simon: LoOk", "axe");
        this.assertCommandSucceed("simon: look", "potion");
    }

    // look with invalid or malformed commands
    @Test
    void testInvalidLookCommands() {
        this.assertCommandFail("simon: lookat"); // invalid command variation
        this.assertCommandSucceed("simon: look at"); // extra words without meaning
        this.assertCommandFail("simon:"); // empty command after player name
    }

    // look with multiple players in the same location
    @Test
    void testMultiPlayerVisibility() {
        this.assertCommandSucceed("simon: look", "cabin");
        // Joe joins same location
        this.assertCommandSucceed("joe: look", "cabin");
        this.assertCommandSucceed("simon: look", "joe"); // Simon sees Joe
        this.assertCommandSucceed("joe: look", "simon"); // Joe sees Simon
        // artefacts still visible
        this.assertCommandSucceed("simon: look", "axe");
    }

    // look after moving to a different location
    @Test
    void testLookAfterMove() {
        this.assertCommandSucceed("simon: look", "cabin"); // current location
        this.assertCommandSucceed("simon: look", "forest"); // path
        this.assertCommandSucceed("simon: look", "cellar", true);

        this.gotoCellar("simon");
        this.assertCommandSucceed("simon: look", "cellar"); // current location
        this.assertCommandSucceed("simon: look", "cabin"); // path
        this.assertCommandSucceed("simon: look", "forest", true);
    }

    // look with decorative words
    @Test
    void testDecorativeWords() {
        this.assertCommandSucceed("simon: please look around", "cabin");
        this.assertCommandSucceed("simon: look at the room", "axe");
        this.assertCommandSucceed("simon: look carefully", "potion");
    }

    // look in a location with no artefacts
    @Test
    void testEmptyLocation() {
        // move to cellar, which initially has only an elf (character)
        this.gotoCellar("simon");

        this.assertCommandSucceed("simon: look", "cellar");
        this.assertCommandSucceed("simon: look", "elf");
        this.assertCommandSucceed("simon: look", "cabin");
        this.assertCommandSucceed("simon: look", "axe", true);
    }

    // look when another player holds an artefact
    @Test
    void testArtefactInOtherPlayerInventory() {
        this.assertCommandSucceed("simon: get potion");
        this.assertCommandSucceed("joe: look", "potion", true);
        this.assertCommandSucceed("joe: look", "axe");
        this.assertCommandSucceed("simon: look", "potion", true);
    }

    // look with entities in storeroom (should not be visible)
    @Test
    @Tag("extended")
    void testStoreroomEntities() {
        // shovel in storeroom
        this.assertCommandSucceed("simon: get coin");
        this.gotoCellar("simon");
        this.assertCommandSucceed("simon: look", "shovel", true);

        // trigger action to bring gold into game
        this.assertCommandSucceed("simon: pay to elf");
        this.assertCommandSucceed("simon: look", "shovel");

        // trigger action to bring shovel into game
        this.assertCommandSucceed("simon: get shovel");
        this.assertCommandSucceed("simon: look", "shovel", true);
        this.assertCommandSucceed("simon: goto cabin");

        this.assertCommandSucceed("simon: get axe");
        this.assertCommandSucceed("simon: look", "axe", true);
        this.assertCommandSucceed("simon: goto forest");

        this.assertCommandSucceed("simon: cut with axe");
        this.assertCommandSucceed("simon: look", "log");
        this.assertCommandSucceed("simon: get log");
        this.assertCommandSucceed("simon: look", "log", true);
        this.assertCommandSucceed("simon: goto riverbank");

        this.assertCommandSucceed("simon: drop log");
        this.assertCommandSucceed("simon: look", "log");
        this.assertCommandSucceed("simon: look", "clearing", true);
        this.assertCommandSucceed("simon: bridge on river");
        this.assertCommandSucceed("simon: look", "log", true);
        this.assertCommandSucceed("simon: look", "clearing");
        this.assertCommandSucceed("simon: goto clearing");

        this.assertCommandSucceed("simon: look", "soil");
        this.assertCommandSucceed("simon: dig the ground", "gold");
        this.assertCommandSucceed("simon: look", "gold");
        this.assertCommandSucceed("simon: look", "hole");
        this.assertCommandSucceed("simon: look", "soil", true);
    }

    // look with multiple paths from a location
    @Test
    @Tag("extended")
    void testMultiplePaths() {
        this.assertCommandSucceed("simon: look", "forest");
        this.assertCommandSucceed("simon: goto forest");

        this.assertCommandSucceed("simon: look", "cabin");
        this.assertCommandSucceed("simon: look", "riverbank");
        this.assertCommandSucceed("simon: goto riverbank");

        this.assertCommandSucceed("simon: look", "forest");
    }

    // TODO: look in a location with no paths
    @Test
    void testNoPaths() {
    }

    // look with invalid player name
    @Test
    void testInvalidPlayerName() {
        // invalid characters in name
        this.assertCommandFail("123: look");
        // valid with hyphen
        this.assertCommandSucceed("simon-smith: look", "cabin");
        // valid with apostrophe
        this.assertCommandSucceed("o'malley: look", "axe");
    }

    // look after player death (health extension)
    @Test
    @Tag("extended")
    void testLookAfterDeath() {
        // assuming health extension is implemented
        this.assertCommandSucceed("simon: get potion");
        this.assertCommandSucceed("simon: goto forest");

        this.assertCommandSucceed("simon: get key");
        this.assertCommandSucceed("simon: goto cabin");

        this.assertCommandSucceed("simon: unlock with key");
        this.assertCommandSucceed("simon: goto cellar");

        this.assertCommandSucceed("simon: attack elf"); // health decreases
        this.assertCommandSucceed("simon: attack elf"); // health decreases
        this.assertCommandSucceed("simon: attack elf"); // health to 0, respawn
        this.assertCommandSucceed("simon: look", "cabin"); // back at start
        this.assertCommandSucceed(
            "simon: look", "potion", true); // items dropped
    }

    // look with ambiguous or extraneous input
    @Test
    void testAmbiguousInput() {
        this.assertCommandSucceed("simon: look axe", "cabin");
        this.assertCommandSucceed("simon: look potion forest", "axe");
    }
}
