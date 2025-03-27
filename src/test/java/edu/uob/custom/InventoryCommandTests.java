package edu.uob;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InventoryCommandTests {
    // TEST: call inventory with "inventory", "InvEntOry", "inv", "InV"
    @Test
    void testBasicInventory() {
        String[] variants = {"inventory", "InvEntOry", "inv", "InV"};
        for (String variant : variants) {
            String response = sendCommandToServer(
                new StringBuilder().append("simon: ").append(variant).toString());
            assertTrue(response.contains("inventory"),
                "Inventory should works with 'inv' & 'inventory' and be case-insensitive");
        }

        String[] failedVariants = {"inventory1", "InvEntOryz", "inv0", "InVZ"};
        for (String variant : failedVariants) {
            String response = sendCommandToServer(
                new StringBuilder().append("simon: ").append(variant).toString());
            assertTrue(response.contains(this.FAIL_PREFIX),
                "Inventory should fail when call with wrong text");
        }
    }

    // TEST: call inventory with decorative words &  unexpected order
    @Test
    void testInventoryWithDecorativeWords() {
        String response = sendCommandToServer("simon: please show inventory");
        assertTrue(response.contains("inventory"),
            "Decorative words should work for inventory");
    }
}
