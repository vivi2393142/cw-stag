package edu.uob.custom;

import org.junit.jupiter.api.Test;

public class DropCommandTests extends BaseSTAGTests {
    // TEST: drop with valid cmd
    @Test
    void testBasicDrop() {
        String[] variants = {"drop", "DROP", "dRoP"};
        for (String variant : variants) {
            // get axe
            this.assertCommandSucceed("simon: get axe", "axe");
            this.assertCommandSucceed("simon: inv", "axe");

            // drop axe
            String cmd = new StringBuilder().append("simon: axe ").append(variant).toString();
            this.assertCommandSucceed(cmd, "axe");
            this.assertCommandSucceed("simon: look", "axe");
            this.assertCommandSucceed("simon: inv", "axe", true);
        }

        String[] failVariants = {"dropA", "DROP1", "dRoPz"};
        this.assertCommandSucceed("simon: get axe");
        for (String variant : failVariants) {
            String cmd = new StringBuilder()
                .append("simon: ").append(variant).append(" axe").toString();
            this.assertCommandFail(cmd);
        }
    }

    // TEST: drop with other players
    @Test
    void testDropWithMultiPlayer() {
        // get axe
        this.assertCommandSucceed("ryan: get axe"); // case-insensitive
        this.assertCommandSucceed("ryan: inv", "axe");
        this.assertCommandSucceed("bob: look", "axe", true);

        // drop axe
        this.assertCommandSucceed("ryan: DROP axe"); // case-insensitive
        this.assertCommandSucceed("bob: look", "axe");
        this.assertCommandSucceed("ryAn: inv", "axe", true);

        // other player get axe
        this.assertCommandSucceed("BOB: get axe", "axe"); // case-insensitive
        this.assertCommandSucceed("bob: inv", "axe");
        this.assertCommandSucceed("RYAN: look", "aex", true);
    }

    // TEST: drop with decorative words & unexpected order
    @Test
    void testDropWithDecorativeWords() {
        this.assertCommandSucceed("simon: get potion");
        this.assertCommandSucceed("simon: please drop the potion");
        this.assertCommandSucceed("simon: inv", "potion", true);
    }

    // TEST: drop without any artefact
    @Test
    void testDropWithoutArtefact() {
        this.assertCommandFail("simon: drop");
    }

    // TEST: drop with invalid artefact
    @Test
    void testDropWithInvalidArtefact() {
        this.assertCommandSucceed("simon: get potion");

        // drop with non-exist artefact
        this.assertCommandFail("simon: drop xyz");

        // drop with artefact in other location
        this.assertCommandFail("simon: drop key");

        // drop with artefact in storeroom
        this.assertCommandFail("simon: drop log");

        // get with wrong target type: location
        this.assertCommandFail("simon: drop forest");

        // drop with wrong target type: player
        this.assertCommandSucceed("alice: look");
        this.assertCommandSucceed("simon: look", "alice");

        this.assertCommandFail("simon: drop alice");

        // get with wrong target type: character
        this.assertCommandSucceed("alice wang: goto forest");
        this.assertCommandSucceed("alice wang: get key");
        this.assertCommandSucceed("alice wang: goto cabin");
        this.assertCommandSucceed("alice wang: unlock with key");

        this.assertCommandSucceed("simon: look", "cellar");

        this.assertCommandSucceed("alice wang: goto cellar");
        this.assertCommandSucceed("alice wang: look", "elf");
        this.assertCommandSucceed("simon: look", "alice wang", true);

        this.assertCommandFail("simon: drop elf");
    }

    // TEST: drop with multiple artefacts
    @Test
    void testDropMultipleArtefacts() {
        // before drop
        this.assertCommandSucceed("alex: get axe");
        this.assertCommandSucceed("alex: get potion");

        // drop multiple artefact
        this.assertCommandFail("simon: drop potion axe");

        // drop artefact with other artefact in other's inventory
        this.assertCommandSucceed("alex: inv", "potion");
        this.assertCommandSucceed("alex: inv", "axe");

        this.assertCommandSucceed("lee: goto forest");
        this.assertCommandSucceed("lee: get key");

        this.assertCommandSucceed("alex: drop potion key", "potion");
        this.assertCommandSucceed("alex: inv", "potion", true);

        // drop artefact with non-entity word
        this.assertCommandSucceed("alex: drop axe abc", "axe");
        this.assertCommandSucceed("alex: inv", "axe", true);
    }
}
