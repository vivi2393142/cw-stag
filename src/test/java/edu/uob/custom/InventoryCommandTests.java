package edu.uob.custom;

import org.junit.jupiter.api.Test;

import java.util.Set;

public class InventoryCommandTests extends BaseSTAGTests {
    // call inventory with valid & invalid cmd
    @Test
    void testBasicInventory() {
        Set<String> variants = Set.of("inventory", "InvEntOry", "inv", "InV");
        for (String variant : variants) {
            String cmd = new StringBuilder().append("simon: ").append(variant).toString();
            this.assertCommandSucceed(cmd, "inventory");
        }

        Set<String> failedVariants = Set.of("inventory1", "InvEntOryz", "inv0", "InVZ");
        for (String variant : failedVariants) {
            String cmd = new StringBuilder().append("simon: ").append(variant).toString();
            this.assertCommandFail(cmd);
        }
    }

    // inventory with other players
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

    // call inventory with decorative words & unexpected order
    @Test
    void testWithDecorativeWords() {
        this.assertCommandSucceed("simon: please show inventory", "inventory");
    }
}
