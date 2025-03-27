package edu.uob.custom;

import org.junit.jupiter.api.Test;

public class OtherTests extends BaseSTAGTests {
    /* Player */
    // TEST: command should start with player name like "xxx: xxx"
    @Test
    void testCommandMustStartWithPlayerName() {
        this.assertCommandFail("look");
        this.assertCommandSucceed("simon: look");
    }

    // TEST: Valid player names can consist of uppercase and lowercase letters, spaces,
    // apostrophes and hyphens
    @Test
    void testValidPlayerNames() {
        // valid names
        this.assertCommandSucceed("Simon: look", "cabin");
        this.assertCommandSucceed("joe: look", "cabin");
        this.assertCommandSucceed("si  mon do  e: look", "cabin");
        this.assertCommandSucceed("O'Brien: look", "cabin");
        this.assertCommandSucceed("Mary-Jane: look", "cabin");

        // invalid names
        this.assertCommandFail("Simon123: look");
        this.assertCommandFail("Simon!: look");
    }

    // TEST: player name cannot use any keywords
    @Test
    void testPlayerNameCannotUseKeywords() {
        String[] keywords = {"health", "inv", "inventory", "get", "drop", "goto", "look"};
        for (String keyword : keywords) {
            String cmd = new StringBuilder().append(keyword).append(": look").toString();
            this.assertCommandFail(cmd);
        }

        String[] validWords = {"invest", "dropping", "gotoo", "olook"};
        for (String word : validWords) {
            String cmd = new StringBuilder().append(word).append(": look").toString();
            this.assertCommandSucceed(cmd);
        }
    }

    // TODO: TEST: case-insensitive player
    // 1. if create player in different case, should be the same one
    // 2. should get player with different case

    /* Entity */
    // TODO: TEST: built-in commands are reserved words and therefore cannot be used as names for
    // any other elements of the command language

    // TODO: TEST: if no storeroom, add it in

    /* Command Flexibility */
    // TEST: All commands (including entity names, locations, built-in commands and action triggers)
    // should be treated as case-insensitive
    @Test
    void testCaseInsensitiveCommands() {
        String[] commands = {"simon: LOOK", "Simon: Get potion", "simon: goto FOREST"};
        for (String cmd : commands) this.assertCommandSucceed(cmd);
    }

    // TODO: TEST: it is not possible for the configuration files to contain two different things
    //  with the same name, but different capitalisation (e.g. there cannot be a door and
    //  a DOOR in the same game)

    // TEST: [Decorated Commands] he basic command chop tree with axe might well be entered by
    // the user as please chop the tree using the axe
    // Each incoming command MUST contain a trigger phrase and at least one subject
    @Test
    void testDecoratedCommands() {
        String[] commands = {
            "simon: please cut tree using the axe",
            "simon: cut the axe",
            "simon: i cut the axe here"
        };

        for (String cmd : commands) {
            this.resetServer(false);
            this.assertCommandSucceed("simon: get axe");
            this.assertCommandSucceed("simon: goto forest");
            this.assertCommandSucceed(cmd, ACTION_SUCCESS_MSG.CUT.getValue());
        }
    }

    // TODO: test decorative command on built-in commands

    // TEST: [Word Ordering] chop tree with axe and use axe to chop tree are equivalent
    @Test
    void testWordOrdering() {
        String[] commands = {
            "simon: use axe to cut tree",
            "simon: cut tree with axe",
            "simon: i cut the axe here"
        };

        for (String cmd : commands) {
            this.resetServer(false);
            this.assertCommandSucceed("simon: get axe");
            this.assertCommandSucceed("simon: goto forest");
            this.assertCommandSucceed(cmd, ACTION_SUCCESS_MSG.CUT.getValue());
        }
    }

    // TEST: [Partial Commands] the command unlock trapdoor with key could alternatively be
    // entered as either unlock trapdoor or unlock with key
    @Test
    void testPartialCommands() {
        String[] commands = {
            "simon: unlock trapdoor",
            "simon: unlock with key"
        };

        for (String cmd : commands) {
            this.resetServer(false);
            this.assertCommandSucceed("simon: goto forest");
            this.assertCommandSucceed("simon: get key");
            this.assertCommandSucceed("simon: goto cabin");
            this.assertCommandSucceed(cmd, ACTION_SUCCESS_MSG.UNLOCK.getValue());
        }
    }

    // TEST: [Extraneous Entities] When searching for an action, you must match ALL of the
    // subjects that are specified in the incoming command (e.g. repair door with hammer and
    // nails). Extraneous entities included within an incoming command (i.e. entities that are in
    // the incoming command, but not specified in the action file) should prevent a match from
    // being made.
    @Test
    void testExtraneousEntities() {
        // prepare to cut
        this.assertCommandSucceed("simon: get axe");
        this.assertCommandSucceed("simon: goto forest");

        // cut with extraneous entity
        this.assertCommandFail("simon: cut tree with axe and potion");

        // cut without extraneous entity
        this.assertCommandSucceed("simon: cut tree with axe and",
            ACTION_SUCCESS_MSG.CUT.getValue());
    }

    // TEST: prevent the user attempting to perform actions with inappropriate entities
    // (e.g. open potion with hammer should not succeed)
    @Test
    void testInappropriateEntities() {
        // prepare to unlock
        this.assertCommandSucceed("simon: get axe");
        this.assertCommandSucceed("simon: goto forest");
        this.assertCommandSucceed("simon: get key");
        this.assertCommandSucceed("simon: goto cabin");

        // unlock
        this.assertCommandFail("simon: unlock trapdoor with axe");

        this.assertCommandSucceed("simon: unlock trapdoor with key",
            ACTION_SUCCESS_MSG.UNLOCK.getValue());
    }

    /* Unsure Tests */
    // TEST: 2 built-in keywords in 1 command?

    // TEST: built-in action with decorative words?

    // TEST: built-in action with unexpected order?

    // TEST: have built-in keyword & custom action trigger in 1 command?

    /* Parser */
    // TEST: parse different actionsFiles
    // TEST: should be all lowercase after parse actionsFiles

    // TEST: parse different entitiesFiles
    // TEST: should be all lowercase after parse entitiesFiles

    /* Other */
    // TEST: add two same subjects to every kind of commands
}
