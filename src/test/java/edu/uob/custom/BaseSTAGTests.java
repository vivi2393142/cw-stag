package edu.uob.custom;

import edu.uob.GameServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BaseSTAGTests {
    protected final String FAIL_PREFIX = "[fail]";

    protected enum ACTION_SUCCESS_MSG {
        UNLOCK("you unlock the trapdoor and see steps leading down into a cellar"),
        CUT("you cut down the tree with the axe"),
        DRINK("you drink the potion and your health improves"),
        FIGHT("you attack the elf, but he fights back and you lose some health"),
        PAY("you pay the elf your silver coin and he produces a shovel"),
        BRIDGE("you bridge the river with the log and can now reach the other side"),
        DIG("you dig into the soft ground and unearth a pot of gold !!!"),
        BLOW("you blow the horn and as if by magic, a lumberjack appears !");

        private final String value;
        ACTION_SUCCESS_MSG(String value) {this.value = value;}
        public String getValue() {return value;}
    }

    protected GameServer server;

    @BeforeEach
    void setup(TestInfo info) {
        this.resetServer(info.getTags().contains("extended"));
    }

    protected void resetServer(boolean isExtended) {
        String entFile;
        String actFile;

        if (isExtended) {
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

    protected String sendCommandToServer(String command) {
        class CommandTask {
            String execute() {return server.handleCommand(command);}
        }
        CommandTask task = new CommandTask();
        return assertTimeoutPreemptively(Duration.ofMillis(1000), task::execute,
            "Server took too long to respond (probably stuck in an infinite loop)");
    }

    /* Assert */
    private String getAssertMessage(String cmd, String action, String expectedResult) {
        return new StringBuilder()
            .append("Command '")
            .append(cmd)
            .append("' should ")
            .append(action)
            .append(" ")
            .append(expectedResult).toString();
    }

    protected void assertCommandSucceed(String cmd, String expectedContent) {
        String response = sendCommandToServer(cmd).toLowerCase();
        assertTrue(response.contains(expectedContent) && !response.contains(FAIL_PREFIX),
            this.getAssertMessage(cmd, "contain", expectedContent)
        );
    }

    protected void assertCommandSucceed(String cmd, String unexpectedContent, boolean isWithout) {
        if (!isWithout) {
            this.assertCommandSucceed(cmd, unexpectedContent);
        } else {
            String response = sendCommandToServer(cmd).toLowerCase();
            assertFalse(response.contains(unexpectedContent) && !response.contains(FAIL_PREFIX),
                this.getAssertMessage(cmd, "not contain", unexpectedContent));
        }
    }

    protected void assertCommandSucceed(String cmd) {
        String response = sendCommandToServer(cmd).toLowerCase();
        assertFalse(response.contains(FAIL_PREFIX),
            this.getAssertMessage(cmd, "success", ""));
    }

    protected void assertCommandFail(String cmd) {
        String response = sendCommandToServer(cmd).toLowerCase();
        assertTrue(response.contains(FAIL_PREFIX),
            this.getAssertMessage(cmd, "fail", ""));
    }


    /* Utility */
    protected void prepareCut(String player) {
        this.assertCommandSucceed(this.cmdWithPlayer(player, "get axe"));
        this.assertCommandSucceed(this.cmdWithPlayer(player, "goto forest"));
    }


    protected void prepareUnlock(String player) {
        this.assertCommandSucceed(this.cmdWithPlayer(player, "goto forest"));
        this.assertCommandSucceed(this.cmdWithPlayer(player, "get key"));
        this.assertCommandSucceed(this.cmdWithPlayer(player, "goto cabin"));
    }

    protected void gotoCellar(String player) {
        this.prepareUnlock(player);
        this.assertCommandSucceed(this.cmdWithPlayer(player, "unlock with key"));
        this.assertCommandSucceed(this.cmdWithPlayer(player, "goto cellar"));
    }

    protected String cmdWithPlayer(String player, String cmd) {
        return new StringBuilder().append(player).append(":").append(cmd).toString();
    }
}
