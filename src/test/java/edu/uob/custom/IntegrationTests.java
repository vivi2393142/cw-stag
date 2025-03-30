package edu.uob.custom;

import edu.uob.EntityType;
import edu.uob.GameAction;
import edu.uob.data.EntityData;
import edu.uob.data.InteractableEntityData;
import edu.uob.data.PathData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class IntegrationTests extends BaseSTAGTests {
    // a chaotic single-player playthrough with basic entities and actions
    @Test
    void testSinglePlayerChaosBasic() {
        // Start in cabin, explore and interact randomly
        this.assertCommandSucceed("simon: look", "in a log cabin");
        this.assertCommandSucceed("simon: look", "axe");
        this.assertCommandSucceed("simon: get axe");
        this.assertCommandSucceed("simon: inv", "axe"); // Confirm axe is picked up
        this.assertCommandSucceed("simon: look", "axe", true); // Axe no longer in room
        this.assertCommandSucceed("simon: goto forest", "in a dark forest");
        this.assertCommandSucceed("simon: look", "in a dark forest"); // Confirm location

        this.assertCommandSucceed("simon: look", "key");
        this.assertCommandSucceed("simon: get key");
        this.assertCommandSucceed("simon: inv", "key"); // Confirm key is picked up
        this.assertCommandSucceed("simon: look", "key", true); // Key no longer in room
        this.assertCommandSucceed("simon: goto cabin", "in a log cabin");
        this.assertCommandSucceed("simon: look", "in a log cabin"); // Confirm location

        this.assertCommandSucceed("simon: look", "potion");
        this.assertCommandSucceed("simon: get potion");
        this.assertCommandSucceed("simon: inv", "potion"); // Confirm potion is picked up
        this.assertCommandSucceed("simon: look", "potion", true); // Potion no longer in room
        this.assertCommandSucceed("simon: look", "cellar", true); // Cellar not visible yet
        this.assertCommandSucceed("simon: unlock with key");
        this.assertCommandSucceed("simon: look", "cellar"); // Cellar now visible
        this.assertCommandSucceed("simon: goto cellar", "in a dusty cellar");
        this.assertCommandSucceed("simon: look", "in a dusty cellar"); // Confirm location

        this.assertCommandSucceed("simon: look", "elf");
        this.assertCommandSucceed("simon: look", "axe", true); // Axe still in inventory
        this.assertCommandSucceed("simon: drop axe"); // Random drop
        this.assertCommandSucceed("simon: look", "axe"); // Axe now in room
        this.assertCommandSucceed("simon: inv", "axe", true); // Axe no longer in inventory
        this.assertCommandSucceed("simon: goto cabin", "in a log cabin");
        this.assertCommandSucceed("simon: look", "in a log cabin"); // Confirm location

        this.assertCommandFail("simon: get axe"); // Axe left in cellar
        this.assertCommandSucceed("simon: inv", "potion"); // Still has potion
        this.assertCommandSucceed("simon: inv", "axe", true); // Confirm axe not in inventory
    }

    // a multi-player chaotic playthrough with extended actions
    @Test
    @Tag("extended")
    void testMultiPlayerChaosExtended() {
        // Simon starts exploring and fighting
        this.assertCommandSucceed("simon: look", "in a log cabin");
        this.assertCommandSucceed("simon: look", "axe");
        this.assertCommandSucceed("simon: get axe", "axe");
        this.assertCommandSucceed("simon: inv", "axe"); // Confirm axe picked up
        this.assertCommandSucceed("simon: look", "axe", true); // Axe not in room
        this.assertCommandSucceed("simon: goto forest", "in a deep dark forest");
        this.assertCommandSucceed("simon: look", "in a deep dark forest"); // Confirm location

        this.assertCommandSucceed("simon: look", "key");
        this.assertCommandSucceed("simon: get key", "key");
        this.assertCommandSucceed("simon: inv", "key"); // Confirm key picked up
        this.assertCommandSucceed("simon: look", "key", true); // Key not in room
        this.assertCommandSucceed("simon: cut tree"); // Tree consumed, log produced
        this.assertCommandSucceed("simon: look", "tree", true); // Tree gone
        this.assertCommandSucceed("simon: look", "log"); // Log now visible
        this.assertCommandSucceed("simon: get log");
        this.assertCommandSucceed("simon: inv", "log"); // Confirm log picked up
        this.assertCommandSucceed("simon: look", "log", true); // Log not in room
        this.assertCommandSucceed("simon: goto riverbank", "in a grassy riverbank");
        this.assertCommandSucceed("simon: look", "in a grassy riverbank"); // Confirm location

        this.assertCommandSucceed("simon: bridge on river"); // Clearing produced
        this.assertCommandSucceed("simon: look", "clearing"); // Clearing now visible
        this.assertCommandSucceed("simon: goto clearing", "in a clearing");
        this.assertCommandSucceed("simon: look", "in a clearing"); // Confirm location
        this.assertCommandSucceed("simon: drop key"); // Random drop
        this.assertCommandSucceed("simon: look", "key"); // Key in room
        this.assertCommandSucceed("simon: inv", "key", true); // Key not in inventory
        this.assertCommandSucceed("simon: health", "3");

        // Joe joins and messes around
        this.assertCommandSucceed("joe: look", "in a log cabin");
        this.assertCommandSucceed("joe: get potion");
        this.assertCommandSucceed("joe: inv", "potion"); // Confirm potion picked up
        this.assertCommandSucceed("joe: look", "potion", true); // Potion not in room
        this.assertCommandSucceed("joe: goto forest", "in a deep dark forest");
        this.assertCommandSucceed("joe: look", "in a deep dark forest"); // Confirm location

        this.assertCommandSucceed("joe: drink potion"); // Health stays at 3
        this.assertCommandSucceed("joe: inv", "potion", true); // Potion consumed
        this.assertCommandSucceed("joe: health", "3");
        this.assertCommandSucceed("joe: goto cabin", "in a log cabin");
        this.assertCommandSucceed("joe: look", "in a log cabin"); // Confirm location

        this.assertCommandSucceed("joe: goto forest", "in a deep dark forest");
        this.assertCommandSucceed("joe: goto riverbank", "in a grassy riverbank");
        this.assertCommandSucceed("joe: goto clearing", "in a clearing");
        this.assertCommandSucceed("joe: look", "in a clearing"); // Confirm location
        this.assertCommandSucceed("joe: get key");
        this.assertCommandSucceed("joe: inv", "key"); // Confirm key picked up
        this.assertCommandSucceed("joe: look", "key", true); // Key not in room
        this.assertCommandSucceed("joe: goto riverbank", "in a grassy riverbank");
        this.assertCommandSucceed("joe: goto forest", "in a deep dark forest");
        this.assertCommandSucceed("joe: goto cabin", "in a log cabin");
        this.assertCommandSucceed("joe: look", "in a log cabin"); // Confirm location

        this.assertCommandSucceed("joe: get coin");
        this.assertCommandSucceed("joe: inv", "coin"); // Confirm coin picked up
        this.assertCommandSucceed("joe: unlock with key", "cellar");
        this.assertCommandSucceed("joe: look", "cellar"); // Cellar visible
        this.assertCommandSucceed("joe: goto cellar", "in a dusty cellar");
        this.assertCommandSucceed("joe: look", "in a dusty cellar"); // Confirm location

        this.assertCommandSucceed("joe: fight elf"); // Health to 2
        this.assertCommandSucceed("joe: health", "2");
        this.assertCommandSucceed("joe: pay elf"); // Shovel produced
        this.assertCommandSucceed("joe: look", "shovel"); // Shovel visible
        this.assertCommandSucceed("joe: get shovel");
        this.assertCommandSucceed("joe: inv", "shovel"); // Confirm shovel picked up
        this.assertCommandSucceed("joe: look", "shovel", true); // Shovel not in room
        this.assertCommandSucceed("joe: look", "axe", true); // Axe not here

        // Simon continues, dies and respawns
        this.assertCommandSucceed("simon: goto riverbank", "in a grassy riverbank");
        this.assertCommandSucceed("simon: goto forest", "in a deep dark forest");
        this.assertCommandSucceed("simon: goto cabin", "in a log cabin");
        this.assertCommandSucceed("simon: goto cellar", "in a dusty cellar");
        this.assertCommandSucceed("simon: look", "in a dusty cellar"); // Confirm location
        this.assertCommandSucceed("simon: look", "joe"); // Joe visible
        this.assertCommandSucceed("simon: fight elf"); // Health to 2
        this.assertCommandSucceed("simon: health", "2");
        this.assertCommandSucceed("simon: fight elf"); // Health to 1
        this.assertCommandSucceed("simon: health", "1");
        this.assertCommandSucceed("simon: fight elf"); // Health to 0, respawn
        this.assertCommandSucceed("simon: health", "3");
        this.assertCommandSucceed("simon: look", "in a log cabin"); // Confirm respawn

        // Axe dropped in cellar
        this.assertCommandSucceed("joe: look", "axe"); // Joe sees axe
        this.assertCommandSucceed("simon: inv", "axe", true); // Simon lost axe

        // Joe digs for gold
        this.assertCommandSucceed("joe: goto cabin", "in a log cabin");
        this.assertCommandSucceed("joe: goto forest", "in a deep dark forest");
        this.assertCommandSucceed("joe: goto riverbank", "in a grassy riverbank");
        this.assertCommandSucceed("joe: goto clearing", "in a clearing");
        this.assertCommandSucceed("joe: look", "in a clearing"); // Confirm location
        this.assertCommandSucceed("joe: dig ground"); // Gold produced
        this.assertCommandSucceed("joe: look", "gold"); // Gold visible
        this.assertCommandSucceed("joe: get gold");
        this.assertCommandSucceed("joe: inv", "gold"); // Confirm gold picked up
        this.assertCommandSucceed("joe: look", "gold", true); // Gold not in room
    }

    // a custom file with non-standard starting location and chaotic play
    @Test
    void testCustomFileChaos() {
        String entityFileName = "custom-chaos-entities.dot";
        EntityData startLocation =
            new EntityData("cave", "a dark mysterious cave");
        Set<EntityData> locations = Set.of(
            startLocation, // cave as start
            new EntityData("tunnel", "a narrow winding tunnel"),
            new EntityData("vault", "a hidden treasure vault"),
            new EntityData("storeroom", "storage for any unused entities")
        );
        Set<PathData> paths = Set.of(
            new PathData("cave", "tunnel"),
            new PathData("tunnel", "cave"),
            new PathData("vault", "tunnel")
        );
        Set<InteractableEntityData> entities = Set.of(
            new InteractableEntityData("torch", "a flaming torch",
                "cave", EntityType.ARTEFACT),
            new InteractableEntityData("lock", "a rusty old lock",
                "tunnel", EntityType.FURNITURE),
            new InteractableEntityData("guard", "a stern vault guard",
                "vault", EntityType.CHARACTER),
            new InteractableEntityData("ruby", "a sparkling red ruby",
                "storeroom", EntityType.ARTEFACT)
        );
        Set<GameAction> actions = Set.of(
            new GameAction(Set.of("unlock"), Set.of("lock", "torch"),
                0, 0, Set.of("torch"), Set.of("vault"),
                "You use the torch to melt the lock, revealing a vault"),
            new GameAction(Set.of("bribe"), Set.of("guard", "ruby"),
                0, 0, Set.of("ruby", "guard"), Set.of(),
                "You bribe the guard with the ruby and he vanishes"),
            new GameAction(Set.of("mine"), Set.of("guard"),
                0, 0, Set.of(), Set.of("ruby"),
                "You mine the vault and find a ruby")
        );

        String actionFileName = "custom-chaos-actions.xml";
        TestUtils.makeEntitiesFile(startLocation, locations, paths, entities, entityFileName);
        TestUtils.makeActionsFile(actions, actionFileName);
        this.resetServer(entityFileName, actionFileName);

        // Simon explores and interacts chaotically
        this.assertCommandSucceed("simon: look", "in a dark mysterious cave");
        this.assertCommandSucceed("simon: look", "torch");
        this.assertCommandSucceed("simon: get torch");
        this.assertCommandSucceed("simon: inv", "torch"); // Confirm torch picked up
        this.assertCommandSucceed("simon: look", "torch", true); // Torch not in room
        this.assertCommandSucceed("simon: goto tunnel");
        this.assertCommandSucceed("simon: look", "in a narrow winding tunnel"); // Confirm location

        this.assertCommandSucceed("simon: look", "vault", true); // Vault not visible yet
        this.assertCommandSucceed("simon: unlock lock"); // Vault produced
        this.assertCommandSucceed("simon: look", "vault"); // Vault now visible
        this.assertCommandSucceed("simon: goto vault");
        this.assertCommandSucceed("simon: look", "in a hidden treasure vault"); // Confirm location

        this.assertCommandSucceed("simon: look", "guard");
        this.assertCommandSucceed("simon: look", "ruby", true); // Ruby not here yet
        this.assertCommandSucceed("simon: mine beside guard"); // Ruby produced
        this.assertCommandSucceed("simon: look", "ruby"); // Ruby visible
        this.assertCommandSucceed("simon: get ruby");
        this.assertCommandSucceed("simon: inv", "ruby"); // Confirm ruby picked up
        this.assertCommandSucceed("simon: look", "ruby", true); // Ruby not in room
        this.assertCommandSucceed("simon: drop ruby"); // Random drop
        this.assertCommandSucceed("simon: look", "ruby"); // Ruby in room
        this.assertCommandSucceed("simon: inv", "ruby", true); // Ruby not in inventory
        this.assertCommandSucceed("simon: goto tunnel");
        this.assertCommandSucceed("simon: look", "in a narrow winding tunnel"); // Confirm location
        this.assertCommandSucceed("simon: goto cave");
        this.assertCommandSucceed("simon: look", "in a dark mysterious cave"); // Confirm location
        this.assertCommandSucceed("simon: look", "torch", true); // Torch consumed

        // Joe joins and continues the chaos
        this.assertCommandSucceed("joe: look", "in a dark mysterious cave");
        this.assertCommandSucceed("joe: goto tunnel");
        this.assertCommandSucceed("joe: look", "in a narrow winding tunnel"); // Confirm location
        this.assertCommandFail("joe: unlock lock"); // Torch gone
        this.assertCommandSucceed("joe: goto vault");
        this.assertCommandSucceed("joe: look", "in a hidden treasure vault"); // Confirm location
        this.assertCommandSucceed("joe: get ruby");
        this.assertCommandSucceed("joe: inv", "ruby"); // Confirm ruby picked up
        this.assertCommandSucceed("joe: look", "ruby", true); // Ruby not in room
        this.assertCommandSucceed("joe: bribe guard"); // Guard consumed
        this.assertCommandSucceed("joe: look", "guard", true); // Guard gone
        this.assertCommandSucceed("joe: inv", "ruby", true); // Ruby consumed

        // Cleanup
        TestUtils.removeFile(entityFileName);
        TestUtils.removeFile(actionFileName);
    }

    // Extreme edge cases with extended actions
    @Test
    @Tag("extended")
    void testEdgeCaseMadness() {
        // Simon does everything at once
        this.assertCommandSucceed("simon: get axe");
        this.assertCommandSucceed("simon: inv", "axe"); // Confirm axe picked up
        this.assertCommandSucceed("simon: get potion");
        this.assertCommandSucceed("simon: inv", "potion"); // Confirm potion picked up
        this.assertCommandSucceed("simon: get coin");
        this.assertCommandSucceed("simon: inv", "coin"); // Confirm coin picked up
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: look", "in a deep dark forest"); // Confirm location

        this.assertCommandSucceed("simon: get key");
        this.assertCommandSucceed("simon: inv", "key"); // Confirm key picked up
        this.assertCommandSucceed("simon: cut tree");
        this.assertCommandSucceed("simon: look", "tree", true); // Tree gone
        this.assertCommandSucceed("simon: look", "log"); // Log produced
        this.assertCommandSucceed("simon: get log");
        this.assertCommandSucceed("simon: inv", "log"); // Confirm log picked up
        this.assertCommandSucceed("simon: goto riverbank");
        this.assertCommandSucceed("simon: look", "in a grassy riverbank"); // Confirm location

        this.assertCommandSucceed("simon: bridge on river");
        this.assertCommandSucceed("simon: look", "clearing"); // Clearing produced
        this.assertCommandSucceed("simon: goto clearing");
        this.assertCommandSucceed("simon: look", "in a clearing"); // Confirm location
        this.assertCommandFail("simon: goto cabin"); // No path, stays in clearing
        this.assertCommandSucceed("simon: look", "in a clearing"); // Confirm still in clearing

        this.assertCommandSucceed("simon: goto riverbank");
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: goto cabin");
        this.assertCommandSucceed("simon: look", "in a log cabin"); // Confirm location
        this.assertCommandSucceed("simon: unlock trapdoor");

        // Joe joins, steals and dies
        this.assertCommandSucceed("joe: goto forest");
        this.assertCommandSucceed("joe: look", "in a deep dark forest"); // Confirm location
        this.assertCommandSucceed("joe: goto riverbank");
        this.assertCommandSucceed("joe: get horn");
        this.assertCommandSucceed("joe: inv", "horn"); // Confirm horn picked up
        this.assertCommandSucceed("joe: blow horn"); // Lumberjack produced
        this.assertCommandSucceed("joe: look", "a burly wood cutter"); // Lumberjack visible
        this.assertCommandSucceed("joe: goto forest");
        this.assertCommandSucceed("joe: goto cabin");
        this.assertCommandSucceed("joe: goto cellar");
        this.assertCommandSucceed("joe: look", "in a dusty cellar"); // Confirm location

        this.assertCommandSucceed("joe: fight elf"); // Health to 2
        this.assertCommandSucceed("joe: health", "2");
        this.assertCommandSucceed("joe: fight elf"); // Health to 1
        this.assertCommandSucceed("joe: health", "1");
        this.assertCommandSucceed("joe: fight elf"); // Health to 0
        this.assertCommandSucceed("joe: health", "3");
        this.assertCommandSucceed("joe: look", "in a log cabin"); // Confirm respawn
        this.assertCommandSucceed("joe: inv", "horn", true); // Horn dropped

        // Simon picks up where Joe left off
        this.assertCommandSucceed("simon: goto cellar");
        this.assertCommandSucceed("simon: look", "in a dusty cellar"); // Confirm location
        this.assertCommandSucceed("simon: pay elf"); // Shovel produced
        this.assertCommandSucceed("simon: look", "shovel"); // Shovel visible
        this.assertCommandSucceed("simon: get shovel");
        this.assertCommandSucceed("simon: inv", "shovel"); // Confirm shovel picked up

        this.assertCommandSucceed("simon: goto cabin");
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: goto riverbank");
        this.assertCommandSucceed("simon: goto clearing");
        this.assertCommandSucceed("simon: look", "in a clearing"); // Confirm location
        this.assertCommandSucceed("simon: dig ground"); // Gold produced
        this.assertCommandSucceed("simon: look", "gold"); // Gold visible
        this.assertCommandSucceed("simon: get gold");
        this.assertCommandSucceed("simon: inv", "gold"); // Confirm gold picked up
        this.assertCommandFail("simon: drop everything please"); // Invalid
        this.assertCommandSucceed("simon: inv", "axe"); // Still has axe
        this.assertCommandSucceed("simon: inv", "gold"); // Still has gold
        this.assertCommandSucceed("simon: look", "gold", true); // Gold not in room

        // Third player causes chaos
        this.assertCommandSucceed("alice: goto cellar");
        this.assertCommandSucceed("alice: look", "in a dusty cellar"); // Confirm location
        this.assertCommandSucceed("alice: look", "horn"); // Joe's dropped horn
        this.assertCommandSucceed("alice: get horn");
        this.assertCommandSucceed("alice: inv", "horn"); // Confirm horn picked up
        this.assertCommandFail("alice: blow horn"); // No new lumberjack
        this.assertCommandSucceed("alice: look", "lumberjack", true); // No new lumberjack
        this.assertCommandSucceed("alice: inv", "horn"); // Horn still in inventory
    }

    // Invalid and nonsense inputs
    @Test
    void testNonsenseInputs() {
        this.assertCommandFail("simon: gibberish");
        this.assertCommandFail("simon: goto nowhere");
        this.assertCommandFail("simon: get elf");
        this.assertCommandFail("simon: throw axe potion key forest");
        this.assertCommandSucceed("simon: look", "in a log cabin"); // State unchanged
        this.assertCommandFail("123: health"); // Invalid player name
        this.assertCommandFail("simon: "); // Empty command
        this.assertCommandSucceed("simon: look", "in a log cabin"); // State still unchanged
    }
}