package edu.uob;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActionCommandTests extends BaseSTAGTests {
    /* Actions */
    // TODO: TEST: trigger in two word, e.g. "cut down"

    // TEST: subjects mentioned in command are more than that in action
    @Test
    void testMoreSubjectsThanAction() {
        // prepare cut
        sendCommandToServer("yang-yang: get axe");
        sendCommandToServer("yang-yang: goto forest");

        // cut with extra subject
        String response = sendCommandToServer("yang-yang: cut tree key");
        assertTrue(response.contains(this.FAIL_PREFIX),
            "Extra subject 'key' should prevent match");

        // cut without extra subject
        response = sendCommandToServer("yang-yang: cut tree");
        assertFalse(response.contains(this.FAIL_PREFIX), "Cut should work");
    }

    // TEST: only one trigger and only one subject in command, and is valid situation
    @Test
    void testSingleTriggerSingleSubjectValid() {
        // prepare cut
        sendCommandToServer("ken: get axe");
        sendCommandToServer("ken: goto forest");

        // cut with signe trigger & subject
        String response = sendCommandToServer("ken: cut tree").toLowerCase();
        assertTrue(response.contains("you cut"),
            "Single trigger and subject should work when valid");
    }

    // TEST: only one trigger and only one subject in command, and isn't valid situation
    @Test
    void testSingleTriggerSingleSubjectInvalid() {
        // prepare cut
        sendCommandToServer("ken: get axe");
        sendCommandToServer("ken: goto forest");

        // cut
        String response = sendCommandToServer("ken: cut potion");
        assertTrue(response.contains(this.FAIL_PREFIX),
            "Invalid subject 'potion' should fail");
    }

    // TODO: TEST: 2 actions have same trigger
    //  1. 2 valid, match first one
    //  2. only second one valid, match second one

    // TEST: match subject is in inventory
    @Test
    void testMatchSubjectInInventory() {
        // prepare cut
        sendCommandToServer("ken: get axe");
        sendCommandToServer("ken: goto forest");

        // cut
        String response = sendCommandToServer("ken: cut with axe").toLowerCase();
        assertTrue(response.contains("you cut"), "Cut should work");
    }

    // TEST: match subject is in location as a furniture
    @Test
    void testMatchFurnitureSubject() {
        // prepare unlock
        sendCommandToServer("ken: goto forest");
        sendCommandToServer("ken: get key");
        sendCommandToServer("ken: goto cabin");

        // unlock with subject as a furniture
        String response = sendCommandToServer("ken: open by key").toLowerCase();
        assertTrue(response.contains("you unlock"), "Unlock should work");
    }

    // TODO: TEST: match subject is in location as a character

    // TODO: TEST: match subject is in location as a path

    // TEST: match subject is in other player's inventory
    @Test
    void testMatchSubjectInOthersInventory() {
        // prepare unlock
        sendCommandToServer("wang: get axe");
        sendCommandToServer("wang: goto forest");
        sendCommandToServer("ken: goto forest");

        // unlock with subject as a furniture
        String response = sendCommandToServer("ken: cutdown tree");
        assertTrue(response.contains(this.FAIL_PREFIX),
            "Should not cut with subject in other's inventory");

        response = sendCommandToServer("wang: cutdown tree").toLowerCase();
        assertTrue(response.contains("you cut"), "Should cut tree successfully");
    }

    // TEST: match subject is in other location as an artefact

    // TEST: match subject is in other location as a furniture

    // TEST: match subject is in other location as a character

    // TEST: match subject is in other location as a path

    // TEST: match consume is in inventory

    // TEST: match consume is in location as an artefact

    // TEST: match consume is in location as a furniture

    // TEST: match consume is in location as a character

    // TEST: match consume is in location as a path

    // TEST: match consume is in other player's inventory

    // TEST: match consume is in other location as an artefact

    // TEST: match consume is in other location as a furniture

    // TEST: match consume is in other location as a character

    // TEST: match consume is in other location as a path

    // TEST: match produce is in storeroom as an artefact

    // TEST: match produce is in storeroom as a furniture

    // TEST: match produce is in storeroom as a character

    // TEST: match produce is in storeroom as a path

    // TEST: match produce is in one's inventory

    // TEST: match produce is in other location as a furniture

    // TEST: match produce is in other location as a character

    // TEST: match produce is in other location as a path

    // TEST: match 2 triggers in one action
}
