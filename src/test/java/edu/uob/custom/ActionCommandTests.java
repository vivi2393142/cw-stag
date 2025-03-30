package edu.uob.custom;

import edu.uob.GameAction;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ActionCommandTests extends BaseSTAGTests {
    // basic trigger, trigger in two word, invalid trigger
    @Test
    @Tag("extended")
    void testTrigger() {
        // all valid triggers
        Set<String> validTriggers = Set.of("chop", "cut", "cut down");
        for (String trigger : validTriggers) {
            this.resetServer(true);
            this.prepareCut("yang-yang");

            String cmd = new StringBuilder()
                .append("yang-yang: ").append(trigger).append(" tree").toString();
            this.assertCommandSucceed(cmd);
        }

        // invalid trigger
        Set<String> invalidTriggers = Set.of("cutting", "hit", "cutdown");
        for (String invalidTrigger : invalidTriggers) {
            this.resetServer(true);
            this.prepareCut("yang-yang");

            String cmd = new StringBuilder()
                .append("yang-yang: ").append(invalidTrigger).append(" tree").toString();
            this.assertCommandFail(cmd);
        }
    }

    // subjects mentioned in command are more than that in action
    @Test
    void testMoreSubjectsThanAction() {
        // prepare cut
        this.prepareCut("yang-yang");
        // cut with extra subject
        this.assertCommandFail("yang-yang: cut tree key");
        // cut without extra subject
        this.assertCommandSucceed("yang-yang: cut tree");
    }

    // only one trigger and only one subject in command, and is valid situation
    @Test
    void testSingleTriggerSingleSubjectValid() {
        // prepare cut
        this.prepareCut("ken");
        // cut with signe trigger & subject
        this.assertCommandSucceed("ken: cut tree", ACTION_SUCCESS_MSG.CUT.getValue());
    }

    // only one trigger and only one subject in command, and isn't valid situation
    @Test
    void testSingleTriggerSingleSubjectInvalid() {
        // prepare cut
        this.prepareCut("ken");
        // cut
        this.assertCommandFail("ken: cut potion");
    }

    // actions with same trigger
    @Test
    void testActionsWithSameTrigger() {
        String m1 = "you throw an axe";
        String m2 = "you throw a potion";
        String m3 = "you throw an axe and a potion";
        Set<GameAction> testActions = Set.of(
            new GameAction(Set.of("throw", "discard"), Set.of("axe"),
                0, 0, Set.of(), Set.of(), m1),
            new GameAction(Set.of("throw", "put"), Set.of("potion"),
                0, 0, Set.of(), Set.of(), m2),
            new GameAction(Set.of("throw"), Set.of("axe", "potion"),
                0, 0, Set.of(), Set.of(), m3)
        );

        String actionFileName = "custom-actions.xml";
        TestUtils.makeActionsFile(testActions, actionFileName);
        this.resetServer("basic-entities.dot", actionFileName);

        // starting in cabin with axe and potion
        // test 1: "throw axe" match 2 should fail
        this.assertCommandFail("simon: throw axe");
        // test 2: "discard axe" triggers Action1
        this.assertCommandSucceed("simon: discard axe", m1);
        // test 3: "discard throw axe" match 2 should fail
        this.assertCommandFail("simon: discard throw axe");
        // test 4: "throw potion" match 2 should fail
        this.assertCommandFail("simon: throw axe");
        // test 5: "put potion" triggers Action2
        this.assertCommandSucceed("simon: put potion", m2);
        // test 6: "put throw potion" match 2 should fail
        this.assertCommandFail("simon: put throw potion");
        // test 7: "throw" with no subject should fail
        this.assertCommandFail("simon: throw");
        // test 8: "throw axe potion" match Action3
        this.assertCommandSucceed("simon: throw axe potion", m3);
        // test 9: case insensitivity
        this.assertCommandSucceed("simon: DISCARD AXE", m1);
        this.assertCommandSucceed("simon: PuT Potion", m2);
        // test 10: decorative words
        this.assertCommandSucceed("simon: please discard axe now", m1);
        this.assertCommandSucceed("simon: put potion gently", m2);
        // test 11: invalid subject with trigger
        this.assertCommandFail("simon: throw key"); // key not in cabin

        // move to forest with axe, potion not present
        this.assertCommandSucceed("simon: get axe");
        this.assertCommandSucceed("simon: goto forest");

        // test 1: "throw axe" triggers Action1
        this.assertCommandSucceed("simon: throw axe", m1);
        // test 2: "discard axe" triggers Action1
        this.assertCommandSucceed("simon: discard axe", m1);
        // test 3: "discard throw axe" triggers Action1
        this.assertCommandSucceed("simon: discard throw axe", m1);
        // test 4: "throw potion" match nothing should fail
        this.assertCommandFail("simon: throw potion");
        // test 5: "put potion" match nothing should fail
        this.assertCommandFail("simon: put potion");
        // test 6: "put throw potion" match nothing should fail
        this.assertCommandFail("simon: put throw potion");
        // test 7: "throw" with no subject should fail
        this.assertCommandFail("simon: throw");
        // test 8: "throw axe potion" match nothing should fail
        this.assertCommandFail("simon: throw axe potion");

        // move back to cabin with potion, axe not present
        this.assertCommandSucceed("simon: drop axe");
        this.assertCommandSucceed("simon: goto cabin");

        // test 1: "throw axe" match nothing should fail
        this.assertCommandFail("simon: throw axe");
        // test 2: "discard axe" match nothing should fail
        this.assertCommandFail("simon: discard axe");
        // test 3: "discard throw axe" match nothing should fail
        this.assertCommandFail("simon: discard throw axe");
        // test 4: "throw potion" triggers Action2
        this.assertCommandSucceed("simon: throw potion", m2);
        // test 5: "put potion" triggers Action2
        this.assertCommandSucceed("simon: put potion", m2);
        // test 6: "put throw potion" triggers Action2
        this.assertCommandSucceed("simon: put throw potion", m2);
        // test 7: "throw" with no subject should fail
        this.assertCommandFail("simon: throw");
        // test 8: "throw axe potion" match nothing should fail
        this.assertCommandFail("simon: throw axe potion");

        // cleanup
        TestUtils.removeFile(actionFileName);
    }

    // match subject is in inventory
    @Test
    void testMatchSubjectInInventory() {
        // prepare cut
        this.prepareCut("ken");
        // cut
        this.assertCommandSucceed("ken: cut with axe", ACTION_SUCCESS_MSG.CUT.getValue());
    }

    // match subject is in location as a furniture
    @Test
    void testMatchFurnitureSubject() {
        // prepare unlock
        this.prepareUnlock("ken");
        // unlock with subject as a furniture
        this.assertCommandSucceed("ken: open by key", ACTION_SUCCESS_MSG.UNLOCK.getValue());
    }

    // match subject is in location as a character
    @Test
    @Tag("extended")
    void testMatchSubjectAsCharacter() {
        Set<String> commands = Set.of(
            "fight elf",
            "elf fight",
            "hit elf",
            "attack elf",
            "fight and fight elf",
            "hit and attack elf",
            "please hit ot attack elf here"
        );
        for (String cmd : commands) {
            this.resetServer(true);
            this.gotoCellar("lin");
            this.assertCommandSucceed("lin: health", "3");
            this.assertCommandSucceed(
                this.cmdWithPlayer("lin", cmd), ACTION_SUCCESS_MSG.FIGHT.getValue());
            this.assertCommandSucceed("lin: health", "2");
        }

        Set<String> invalidCommands = Set.of(
            "fight elfA",
            "fight",
            "elf",
            "fight elf with axe",
            "fight elf in forest"
        );
        for (String invalidCmd : invalidCommands) {
            this.resetServer(true);
            this.gotoCellar("lin");
            this.assertCommandFail(this.cmdWithPlayer("lin", invalidCmd));
        }
    }

    // match subject is in location as a path
    @Test
    void testMatchSubjectAsPath() {
        String m1 = "you throw an axe with path to cabin";
        String m2 = "you discard an axe with path to cellar";
        String m3 = "you unlock the door and see steps leading down into a cellar";
        Set<GameAction> testActions = Set.of(
            new GameAction(Set.of("throw"), Set.of("axe", "cabin"),
                0, 0, Set.of(), Set.of(), m1),
            new GameAction(Set.of("discard"), Set.of("axe", "cellar"),
                0, 0, Set.of(), Set.of(), m2),
            new GameAction(Set.of("open", "unlock"), Set.of("trapdoor", "key"),
                0, 0, Set.of("key"), Set.of("cellar"), m3)
        );

        String actionFileName = "custom-actions.xml";
        TestUtils.makeActionsFile(testActions, actionFileName);
        this.resetServer("basic-entities.dot", actionFileName);

        // should fail Action1(no cabin) and Action2(no cellar)
        this.assertCommandFail("simon: throw axe");
        this.assertCommandFail("simon: discard axe");

        // take axe and move to forest
        this.assertCommandSucceed("simon: get axe");
        this.assertCommandSucceed("simon: goto forest");

        // should match Action1 but no Action2(no cellar)
        this.assertCommandSucceed("simon: throw axe", m1);
        this.assertCommandFail("simon: discard axe");

        // produce new path - cellar
        this.assertCommandSucceed("simon: get key");
        this.assertCommandSucceed("simon: goto cabin");
        this.assertCommandSucceed("simon: open trapdoor");

        // should match Action2 but no Action1(no cabin)
        this.assertCommandFail("simon: throw axe");
        this.assertCommandSucceed("simon: discard axe", m2);

        // cleanup
        TestUtils.removeFile(actionFileName);
    }

    // match subject is in other player's inventory
    @Test
    void testMatchSubjectInOthersInventory() {
        // prepare unlock
        this.assertCommandSucceed("wang: get axe");
        this.assertCommandSucceed("wang: goto forest");
        this.assertCommandSucceed("ken: goto forest");

        // unlock with subject as a furniture
        this.assertCommandFail("ken: cutdown tree");
        this.assertCommandSucceed("wang: cutdown tree", ACTION_SUCCESS_MSG.CUT.getValue());
    }

    // match subject is in other location as an artefact
    @Test
    void testMatchSubjectInOtherLocationArtefact() {
        this.assertCommandSucceed("zoe: goto forest");
        this.assertCommandFail("zoe: open with key");
    }

    // match subject is in other location as a furniture
    @Test
    void testMatchSubjectInOtherLocationFurniture() {
        this.assertCommandSucceed("zoe: goto forest");
        this.assertCommandSucceed("zoe:get key");
        this.assertCommandFail("zoe: open with key");
    }

    // match subject is in other location as a character
    @Test
    @Tag("extended")
    void testMatchSubjectInOtherLocationCharacter() {
        this.assertCommandFail("simon: fight elf");
    }

    // match consume is in inventory
    @Test
    @Tag("extended")
    void testMatchConsumeInInventory() {
        this.assertCommandSucceed("simon: get potion");
        this.assertCommandSucceed("simon: inv", "potion");

        this.assertCommandSucceed("simon: drink potion", ACTION_SUCCESS_MSG.DRINK.getValue());
        this.assertCommandSucceed("simon: inv", "potion", true);
    }

    // match consume is in location as an artefact
    @Test
    @Tag("extended")
    void testMatchConsumeInLocation() {
        this.assertCommandSucceed("simon: look", "potion");
        this.assertCommandSucceed("simon: drink potion", ACTION_SUCCESS_MSG.DRINK.getValue());
        this.assertCommandSucceed("simon: look", "potion", true);
    }

    // match consume is in location as a furniture
    @Test
    @Tag("extended")
    void testMatchConsumeInLocationFurniture() {
        this.prepareCut("simon");
        this.assertCommandSucceed("simon: look", "tree");
        this.assertCommandSucceed("simon: cut tree", ACTION_SUCCESS_MSG.CUT.getValue());
        this.assertCommandSucceed("simon: look", "tree", true);
    }

    // match consume is in location as a character
    @Test
    void testMatchConsumedCharacter() {
        String m1 = "you hide the elf";
        String m2 = "you show the elf in forest";
        String m3 = "you unlock the door and see steps leading down into a cellar";
        Set<GameAction> testActions = Set.of(
            new GameAction(Set.of("hide"), Set.of("elf"),
                0, 0, Set.of("elf"), Set.of(), m1),
            new GameAction(Set.of("show"), Set.of("tree"),
                0, 0, Set.of(), Set.of("elf"), m2),
            new GameAction(Set.of("open", "unlock"), Set.of("trapdoor", "key"),
                0, 0, Set.of("key"), Set.of("cellar"), m3)
        );

        String actionFileName = "custom-actions.xml";
        TestUtils.makeActionsFile(testActions, actionFileName);
        this.resetServer("extended-entities.dot", actionFileName);

        this.gotoCellar("simon");
        this.assertCommandSucceed("simon: look", "elf");
        this.assertCommandSucceed("simon: hide elf", m1);
        this.assertCommandSucceed("simon: look", "elf", true);

        this.assertCommandSucceed("simon: goto cabin");
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: look", "elf", true);
        this.assertCommandSucceed("simon: show among tree", m2);
        this.assertCommandSucceed("simon: look", "elf");

        this.assertCommandSucceed("simon: goto cabin");
        this.assertCommandFail("simon: hide elf");

        // cleanup
        TestUtils.removeFile(actionFileName);
    }

    // match consume is in location as a path
    @Test
    void testMatchConsumedPath() {
        String m1 = "you hide path to forest";
        String m2 = "you show path to forest";
        Set<GameAction> testActions = Set.of(
            new GameAction(Set.of("hide"), Set.of("forest"),
                0, 0, Set.of("forest"), Set.of(), m1),
            new GameAction(Set.of("show"), Set.of("axe"),
                0, 0, Set.of(), Set.of("forest"), m2)
        );

        String actionFileName = "custom-actions.xml";
        TestUtils.makeActionsFile(testActions, actionFileName);
        this.resetServer("basic-entities.dot", actionFileName);

        this.assertCommandSucceed("simon: look", "forest");
        this.assertCommandSucceed("simon: hide forest", m1);
        this.assertCommandSucceed("simon: look", "forest", true);

        this.assertCommandSucceed("simon: look", "forest", true);
        this.assertCommandSucceed("simon: show path with axe on the ground", m2);
        this.assertCommandSucceed("simon: look", "forest");

        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandFail("simon: hide forest");

        // cleanup
        TestUtils.removeFile(actionFileName);
    }

    // match consume is in other player's inventory
    @Test
    @Tag("extended")
    void testMatchConsumeInOtherPlayerInventory() {
        this.assertCommandSucceed("wang: get potion");
        this.assertCommandFail("simon: drink potion");
        this.assertCommandSucceed("wang: drink potion", ACTION_SUCCESS_MSG.DRINK.getValue());
    }

    // match consume is in other location as an artefact
    @Test
    @Tag("extended")
    void testMatchConsumeInOtherLocationArtefact() {
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandFail("simon: drink potion");
    }

    // match consume is in other location as a furniture
    @Test
    @Tag("extended")
    void testMatchConsumeInOtherLocationFurniture() {
        this.prepareCut("simon");

        this.assertCommandSucceed("simon: goto cabin");
        this.assertCommandFail("simon: cut tree");

        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: cut tree");
    }

    // match produce is in storeroom as an artefact
    @Test
    @Tag("extended")
    void testMatchProduceInStoreroomArtefact() {
        this.prepareCut("simon");
        this.assertCommandSucceed("simon: cut tree", ACTION_SUCCESS_MSG.CUT.getValue());
        this.assertCommandSucceed("simon: get log", "log");
    }

    // match produce is in storeroom as a furniture
    @Test
    @Tag("extended")
    void testMatchProduceInInventory() {
        this.assertCommandSucceed("simon: get coin");
        this.gotoCellar("simon");
        this.assertCommandSucceed("simon: pay elf", ACTION_SUCCESS_MSG.PAY.getValue());
        this.assertCommandSucceed("simon: look", "shovel");
    }

    // match produce is in storeroom as a character
    @Test
    @Tag("extended")
    void testMatchProduceInStoreroom() {
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: goto riverbank");

        this.assertCommandSucceed("simon: look", "lumberjack", true);
        this.assertCommandSucceed("simon: blow the horn", ACTION_SUCCESS_MSG.BLOW.getValue());
        this.assertCommandSucceed("simon: look", "cutter");
    }

    // match produce is in one's inventory
    @Test
    void testMatchProducedInInventory() {
        String message = "you found a shovel here";
        Set<GameAction> testActions = Set.of(
            new GameAction(Set.of("search"), Set.of("forest"),
                0, 0, Set.of(), Set.of("shovel"), message)
        );

        String actionFileName = "custom-actions.xml";
        TestUtils.makeActionsFile(testActions, actionFileName);
        this.resetServer("extended-entities.dot", actionFileName);

        this.assertCommandSucceed("simon: look", "shovel", true);
        this.assertCommandSucceed("simon: search in forest", message);
        this.assertCommandSucceed("simon: look", "shovel");

        this.assertCommandSucceed("simon: inventory", "nothing");
        this.assertCommandSucceed("simon: get shovel");
        this.assertCommandSucceed("simon: inventory", "shovel");
        this.assertCommandSucceed("simon: look", "shovel", true);

        // shovel is not in storeroom, should fail
        this.assertCommandFail("simon: search in forest");
        this.assertCommandFail("john: search in forest");
        this.assertCommandSucceed("simon: inventory", "shovel");
        this.assertCommandSucceed("simon: look", "shovel", true);

        // cleanup
        TestUtils.removeFile(actionFileName);
    }

    // match produce is in other location as a furniture
    @Test
    void testMatchProducedAsFurniture() {
        String m1 = "you plant a tree";
        String m2 = "you dig a hole";
        String m3 = "you fill a hole";
        Set<GameAction> testActions = Set.of(
            new GameAction(Set.of("plant"), Set.of("potion"),
                0, 0, Set.of(), Set.of("tree"), m1),
            new GameAction(Set.of("dig"), Set.of("axe"),
                0, 0, Set.of(), Set.of("hole"), m2),
            new GameAction(Set.of("fill"), Set.of("hole", "axe"),
                0, 0, Set.of("hole"), Set.of(), m3)
        );

        String actionFileName = "custom-actions.xml";
        TestUtils.makeActionsFile(testActions, actionFileName);
        this.resetServer("extended-entities.dot", actionFileName);

        this.assertCommandSucceed("simon: look", "tree", true);
        this.assertCommandSucceed("simon: look", "hole", true);
        this.assertCommandFail("simon: plant with potion"); // tree is in other location
        this.assertCommandSucceed("simon: look", "tree", true);
        this.assertCommandSucceed("simon: look", "hole", true);

        this.assertCommandSucceed("simon: dig with axe", m2);
        this.assertCommandSucceed("simon: look", "tree", true);
        this.assertCommandSucceed("simon: look", "hole");

        this.assertCommandSucceed("simon: fill the hole", m3);
        this.assertCommandSucceed("simon: look", "tree", true);
        this.assertCommandSucceed("simon: look", "hole", true);

        this.assertCommandSucceed("simon: dig with axe", m2);
        this.assertCommandSucceed("simon: look", "tree", true);
        this.assertCommandSucceed("simon: look", "hole");

        // cleanup
        TestUtils.removeFile(actionFileName);
    }

    @Test
    void testDuplicatedValidSubject() {
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: get key");
        this.assertCommandSucceed("simon: goto cabin");
        this.assertCommandSucceed("simon: open trapdoor trapdoor with key key");
    }
}
