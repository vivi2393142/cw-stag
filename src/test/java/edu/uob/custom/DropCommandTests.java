package edu.uob;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DropCommandTests {
    // TEST: drop with "drop", "DROP", "dRoP"
    @Test
    void testBasicDrop() {
        String[] variants = {"drop", "DROP", "dRoP"};
        for (String variant : variants) {
            // get axe
            sendCommandToServer("simon: get axe");
            String response = sendCommandToServer("simon: inv");
            assertTrue(response.contains("axe"), "Should have axe in inventory");

            // drop axe
            response = sendCommandToServer(
                new StringBuilder().append("simon: axe ").append(variant).toString());
            assertTrue(response.contains("axe"), "Should drop axe");
            response = sendCommandToServer("simon: inv");
            assertFalse(response.contains("axe"), "Should not have axe in inventory");
        }

        String[] failVariants = {"dropA", "DROP1", "dRoPz"};
        sendCommandToServer("simon: get axe");
        for (String variant : failVariants) {
            String response = sendCommandToServer(
                new StringBuilder().append("simon: ").append(variant).append(" axe").toString());
            assertTrue(response.contains(this.FAIL_PREFIX), "Should fail to drop axe");
        }
    }

    // TEST: drop with other players
    @Test
    void testDropWithMultiPlayer() {
        // get axe
        sendCommandToServer("ryan: get axe"); // case-insensitive
        String response = sendCommandToServer("ryan: inv");
        assertTrue(response.contains("axe"), "Should have axe in inventory");
        response = sendCommandToServer("bob: look");
        assertFalse(response.contains("axe"), "Should not have axe in location");

        // drop axe
        sendCommandToServer("ryan: DROP axe"); // case-insensitive
        response = sendCommandToServer("bob: look");
        assertTrue(response.contains("axe"), "Should have axe in location");
        response = sendCommandToServer("ryAn: inv");
        assertFalse(response.contains("axe"), "Should not have axe in inventory");

        // other player get axe
        response = sendCommandToServer("BOB: get axe"); // case-insensitive
        assertTrue(response.contains("axe"), "Should get axe");
        response = sendCommandToServer("bob: inv");
        assertTrue(response.contains("axe"), "Should have axe in inventory");
        response = sendCommandToServer("RYAN: look");
        assertFalse(response.contains("axe"), "Should not have axe in location");
    }

    // TEST: drop with decorative words & unexpected order
    @Test
    void testDropWithDecorativeWords() {
        sendCommandToServer("simon: get potion");
        sendCommandToServer("simon: please drop the potion");
        String response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("potion"), "Decorative words should work for drop");
    }

    // TEST: drop without any artefact
    @Test
    void testDropWithoutArtefact() {
        String response = sendCommandToServer("simon: drop");
        assertTrue(response.contains(this.FAIL_PREFIX), "Drop without artefact should fail");
    }

    // TEST: drop with invalid artefact
    @Test
    void testDropWithInvalidArtefact() {
        sendCommandToServer("simon: get potion");

        // drop with non-exist artefact
        String response = sendCommandToServer("simon: drop xyz");
        assertTrue(response.contains(this.FAIL_PREFIX),
            "Should fail to drop invalid artefact 'xyz'");

        // drop with artefact in other location
        response = sendCommandToServer("simon: drop key");
        assertTrue(response.contains(this.FAIL_PREFIX),
            "Should fail to drop artefact in other location");

        // drop with artefact in storeroom
        response = sendCommandToServer("simon: drop log");
        assertTrue(response.contains(this.FAIL_PREFIX),
            "Should fail to drop artefact in storeroom");

        // get with wrong target type: location
        response = sendCommandToServer("simon: drop forest");
        assertTrue(response.contains(this.FAIL_PREFIX), "Should fail to drop a location");

        // drop with wrong target type: player
        sendCommandToServer("alice: look");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("alice"), "Should see Alice in the location");

        response = sendCommandToServer("simon: drop alice");
        assertTrue(response.contains(this.FAIL_PREFIX), "Should fail to drop a player");

        // get with wrong target type: character
        sendCommandToServer("alice wang: goto forest");
        sendCommandToServer("alice wang: get key");
        sendCommandToServer("alice wang: goto cabin");
        sendCommandToServer("alice wang: unlock with key");

        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cellar"), "Should see path to cellar");

        sendCommandToServer("alice wang: goto cellar");
        response = sendCommandToServer("alice wang: look").toLowerCase();
        assertTrue(response.contains("elf"), "Should see elf in cellar");
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("alice wang"), "Should not see Alice at cabin");

        response = sendCommandToServer("simon: drop elf");
        assertTrue(response.contains(this.FAIL_PREFIX), "Should fail to drop a character");
    }

    // TEST: drop with multiple artefacts
    @Test
    void testDropMultipleArtefacts() {
        // before drop
        sendCommandToServer("alex: get axe");
        sendCommandToServer("alex: get potion");

        // drop multiple artefact
        String response = sendCommandToServer("simon: drop potion axe");
        assertTrue(response.contains(this.FAIL_PREFIX),
            "Should not drop multiple artefacts");

        // drop artefact with other artefact in other's inventory
        response = sendCommandToServer("alex: inv");
        assertTrue(response.contains("potion"), "Should have axe in inventory");
        assertTrue(response.contains("axe"), "Should have axe in inventory");

        sendCommandToServer("lee: goto forest");
        sendCommandToServer("lee: get key");

        response = sendCommandToServer("alex: drop potion key");
        assertTrue(response.contains("potion"),
            "Should drop artefact with artefact in other location");
        response = sendCommandToServer("alex: inv");
        assertFalse(response.contains("potion"), "Should not have potion in inventory");

        // drop artefact with non-entity word
        response = sendCommandToServer("alex: drop axe abc");
        assertTrue(response.contains("axe"), "Should drop artefact with non-entity word");
        response = sendCommandToServer("alex: inv");
        assertFalse(response.contains("axe"), "Should not have axe in inventory");
    }
}
