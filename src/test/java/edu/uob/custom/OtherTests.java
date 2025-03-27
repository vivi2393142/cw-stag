package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class OtherTests {
//    private final String FAIL_PREFIX = "[Fail]";
//    private GameServer server;
//
//    @BeforeEach
//    void setup() {
//        File entitiesFile = Paths.get(
//            new StringBuilder()
//                .append("config")
//                .append(File.separator)
//                .append("basic-entities.dot")
//                .toString()
//        ).toAbsolutePath().toFile();
//
//        File actionsFile = Paths.get(
//            new StringBuilder()
//                .append("config")
//                .append(File.separator)
//                .append("basic-actions.xml")
//                .toString()
//        ).toAbsolutePath().toFile();
//
//        this.server = new GameServer(entitiesFile, actionsFile);
//    }

    //    String sendCommandToServer(String command) {
//        class CommandTask {
//            String execute() {return server.handleCommand(command);}
//        }
//        CommandTask task = new CommandTask();
//        return assertTimeoutPreemptively(Duration.ofMillis(1000), task::execute,
//            "Server took too long to respond (probably stuck in an infinite loop)");
//    }
    private final String FAIL_PREFIX = "[Fail]";
    private GameServer server;

    @BeforeEach
    void setup(TestInfo info) {
        String entFile;
        String actFile;

        if (info.getTags().contains("extended")) {
            entFile = "extended-entities.dot";
            actFile = "extended-actions.xml";
        } else {
            entFile = "basic-entities.dot";
            actFile = "basic-actions.xml";
        }

        File entitiesFile = Paths.get(
            new StringBuilder().append("config").append(File.separator).append(entFile).toString()
        ).toAbsolutePath().toFile();
        File actionsFile = Paths.get(
            new StringBuilder().append("config").append(File.separator).append(actFile).toString()
        ).toAbsolutePath().toFile();

        this.server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        class CommandTask {
            String execute() {return server.handleCommand(command);}
        }
        CommandTask task = new CommandTask();
        return assertTimeoutPreemptively(Duration.ofMillis(1000), task::execute,
            "Server took too long to respond (probably stuck in an infinite loop)");
    }

    /* Player */
    // TEST: command should start with player name like "xxx: xxx"
    @Test
    void testCommandMustStartWithPlayerName() {
        String response = sendCommandToServer("look");
        assertTrue(response.contains(this.FAIL_PREFIX), "Command without player name should fail");

        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("error"), "Command with player name should succeed");
    }

    // TEST: Valid player names can consist of uppercase and lowercase letters, spaces,
    // apostrophes and hyphens
    @Test
    void testValidPlayerNames() {
        assertTrue(sendCommandToServer("Simon: look").contains("cabin"),
            "Uppercase should work");
        assertTrue(sendCommandToServer("joe: look").contains("cabin"),
            "Lowercase should work");
        assertTrue(sendCommandToServer("simon doe: look").contains("cabin"),
            "Space should work");
        assertTrue(sendCommandToServer("O'Brien: look").contains("cabin"),
            "Apostrophe should work");
        assertTrue(sendCommandToServer("Mary-Jane: look").contains("cabin"),
            "Hyphen should work");

        assertTrue(sendCommandToServer("Simon123: look").contains(this.FAIL_PREFIX),
            "Numbers should fail");
    }

    // TEST: player name cannot use any keywords
    @Test
    void testPlayerNameCannotUseKeywords() {
        String[] keywords = {"health", "inv", "inventory", "get", "drop", "goto", "look"};
        for (String keyword : keywords) {
            String response = sendCommandToServer(
                new StringBuilder().append(keyword).append(": look").toString());
            assertTrue(response.contains(this.FAIL_PREFIX), "Player name is invalid");
        }
    }

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
        for (String cmd : commands) {
            String response = sendCommandToServer(cmd).toLowerCase();
            assertFalse(response.contains(this.FAIL_PREFIX),
                "Command should be case-insensitive");
        }
    }

    // TODO: TEST: it is not possible for the configuration files to contain two different things
    //  with the same name, but different capitalisation (e.g. there cannot be a door and
    //  a DOOR in the same game)

    // TEST: [Decorated Commands] he basic command chop tree with axe might well be entered by
    // the user as please chop the tree using the axe
    // Each incoming command MUST contain a trigger phrase and at least one subject
    @Test
    void testDecoratedCommands1() {
        // prepare to cut
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");

        // cut
        String response = sendCommandToServer("simon: please cut tree using the axe").toLowerCase();
        assertTrue(response.contains("you cut"),
            "Different word orders should match same action");
    }

    @Test
    void testDecoratedCommands2() {
        // prepare to cut
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");

        // cut
        String response = sendCommandToServer("simon: cut the axe").toLowerCase();
        assertTrue(response.contains("you cut"),
            "Different word orders should match same action");

        String[] commands = {
            "simon: cut the axe here"
        };
    }

    @Test
    void testDecoratedCommands3() {
        // prepare to cut
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");

        // cut
        String response = sendCommandToServer("simon: cut the axe here").toLowerCase();
        assertTrue(response.contains("you cut"),
            "Different word orders should match same action");
    }

    // TODO: test decorative command on built-in commands

    // TEST: [Word Ordering] chop tree with axe and use axe to chop tree are equivalent
    @Test
    void testWordOrdering1() {
        // prepare to cut
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");

        // cut
        String response = sendCommandToServer("simon: use axe to cut tree").toLowerCase();
        assertTrue(response.contains("you cut"),
            "Different word orders should match same action");
    }

    @Test
    void testWordOrdering2() {
        // prepare to cut
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");

        // cut
        String response = sendCommandToServer("simon: cut tree with axe").toLowerCase();
        assertTrue(response.contains("you cut"),
            "Different word orders should match same action");
    }

    // TEST: [Partial Commands] the command unlock trapdoor with key could alternatively be
    // entered as either unlock trapdoor or unlock with key
    @Test
    void testPartialCommands1() {
        // prepare to unlock
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");

        // unlock
        String response = sendCommandToServer("simon: unlock trapdoor").toLowerCase();
        assertTrue(response.contains("unlock"), "Partial commands should work");
    }

    @Test
    void testPartialCommands2() {
        // prepare to unlock
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");

        // unlock
        String response = sendCommandToServer("simon: unlock with key").toLowerCase();
        assertTrue(response.contains("unlock"), "Partial commands should work");
    }

    // TEST: [Extraneous Entities] When searching for an action, you must match ALL of the
    // subjects that are specified in the incoming command (e.g. repair door with hammer and
    // nails). Extraneous entities included within an incoming command (i.e. entities that are in
    // the incoming command, but not specified in the action file) should prevent a match from
    // being made.
    @Test
    void testExtraneousEntities() {
        // prepare to cut
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");

        // cut with extraneous entity
        String response = sendCommandToServer("simon: cut tree with axe and potion");
        assertTrue(response.contains(this.FAIL_PREFIX),
            "Extraneous entity 'potion' match fail");

        // cut without extraneous entity
        response = sendCommandToServer("simon: cut tree with axe and").toLowerCase();
        assertTrue(response.contains("you cut"),
            "Should cut successfully without extraneous entity");
    }

    // TEST: prevent the user attempting to perform actions with inappropriate entities
    // (e.g. open potion with hammer should not succeed)
    @Test
    void testInappropriateEntities() {
        // prepare to unlock
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");

        // unlock
        String response = sendCommandToServer("simon: unlock trapdoor with axe");
        assertTrue(response.contains(this.FAIL_PREFIX), "Inappropriate 'axe' should fail");

        response = sendCommandToServer("simon: unlock trapdoor with key");
        assertTrue(response.contains("unlock"), "Appropriate 'key' should work");
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
