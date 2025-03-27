package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class GetCommandTests {
    private final String FAIL_PREFIX = "[Fail]";
    private GameServer server;

    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get(
            new StringBuilder()
                .append("config")
                .append(File.separator)
                .append("basic-entities.dot")
                .toString()
        ).toAbsolutePath().toFile();

        File actionsFile = Paths.get(
            new StringBuilder()
                .append("config")
                .append(File.separator)
                .append("basic-actions.xml")
                .toString()
        ).toAbsolutePath().toFile();

        this.server = new GameServer(entitiesFile, actionsFile);
    }

    // TEST: get with valid cmd
    @Test
    void testBasicGet() {
        String response = sendCommandToServer("simon: look");
        assertTrue(response.contains("axe"), "Should have axe in location");
        assertTrue(response.contains("potion"), "Should have potion in location");

        sendCommandToServer("simon: get potion");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("potion"), "Get potion should work");

        sendCommandToServer("simon: GeT axe"); // case-insensitive
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("axe"), "Get axe should work");

        response = sendCommandToServer("simon: get axe");
        assertTrue(response.contains(this.FAIL_PREFIX), "Should get same thing again");
    }

    // TEST: get with invalid cmd
    @Test
    void testInvalidCmdGet() {
        // invalid word
        sendCommandToServer("simon: getA potion");
        String response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("nothing"), "Get potion should fail");

        // get two at the same time
        sendCommandToServer("simon: GET axe potion"); // case-insensitive
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("nothing"), "Get potion should fail");
    }

    // TEST: get with other players
    @Test
    void testGetWithMultiPlayer() {
        String response = sendCommandToServer("simon: look");
        assertTrue(response.contains("axe"), "Should have axe in location");
        assertTrue(response.contains("potion"), "Should have potion in location");

        sendCommandToServer("simon: GET axe"); // case-insensitive
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("axe"), "Get axe should work");

        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("axe"), "Should not have axe in location");

        // other player share same location
        response = sendCommandToServer("joe: get axe");
        assertTrue(response.contains(this.FAIL_PREFIX), "Should not get from other's inv");

        response = sendCommandToServer("joe: get potion");
        assertTrue(response.contains("potion"), "Get potion should work");

        // cannot see potion for both players
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("potion"), "Should not have potion in location");
    }

    // TEST: get with decorative words & unexpected ordering
    @Test
    void testGetWithDecorativeWords() {
        sendCommandToServer("simon: please find potion and get it");
        String response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("potion"), "Decorative words should work for get");
    }

    // TEST: get without any artefact
    @Test
    void testGetWithoutArtefact() {
        String response = sendCommandToServer("simon: get");
        assertTrue(response.contains(this.FAIL_PREFIX),
            "Get without artefact should fail");
    }

    // TEST: get with invalid artefact
    @Test
    void testGetWithInvalidArtefact() {
        // get with non-exist artefact
        String response = sendCommandToServer("simon: get xyz");
        assertTrue(response.contains(this.FAIL_PREFIX),
            "Should fail to get invalid artefact 'xyz'");

        // get with wrong target type: location
        response = sendCommandToServer("simon: get forest");
        assertTrue(response.contains(this.FAIL_PREFIX), "Should fail to get a location");

        // get with wrong target type: player
        sendCommandToServer("joe: look");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("joe"), "Should see Joe in the location");

        response = sendCommandToServer("simon: get joe");
        assertTrue(response.contains(this.FAIL_PREFIX), "Should fail to get a player");

        // get with wrong target type: character
        sendCommandToServer("joe: goto forest");
        sendCommandToServer("joe: get key");
        sendCommandToServer("joe: goto cabin");
        sendCommandToServer("joe: unlock with key");

        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cellar"), "Should see path to cellar");

        sendCommandToServer("joe: goto cellar");
        response = sendCommandToServer("joe: look").toLowerCase();
        assertTrue(response.contains("elf"), "Should see elf in cellar");
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("joe"), "Should not see Joe at cabin");

        response = sendCommandToServer("simon: get elf");
        assertTrue(response.contains(this.FAIL_PREFIX), "Should fail to get a character");
    }

    // TEST: get with an artefact from other place
    @Test
    void testGetArtefactFromOtherLocation() {
        String response = sendCommandToServer("simon: get key");
        assertTrue(response.contains(this.FAIL_PREFIX),
            "Should get artefact from other location");
        response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("key"), "Should not have key in inventory");

        sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        assertTrue(response.contains("key"), "Should get key from right location");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("key"), "Should have key in inventory");
    }

    // TEST: get with multiple artefacts
    @Test
    void testGetMultipleArtefacts() {
        // before get
        String response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("axe"), "Should not have axe in inventory");

        // get multiple artefacts
        response = sendCommandToServer("simon: get potion axe");
        assertTrue(response.contains(this.FAIL_PREFIX),
            "Should not get multiple artefacts");

        // get artefact with other artefact in other location
        response = sendCommandToServer("simon: get potion key");
        assertTrue(response.contains("potion"),
            "Should get artefact with artefact in other location");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("potion"), "Should have axe in inventory");

        // get artefact with non-entity word
        response = sendCommandToServer("simon: get axe abc");
        assertTrue(response.contains("axe"), "Should get artefact with non-entity word");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("axe"), "Should have axe in inventory");
    }

    // TEST: get artefact which is in other location
    @Test
    void testGetArtefactInOtherMap() {
        // before get
        String response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("key"), "Should not have key in inventory");
        response = sendCommandToServer("simon: see");
        assertFalse(response.contains("key"), "Should not have key in current location");

        // get from wrong location
        response = sendCommandToServer("simon: get key");
        assertTrue(response.contains(this.FAIL_PREFIX),
            "Should not get artefact from wrong location");

        // move to right location & get
        sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        assertTrue(response.contains("key"), "Should get key in right location");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("key"), "Should not have key in inventory");
    }

    // TEST: get artefact which is in storeroom
    @Test
    void testGetArtefactInStoreroom() {
        // cannot get artefact in storeroom
        String response = sendCommandToServer("simon: get log");
        assertTrue(response.contains(this.FAIL_PREFIX),
            "Should not get artefact from storeroom");

        // prepare log
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: axe cutdown");

        // get log
        response = sendCommandToServer("simon: get log");
        assertTrue(response.contains("log"), "Should get log after producing");

        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("log"), "Should have log in inventory");
    }

    // TEST: get artefact which is in own inventory
    @Test
    void testGetArtefactInOwnInventory() {
        String response = sendCommandToServer("simon: get potion");
        assertTrue(response.contains("potion"), "Should get potion successfully");

        response = sendCommandToServer("simon: get potion");
        assertTrue(response.contains(this.FAIL_PREFIX), "Should not get from inventory");


        response = sendCommandToServer("joe: get potion");
        assertTrue(response.contains(this.FAIL_PREFIX), "Should not get from other's inv");
    }
}
