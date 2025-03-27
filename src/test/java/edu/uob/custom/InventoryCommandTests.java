package edu.uob.custom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InventoryCommandTests extends BaseSTAGTests {
    // TEST: call inventory with valid & invalid cmd
    @Test
    void testBasicInventory() {
        String[] variants = {"inventory", "InvEntOry", "inv", "InV"};
        for (String variant : variants) {
            String cmd = new StringBuilder().append("simon: ").append(variant).toString();
            this.assertCommandSucceed(cmd, "inventory");
        }

        String[] failedVariants = {"inventory1", "InvEntOryz", "inv0", "InVZ"};
        for (String variant : failedVariants) {
            String cmd = new StringBuilder().append("simon: ").append(variant).toString();
            this.assertCommandFail(cmd);
        }
    }

    // TEST: inventory with other players
    @Test
    void testWithMultiPlayer() {
        // begging
        this.assertCommandSucceed("Lily: inv", "nothing");

        // get axe
        this.assertCommandSucceed("Lily: get axe");
        this.assertCommandSucceed("Lily: inv", "axe");

        this.assertCommandSucceed(" liLY : inv", "axe");

        this.assertCommandSucceed("John : inv", "axe", true);
        this.assertCommandSucceed("John : inv", "nothing");
    }

    // TEST: call inventory with decorative words &  unexpected order
    @Test
    void testWithDecorativeWords() {
        this.assertCommandSucceed("simon: please show inventory", "inventory");
    }
}
