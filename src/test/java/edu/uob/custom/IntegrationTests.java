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
        // start in cabin, explore and interact randomly
        this.assertCommandSucceed("simon: look", "in a log cabin");
        this.assertCommandSucceed("simon: look", "axe");
        this.assertCommandSucceed("simon: get axe");
        this.assertCommandSucceed("simon: inv", "axe");
        this.assertCommandSucceed("simon: look", "axe", true);
        this.assertCommandSucceed("simon: goto forest", "in a dark forest");

        this.assertCommandSucceed("simon: look", "in a dark forest");
        this.assertCommandSucceed("simon: look", "key");
        this.assertCommandSucceed("simon: get key");
        this.assertCommandSucceed("simon: inv", "key");
        this.assertCommandSucceed("simon: look", "key", true);
        this.assertCommandSucceed("simon: goto cabin", "in a log cabin");

        this.assertCommandSucceed("simon: look", "in a log cabin");
        this.assertCommandSucceed("simon: look", "potion");
        this.assertCommandSucceed("simon: get potion");
        this.assertCommandSucceed("simon: inv", "potion");
        this.assertCommandSucceed("simon: look", "potion", true);
        this.assertCommandSucceed("simon: look", "cellar", true);
        this.assertCommandSucceed("simon: unlock with key");
        this.assertCommandSucceed("simon: look", "cellar");
        this.assertCommandSucceed("simon: goto cellar", "in a dusty cellar");

        this.assertCommandSucceed("simon: look", "in a dusty cellar");
        this.assertCommandSucceed("simon: look", "elf");
        this.assertCommandSucceed("simon: look", "axe", true);
        this.assertCommandSucceed("simon: drop axe"); // random drop
        this.assertCommandSucceed("simon: look", "axe");
        this.assertCommandSucceed("simon: inventory", "axe", true);
        this.assertCommandSucceed("simon: goto cabin", "in a log cabin");

        this.assertCommandFail("simon: get axe"); // axe left in cellar
        this.assertCommandSucceed("simon: inv", "potion"); // still has potion
    }

    // a multi-player chaotic playthrough with extended actions
    @Test
    @Tag("extended")
    void testMultiPlayerChaosExtended() {
        // Simon starts exploring and fighting
        this.assertCommandSucceed("simon: look", "in a log cabin");
        this.assertCommandSucceed("simon: look", "axe");
        this.assertCommandSucceed("simon: get axe", "axe");
        this.assertCommandSucceed("simon: look", "axe", true);
        this.assertCommandSucceed("simon: inv", "axe");
        this.assertCommandSucceed("simon: goto forest", "in a deep dark forest");

        this.assertCommandSucceed("simon: look", "in a deep dark forest");
        this.assertCommandSucceed("simon: look", "key");
        this.assertCommandSucceed("simon: get key", "key");
        this.assertCommandSucceed("simon: inv", "key");
        this.assertCommandSucceed("simon: look", "key", true);

        this.assertCommandSucceed("simon: cut tree"); // tree consumed, log produced
        this.assertCommandSucceed("simon: look", "tree", true);
        this.assertCommandSucceed("simon: look", "log");
        this.assertCommandSucceed("simon: get log");
        this.assertCommandSucceed("simon: inv", "log");
        this.assertCommandSucceed("simon: goto riverbank", "in a grassy riverbank");

        this.assertCommandSucceed("simon: bridge on river"); // clearing produced
        this.assertCommandSucceed("simon: look", "clearing");
        this.assertCommandSucceed("simon: goto clearing", "in a clearing");
        this.assertCommandSucceed("simon: drop key"); // random drop
        this.assertCommandSucceed("simon: look", "key");
        this.assertCommandSucceed("simon: health", "3");

        // Joe joins and messes around
        this.assertCommandSucceed("joe: look", "in a log cabin");
        this.assertCommandSucceed("joe: get potion");
        this.assertCommandSucceed("joe: look", "potion", true);
        this.assertCommandSucceed("joe: inv", "potion");
        this.assertCommandSucceed("joe: goto forest", "in a deep dark forest");

        this.assertCommandSucceed("joe: drink potion"); // health stays at 3
        this.assertCommandSucceed("joe: health", "3");
        this.assertCommandSucceed("joe: goto cabin", "in a log cabin");

        this.assertCommandSucceed("joe: goto forest", "in a deep dark forest");
        this.assertCommandSucceed("joe: goto riverbank", "in a grassy riverbank");
        this.assertCommandSucceed("joe: goto clearing", "in a clearing");
        this.assertCommandSucceed("joe: get key");
        this.assertCommandSucceed("joe: inv", "key");
        this.assertCommandSucceed("joe: goto riverbank", "in a grassy riverbank");
        this.assertCommandSucceed("joe: goto forest", "in a deep dark forest");
        this.assertCommandSucceed("joe: goto cabin", "in a log cabin");

        this.assertCommandSucceed("joe: get coin");
        this.assertCommandSucceed("joe: unlock with key", "cellar");
        this.assertCommandSucceed("joe: look", "cellar");
        this.assertCommandSucceed("joe: goto cellar", "in a dusty cellar");

        this.assertCommandSucceed("joe: fight elf"); // health to 2
        this.assertCommandSucceed("joe: health", "2");
        this.assertCommandSucceed("joe: pay elf"); // shovel produced
        this.assertCommandSucceed("joe: look", "shovel");
        this.assertCommandSucceed("joe: get shovel");
        this.assertCommandSucceed("joe: inv", "shovel");
        this.assertCommandSucceed("joe: look", "axe", true);

        // Simon continues, dies and respawns
        this.assertCommandSucceed("simon: goto riverbank", "in a grassy riverbank");
        this.assertCommandSucceed("simon: goto forest", "in a deep dark forest");
        this.assertCommandSucceed("simon: goto cabin", "in a log cabin");
        this.assertCommandSucceed("simon: goto cellar", "in a dusty cellar");
        this.assertCommandSucceed("simon: look", "joe");
        this.assertCommandSucceed("simon: fight elf"); // health to 2
        this.assertCommandSucceed("simon: fight elf"); // health to 1
        this.assertCommandSucceed("simon: fight elf"); // health to 0, respawn
        this.assertCommandSucceed("simon: health", "3");
        this.assertCommandSucceed("simon: look", "cabin");

        // axe dropped in cellar
        this.assertCommandSucceed("joe: look", "axe");
        this.assertCommandSucceed("simon: inv", "axe", true);

        // Joe digs for gold
        this.assertCommandSucceed("joe: goto cabin", "in a log cabin");
        this.assertCommandSucceed("joe: goto forest", "in a deep dark forest");
        this.assertCommandSucceed("joe: goto riverbank", "in a grassy riverbank");
        this.assertCommandSucceed("joe: goto clearing", "in a clearing");
        this.assertCommandSucceed("joe: dig ground"); // gold produced
        this.assertCommandSucceed("joe: look", "gold");
        this.assertCommandSucceed("joe: get gold");
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
        this.assertCommandSucceed("simon: look", "torch", true);
        this.assertCommandSucceed("simon: inv", "torch");
        this.assertCommandSucceed("simon: goto tunnel");

        this.assertCommandSucceed("simon: look", "in a narrow winding tunnel");
        this.assertCommandSucceed("simon: look", "vault", true);
        this.assertCommandSucceed("simon: unlock lock"); // vault produced
        this.assertCommandSucceed("simon: look", "vault");
        this.assertCommandSucceed("simon: goto vault");

        this.assertCommandSucceed("simon: look", "in a hidden treasure vault");
        this.assertCommandSucceed("simon: look", "guard");
        this.assertCommandSucceed("simon: look", "ruby", true);
        this.assertCommandSucceed("simon: mine beside guard"); // ruby produced
        this.assertCommandSucceed("simon: look", "ruby");
        this.assertCommandSucceed("simon: get ruby");
        this.assertCommandSucceed("simon: look", "ruby", true);
        this.assertCommandSucceed("simon: inv", "ruby");
        this.assertCommandSucceed("simon: drop ruby");
        this.assertCommandSucceed("simon: look", "ruby");
        this.assertCommandSucceed("simon: inv", "ruby", true);
        this.assertCommandSucceed("simon: goto tunnel");
        this.assertCommandSucceed("simon: goto cave");
        // torch consumed
        this.assertCommandSucceed("simon: look", "torch", true);

        // Joe joins and continues the chaos
        this.assertCommandSucceed("joe: look", "cave");
        this.assertCommandSucceed("joe: goto tunnel");
        this.assertCommandFail("joe: unlock lock"); // torch gone
        this.assertCommandSucceed("joe: goto vault");
        this.assertCommandSucceed("joe: get ruby");
        this.assertCommandSucceed("joe: look", "ruby", true);
        this.assertCommandSucceed("joe: inv", "ruby");
        this.assertCommandSucceed("joe: bribe guard"); // Guard consumed
        this.assertCommandSucceed("joe: look", "guard", true);
        this.assertCommandSucceed("joe: inv", "ruby", true);

        // cleanup
        TestUtils.removeFile(entityFileName);
        TestUtils.removeFile(actionFileName);
    }

    // extreme edge cases with extended actions
    @Test
    @Tag("extended")
    void testEdgeCaseMadness() {
        // Simon does everything at once
        this.assertCommandSucceed("simon: get axe");
        this.assertCommandSucceed("simon: get potion");
        this.assertCommandSucceed("simon: get coin");
        this.assertCommandSucceed("simon: goto forest");

        this.assertCommandSucceed("simon: get key");
        this.assertCommandSucceed("simon: cut tree");
        this.assertCommandSucceed("simon: get log");
        this.assertCommandSucceed("simon: goto riverbank");

        this.assertCommandSucceed("simon: bridge on river");
        this.assertCommandSucceed("simon: goto clearing");
        this.assertCommandFail("simon: goto cabin"); // no path, stays in clearing

        this.assertCommandSucceed("simon: look", "clearing");
        this.assertCommandSucceed("simon: goto riverbank");
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: goto cabin");
        this.assertCommandSucceed("simon: unlock trapdoor");

        // Joe joins, steals and dies
        this.assertCommandSucceed("joe: goto forest");
        this.assertCommandSucceed("joe: goto riverbank");
        this.assertCommandSucceed("joe: get horn");
        this.assertCommandSucceed("joe: blow horn"); // lumberjack produced
        this.assertCommandSucceed("joe: look", "a burly wood cutter");

        this.assertCommandSucceed("joe: goto forest");
        this.assertCommandSucceed("joe: goto cabin");
        this.assertCommandSucceed("joe: goto cellar");

        this.assertCommandSucceed("joe: fight elf"); // Health to 2
        this.assertCommandSucceed("joe: fight elf"); // Health to 1
        this.assertCommandSucceed("joe: fight elf"); // Health to 0
        this.assertCommandSucceed("joe: health", "3");
        this.assertCommandSucceed("joe: look", "cabin");
        this.assertCommandSucceed("joe: inv", "horn", true);

        // Simon picks up where Joe left off
        this.assertCommandSucceed("simon: goto cellar");
        this.assertCommandSucceed("simon: pay elf"); // shovel produced
        this.assertCommandSucceed("simon: get shovel");

        this.assertCommandSucceed("simon: goto cabin");
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: goto riverbank");
        this.assertCommandSucceed("simon: goto clearing");
        this.assertCommandSucceed("simon: dig ground"); // gold produced
        this.assertCommandSucceed("simon: get gold");
        this.assertCommandFail("simon: drop everything please"); // invalid
        this.assertCommandSucceed("simon: inv", "axe");
        this.assertCommandSucceed("simon: inv", "gold");
        this.assertCommandSucceed("simon: look", "gold", true);

        // third player causes chaos
        this.assertCommandSucceed("alice: goto cellar");
        this.assertCommandSucceed("alice: look", "horn"); // Joe's dropped horn
        this.assertCommandSucceed("alice: get horn");
        this.assertCommandFail("alice: blow horn"); // no new lumberjack
        this.assertCommandSucceed("alice: look", "lumberjack", true);
    }

    // invalid and nonsense inputs
    @Test
    void testNonsenseInputs() {
        this.assertCommandFail("simon: gibberish");
        this.assertCommandFail("simon: goto nowhere");
        this.assertCommandFail("simon: get elf");
        this.assertCommandFail("simon: throw axe potion key forest");
        this.assertCommandSucceed("simon: look", "cabin"); // state unchanged
        this.assertCommandFail("123: health"); // invalid player name
        this.assertCommandFail("simon: "); // empty command
    }
}
