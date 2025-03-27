package edu.uob.custom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetCommandTests extends BaseSTAGTests {
    // TEST: get with valid cmd
    @Test
    void testBasicGet() {
        this.assertCommandSucceed("simon: look", "axe");
        this.assertCommandSucceed("simon: look", "potion");

        this.assertCommandSucceed("simon: get potion");
        this.assertCommandSucceed("simon: inv", "potion");

        this.assertCommandSucceed("simon: GeT axe"); // case-insensitive
        this.assertCommandSucceed("simon: inv", "axe");

        this.assertCommandFail("simon: get axe");
    }

    // TEST: get with invalid cmd
    @Test
    void testInvalidCmdGet() {
        // invalid word
        this.assertCommandFail("simon: getA potion");
        this.assertCommandSucceed("simon: inv", "nothing");

        // get two at the same time
        this.assertCommandFail("simon: GET axe potion"); // case-insensitive
        this.assertCommandSucceed("simon: inv", "nothing");
    }

    // TEST: get with other players
    @Test
    void testWithMultiPlayer() {
        this.assertCommandSucceed("simon: look", "axe");
        this.assertCommandSucceed("simon: look", "potion");

        this.assertCommandSucceed("simon: GET axe", "axe");
        this.assertCommandSucceed("simon: inv", "axe");
        this.assertCommandSucceed("simon: look", "axe", true);

        // other player share same location
        this.assertCommandFail("joe: GET axe");
        this.assertCommandSucceed("joe: get potion", "potion");

        // cannot see potion for both players
        this.assertCommandSucceed("simon: look", "potion", true);
    }

    // TEST: get with decorative words & unexpected ordering
    @Test
    void testWithDecorativeWords() {
        this.assertCommandSucceed("simon: please find potion and get it", "potion");
        this.assertCommandSucceed("simon: inv", "potion");
    }

    // TEST: get without any artefact
    @Test
    void testWithoutArtefact() {
        this.assertCommandFail("simon: get");
    }

    // TEST: get with invalid artefact
    @Test
    void testWithInvalidArtefact() {
        // get with non-exist artefact
        this.assertCommandFail("simon: get xyz");

        // get with wrong target type: location
        this.assertCommandFail("simon: get forest");

        // get with wrong target type: player
        this.assertCommandSucceed("joe: look");
        this.assertCommandSucceed("simon: look", "joe");

        this.assertCommandFail("simon: get joe");

        // get with wrong target type: character
        this.assertCommandSucceed("joe: goto forest");
        this.assertCommandSucceed("joe: get key");
        this.assertCommandSucceed("joe: goto cabin");
        this.assertCommandSucceed("joe: unlock with key");

        this.assertCommandSucceed("simon: look", "cellar");

        this.assertCommandSucceed("joe: goto cellar");
        this.assertCommandSucceed("joe: look", "elf");
        this.assertCommandSucceed("simon: goto cellar");
        this.assertCommandSucceed("simon: look", "joe");

        this.assertCommandFail("simon: get elf");
    }

    // TEST: get with an artefact from other place
    @Test
    void testArtefactFromOtherLocation() {
        // should not get key in other place
        this.assertCommandFail("simon: get key");
        this.assertCommandSucceed("simon: inv", "key", true);

        // should get key from right location
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: get key", "key");
        this.assertCommandSucceed("simon: inv", "key");
    }

    // TEST: get with multiple artefacts
    @Test
    void testMultipleArtefacts() {
        // before get
        this.assertCommandSucceed("simon: inv", "axe", true);

        // get multiple artefacts
        this.assertCommandFail("simon: get potion axe");

        // get artefact with other artefact in other location
        this.assertCommandSucceed("simon: get potion key", "potion");
        this.assertCommandSucceed("simon: inv", "potion");

        // get artefact with non-entity word
        this.assertCommandSucceed("simon: get axe abc", "axe");
        this.assertCommandSucceed("simon: inv", "axe");
    }

    // TEST: get artefact which is in other location
    @Test
    void testArtefactInOtherMap() {
        // before get
        this.assertCommandSucceed("simon: inv", "key", true);
        this.assertCommandSucceed("simon: look", "key", true);

        // get from wrong location
        this.assertCommandFail("simon: get key");

        // move to right location & get
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: get key", "key");
        this.assertCommandSucceed("simon: inv", "key");
    }

    // TEST: get artefact which is in storeroom
    @Test
    void testArtefactInStoreroom() {
        // cannot get artefact in storeroom
        this.assertCommandFail("simon: get log");

        // prepare log
        this.assertCommandSucceed("simon: get axe");
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: axe cutdown");

        // get log
        this.assertCommandSucceed("simon: get log", "log");
        this.assertCommandSucceed("simon: inv", "log");
    }

    // TEST: get artefact which is in own inventory
    @Test
    void testArtefactInOwnInventory() {
        this.assertCommandSucceed("simon: get potion", "potion");

        // should not get from inventory
        this.assertCommandFail("simon: get potion");

        // should not get from other's inv
        this.assertCommandFail("joe: get potion");
    }
}
