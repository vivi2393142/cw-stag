package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public abstract class BaseSTAGTest {
    protected final String FAIL_PREFIX = "[Fail]";
    protected GameServer server;

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

    protected String sendCommandToServer(String command) {
        class CommandTask {
            String execute() {return server.handleCommand(command);}
        }
        CommandTask task = new CommandTask();
        return assertTimeoutPreemptively(Duration.ofMillis(1000), task::execute,
            "Server took too long to respond (probably stuck in an infinite loop)");
    }
}
