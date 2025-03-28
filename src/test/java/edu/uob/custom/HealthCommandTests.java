package edu.uob.custom;

import edu.uob.EntityType;
import edu.uob.GameAction;
import edu.uob.data.EntityData;
import edu.uob.data.InteractableEntityData;
import edu.uob.data.PathData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class HealthCommandTests extends BaseSTAGTests {
    // test basic health command functionality
    @Test
    void testBasicHealth() {
        this.assertCommandSucceed("simon: health", "3");
        this.assertCommandSucceed("simon: health status"); // extra words

        // invalid
        this.assertCommandFail("simon: healthcheck"); // invalid variation
        this.assertCommandFail("simon:"); // empty command
    }

    // test case insensitivity of health command
    @Test
    void testCaseInsensitivity() {
        this.assertCommandSucceed("simon: HEALTH", "3");
        this.assertCommandSucceed("simon: HeAlTh", "3");
    }

    // test health with multiple players
    @Test
    @Tag("extended")
    void testMultiPlayerHealth() {
        this.assertCommandSucceed("simon: health", "3");
        this.assertCommandSucceed("joe: health", "3");

        this.gotoCellar("simon");

        this.assertCommandSucceed("simon: fight elf");
        this.assertCommandSucceed("simon: health", "2");
        this.assertCommandSucceed("joe: health", "3");// independent health
    }

    // test health decrease via action (fight elf)
    @Test
    @Tag("extended")
    void testHealthDecrease() {
        this.gotoCellar("simon");
        this.assertCommandSucceed("simon: health", "3");
        this.assertCommandSucceed("simon: fight elf");
        this.assertCommandSucceed("simon: health", "2"); // health drops to 2
        this.assertCommandSucceed("simon: fight elf");
        this.assertCommandSucceed("simon: health", "1"); // health drops to 1
    }

    // test health reaching 0 and respawn
    @Test
    @Tag("extended")
    void testHealthToZero() {
        this.assertCommandSucceed("simon: get potion");
        this.gotoCellar("simon");
        this.assertCommandSucceed("simon: health", "3");
        this.assertCommandSucceed("simon: fight elf"); // health to 2
        this.assertCommandSucceed("simon: fight elf"); // health to 1
        this.assertCommandSucceed("simon: fight elf"); // health to 0, respawn

        this.assertCommandSucceed("simon: health", "3"); // health reset to 3
        this.assertCommandSucceed("simon: look", "cabin"); // back at start
        // inventory dropped
        this.assertCommandSucceed("simon: inv", "potion", true);
        // not in cellar
        this.assertCommandSucceed("simon: look", "in a log cabin in the woods");
        this.assertCommandSucceed("joe: goto cellar");
        // potion dropped in cellar
        this.assertCommandSucceed("joe: look", "potion");
    }

    // test health with decorative words
    @Test
    void testDecorativeWords() {
        this.assertCommandSucceed("simon: please check health", "3");
        this.assertCommandSucceed("simon: health now", "3");
    }

    // test health after picking up and dropping items
    @Test
    void testHealthWithInventory() {
        this.assertCommandSucceed("simon: get axe");
        this.assertCommandSucceed("simon: health", "3");
        this.assertCommandSucceed("simon: drop axe");
        this.assertCommandSucceed("simon: health", "3"); // no change
    }

    // test health with invalid player name
    @Test
    void testInvalidPlayerName() {
        this.assertCommandFail("123: health"); // invalid characters
        this.assertCommandSucceed("simon-smith: health", "3");
        this.assertCommandSucceed("o'malley: health", "3");
    }

    // test health after consuming health via action (drink potion)
    @Test
    @Tag("extended")
    void testHealthIncrease() {
        this.gotoCellar("simon");
        this.assertCommandSucceed("simon: fight elf"); // health to 2
        this.assertCommandSucceed("simon: health", "2");
        this.assertCommandSucceed("simon: goto cabin");
        this.assertCommandSucceed("simon: drink potion");
        this.assertCommandSucceed("simon: health", "3"); // health back to max
    }

    // test health at minimum (1) without death
    @Test
    @Tag("extended")
    void testHealthAtMinimum() {
        this.gotoCellar("simon");
        this.assertCommandSucceed("simon: fight elf"); // health to 2
        this.assertCommandSucceed("simon: fight elf"); // health to 1
        this.assertCommandSucceed("simon: health", "1");
        this.assertCommandSucceed("simon: look", "cellar"); // still in cellar
    }

    // test health after death with multiple items in inventory
    @Test
    @Tag("extended")
    void testDeathWithMultipleItems() {
        this.assertCommandSucceed("simon: get potion");
        this.assertCommandSucceed("simon: get axe");
        this.gotoCellar("simon");

        this.assertCommandSucceed("simon: fight elf"); // health to 2
        this.assertCommandSucceed("simon: fight elf"); // health to 1
        this.assertCommandSucceed("simon: fight elf"); // health to 0

        this.assertCommandSucceed("simon: health", "3"); // respawn
        this.assertCommandSucceed("simon: look", "cabin");
        this.assertCommandSucceed("simon: inv", "potion", true);
        this.assertCommandSucceed("simon: inv", "axe", true);

        this.assertCommandSucceed("joe: goto cellar");
        this.assertCommandSucceed("joe: look", "potion");
        this.assertCommandSucceed("joe: look", "axe");
    }

    // test health with custom starting location (not cabin)
    @Test
    void testCustomStartingLocation() {
        EntityData[] locations = {
            new EntityData("forest", "a deep dark forest"),
            new EntityData("cabin", "a log cabin in the woods"),
        };
        PathData[] paths = {
            new PathData("cabin", "forest"),
            new PathData("forest", "cabin"),
        };
        InteractableEntityData[] entities = {
            new InteractableEntityData("potion", "a bottle of magic potion",
                "cabin", EntityType.ARTEFACT),
            new InteractableEntityData("elf", "an angry looking elf",
                "cabin", EntityType.CHARACTER),
        };
        GameAction[] actions = {
            new GameAction(Set.of("drink"), Set.of("potion"),
                0, 1, Set.of("potion"), Set.of(),
                "you drink the potion and your health improves"),
            new GameAction(Set.of("fight", "hit", "attack"), Set.of("elf"),
                1, 0, Set.of(), Set.of(),
                "you attack the elf, but he fights back and you lose some health"),
        };

        String entityFileName = "custom-entities.dot";
        String actionFileName = "custom-actions.xml";
        TestUtils.makeEntitiesFile(locations, paths, entities, entityFileName);
        TestUtils.makeActionsFile(actions, actionFileName);
        this.resetServer(entityFileName, actionFileName);

        this.assertCommandSucceed("simon: look", "in a deep dark forest");
        this.assertCommandSucceed("simon: goto cabin");
        this.assertCommandSucceed("simon: look", "a log cabin in the woods");

        this.assertCommandFail("simon: fight fight"); // must with one subject
        this.assertCommandSucceed("simon: fight elf");
        this.assertCommandSucceed("simon: look", "a log cabin in the woods");
        this.assertCommandSucceed("simon: fight elf");
        this.assertCommandSucceed("simon: look", "a log cabin in the woods");
        this.assertCommandSucceed("simon: drink potion");
        this.assertCommandSucceed("simon: health", "2");

        this.assertCommandSucceed("simon: fight elf");
        this.assertCommandSucceed("simon: look", "a log cabin in the woods");
        this.assertCommandSucceed("simon: fight elf");
        this.assertCommandSucceed("simon: look", "in a deep dark forest");

        TestUtils.removeFile(entityFileName);
        TestUtils.removeFile(actionFileName);
    }

    // test health when no actions affect it (basic entities only)
    @Test
    void testNoHealthChange() {
        this.resetServer("basic-entities.dot", "basic-actions.xml");
        this.assertCommandSucceed("simon: health", "3");
        this.assertCommandSucceed("simon: get axe");
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: health", "3"); // no change
    }

    // test health after death with items already in starting location
    @Test
    @Tag("extended")
    void testDeathWithItemsInStart() {
        this.assertCommandSucceed("simon: get potion");
        this.assertCommandSucceed("simon: drop potion"); // potion in cabin
        this.gotoCellar("simon");

        this.assertCommandSucceed("simon: fight elf"); // health to 2
        this.assertCommandSucceed("simon: fight elf"); // health to 1
        this.assertCommandSucceed("simon: fight elf"); // health to 0
        this.assertCommandSucceed("simon: health", "3");
        this.assertCommandSucceed("simon: look", "cabin");
        // original potion still there
        this.assertCommandSucceed("simon: look", "potion");
    }

    // test health with max attempts to increase beyond 3
    @Test
    @Tag("extended")
    void testHealthMaxLimit() {
        this.assertCommandSucceed("simon: drink potion"); // health stays at 3
        // no increase beyond max
        this.assertCommandSucceed("simon: health", "3");

        this.gotoCellar("simon");
        this.assertCommandSucceed("simon: fight elf"); // health to 2
        this.assertCommandSucceed("simon: goto cabin");
        this.assertCommandFail("simon: drink potion"); // no more potion
        this.assertCommandSucceed("simon: health", "2");
        this.assertCommandFail("simon: drink potion"); // no potion left
    }
}
