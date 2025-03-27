package edu.uob.custom;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ActionCommandTests extends BaseSTAGTests {
    // TEST: basic trigger, trigger in two word, invalid trigger
    @Test
    @Tag("extended")
    void testTrigger() {
        // all valid triggers
        String[] validTriggers = {"chop", "cut", "cut down"};
        for (String trigger : validTriggers) {
            this.resetServer(true);
            this.prepareCut("yang-yang");

            String cmd = new StringBuilder()
                .append("yang-yang: ").append(trigger).append(" tree").toString();
            this.assertCommandSucceed(cmd);
        }

        // invalid trigger
        String[] invalidTriggers = {"cutting", "hit", "cutdown"};
        for (String invalidTrigger : invalidTriggers) {
            this.resetServer(true);
            this.prepareCut("yang-yang");

            String cmd = new StringBuilder()
                .append("yang-yang: ").append(invalidTrigger).append(" tree").toString();
            this.assertCommandFail(cmd);
        }
    }

    // TEST: subjects mentioned in command are more than that in action
    @Test
    void testMoreSubjectsThanAction() {
        // prepare cut
        this.prepareCut("yang-yang");
        // cut with extra subject
        this.assertCommandFail("yang-yang: cut tree key");
        // cut without extra subject
        this.assertCommandSucceed("yang-yang: cut tree");
    }

    // TEST: only one trigger and only one subject in command, and is valid situation
    @Test
    void testSingleTriggerSingleSubjectValid() {
        // prepare cut
        this.prepareCut("ken");
        // cut with signe trigger & subject
        this.assertCommandSucceed("ken: cut tree", ACTION_SUCCESS_MSG.CUT.getValue());
    }

    // TEST: only one trigger and only one subject in command, and isn't valid situation
    @Test
    void testSingleTriggerSingleSubjectInvalid() {
        // prepare cut
        this.prepareCut("ken");
        // cut
        this.assertCommandFail("ken: cut potion");
    }

    // TODO: TEST: 2 actions have same trigger
    //  1. 2 valid, match first one
    //  2. only second one valid, match second one

    // TEST: match subject is in inventory
    @Test
    void testMatchSubjectInInventory() {
        // prepare cut
        this.prepareCut("ken");
        // cut
        this.assertCommandSucceed("ken: cut with axe", ACTION_SUCCESS_MSG.CUT.getValue());
    }

    // TEST: match subject is in location as a furniture
    @Test
    void testMatchFurnitureSubject() {
        // prepare unlock
        this.prepareUnlock("ken");
        // unlock with subject as a furniture
        this.assertCommandSucceed("ken: open by key", ACTION_SUCCESS_MSG.UNLOCK.getValue());
    }

    // TEST: match subject is in location as a character
    @Test
    @Tag("extended")
    void testMatchSubjectAsCharacter() {
        String[] commands = {
            "fight elf",
            "elf fight",
            "hit elf",
            "attack elf",
            "fight and fight elf",
            "hit and attack elf",
            "please hit ot attack elf here",
        };
        for (String cmd : commands) {
            this.resetServer(true);
            this.gotoCellar("lin");
            this.assertCommandSucceed("lin: health", "3");
            this.assertCommandSucceed(
                this.cmdWithPlayer("lin", cmd), ACTION_SUCCESS_MSG.FIGHT.getValue());
            this.assertCommandSucceed("lin: health", "2");
        }

        String[] invalidCommands = {
            "fight elfA",
            "fight",
            "elf",
            "fight elf with axe",
            "fight elf in forest"
        };
        for (String invalidCmd : invalidCommands) {
            this.resetServer(true);
            this.gotoCellar("lin");
            this.assertCommandFail(this.cmdWithPlayer("lin", invalidCmd));
        }
    }

    // TODO: TEST: match subject is in location as a path

    // TEST: match subject is in other player's inventory
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

    // TEST: match subject is in other location as an artefact
    @Test
    void testMatchSubjectInOtherLocationArtefact() {
        this.assertCommandSucceed("zoe: goto forest");
        this.assertCommandFail("zoe: open with key");
    }

    // TEST: match subject is in other location as a furniture
    @Test
    void testMatchSubjectInOtherLocationFurniture() {
        this.assertCommandSucceed("zoe: goto forest");
        this.assertCommandSucceed("zoe:get key");
        this.assertCommandFail("zoe: open with key");
    }

    // TEST: match subject is in other location as a character
    @Test
    @Tag("extended")
    void testMatchSubjectInOtherLocationCharacter() {
        this.assertCommandFail("simon: fight elf");
    }

    // TODO: TEST: match subject is in other location as a path

    // TEST: match consume is in inventory
    @Test
    @Tag("extended")
    void testMatchConsumeInInventory() {
        this.assertCommandSucceed("simon: get potion");
        this.assertCommandSucceed("simon: inv", "potion");

        this.assertCommandSucceed("simon: drink potion", ACTION_SUCCESS_MSG.DRINK.getValue());
        this.assertCommandSucceed("simon: inv", "potion", true);
    }

    // TEST: match consume is in location as an artefact
    @Test
    @Tag("extended")
    void testMatchConsumeInLocation() {
        this.assertCommandSucceed("simon: look", "potion");
        this.assertCommandSucceed("simon: drink potion", ACTION_SUCCESS_MSG.DRINK.getValue());
        this.assertCommandSucceed("simon: look", "potion", true);
    }

    // TEST: match consume is in location as a furniture
    @Test
    @Tag("extended")
    void testMatchConsumeInLocationFurniture() {
        this.prepareCut("simon");
        this.assertCommandSucceed("simon: look", "tree");
        this.assertCommandSucceed("simon: cut tree", ACTION_SUCCESS_MSG.CUT.getValue());
        this.assertCommandSucceed("simon: look", "tree", true);
    }

    // TODO: TEST: match consume is in location as a character

    // TODO: TEST: match consume is in location as a path

    // TEST: match consume is in other player's inventory
    @Test
    @Tag("extended")
    void testMatchConsumeInOtherPlayerInventory() {
        this.assertCommandSucceed("wang: get potion");
        this.assertCommandFail("simon: drink potion");
        this.assertCommandSucceed("wang: drink potion", ACTION_SUCCESS_MSG.DRINK.getValue());
    }

    // TEST: match consume is in other location as an artefact
    @Test
    @Tag("extended")
    void testMatchConsumeInOtherLocationArtefact() {
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandFail("simon: drink potion");
    }

    // TEST: match consume is in other location as a furniture
    @Test
    @Tag("extended")
    void testMatchConsumeInOtherLocationFurniture() {
        this.prepareCut("simon");

        this.assertCommandSucceed("simon: goto cabin");
        this.assertCommandFail("simon: cut tree");

        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: cut tree");
    }

    // TODO: TEST: match consume is in other location as a character

    // TODO: TEST: match consume is in other location as a path

    // TEST: match produce is in storeroom as an artefact
    @Test
    @Tag("extended")
    void testMatchProduceInStoreroomArtefact() {
        this.prepareCut("simon");
        this.assertCommandSucceed("simon: cut tree", ACTION_SUCCESS_MSG.CUT.getValue());
        this.assertCommandSucceed("simon: get log", "log");
    }

    // TEST: match produce is in storeroom as a furniture
    @Test
    @Tag("extended")
    void testMatchProduceInInventory() {
        this.assertCommandSucceed("simon: get coin");
        this.gotoCellar("simon");
        this.assertCommandSucceed("simon: pay elf", ACTION_SUCCESS_MSG.PAY.getValue());
        this.assertCommandSucceed("simon: look", "shovel");
    }

    // TODO: TEST: match produce is in storeroom as a character
    @Test
    @Tag("extended")
    void testMatchProduceInStoreroom() {
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: goto riverbank");

        this.assertCommandSucceed("simon: look", "lumberjack", true);
        this.assertCommandSucceed("simon: blow the horn", ACTION_SUCCESS_MSG.BLOW.getValue());
        this.assertCommandSucceed("simon: look", "cutter");
    }

    // TODO: TEST: match produce is in one's inventory

    // TODO: TEST: match produce is in other location as a furniture

    // TODO: TEST: match produce is in other location as a character

    // TODO: TEST: match produce is in other location as a path
}
