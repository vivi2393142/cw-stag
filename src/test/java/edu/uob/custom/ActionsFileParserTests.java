package edu.uob.custom;

import edu.uob.GameAction;
import edu.uob.parser.ActionsParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ActionsFileParserTests {
    Set<GameAction> expectedBasicActions = Set.of(
        new GameAction(Set.of("open", "unlock"), Set.of("trapdoor", "key"),
            0, 0, Set.of("key"), Set.of("cellar"),
            "you unlock the trapdoor and see steps leading down into a cellar"),
        new GameAction(Set.of("chop", "cut", "cutdown"), Set.of("tree", "axe"),
            0, 0, Set.of("tree"), Set.of("log"),
            "you cut down the tree with the axe")
    );
    Set<GameAction> expectedExtendedActions = Set.of(
        new GameAction(Set.of("open", "unlock"), Set.of("trapdoor", "key"),
            0, 0, Set.of("key"), Set.of("cellar"),
            "you unlock the trapdoor and see steps leading down into a cellar"),
        new GameAction(Set.of("chop", "cut", "cut down"), Set.of("tree", "axe"),
            0, 0, Set.of("tree"), Set.of("log"),
            "you cut down the tree with the axe"),
        new GameAction(Set.of("drink"), Set.of("potion"),
            0, 1, Set.of("potion"), Set.of(),
            "you drink the potion and your health improves"),
        new GameAction(Set.of("fight", "hit", "attack"), Set.of("elf"),
            1, 0, Set.of(), Set.of(),
            "you attack the elf, but he fights back and you lose some health"),
        new GameAction(Set.of("pay"), Set.of("elf", "coin"),
            0, 0, Set.of("coin"), Set.of("shovel"),
            "you pay the elf your silver coin and he produces a shovel"),
        new GameAction(Set.of("bridge"), Set.of("log", "river"),
            0, 0, Set.of("log"), Set.of("clearing"),
            "you bridge the river with the log and can now reach the other side"),
        new GameAction(Set.of("dig"), Set.of("ground", "shovel"),
            0, 0, Set.of("ground"), Set.of("hole", "gold"),
            "you dig into the soft ground and unearth a pot of gold !!!"),
        new GameAction(Set.of("blow"), Set.of("horn"),
            0, 0, Set.of(), Set.of("lumberjack"),
            "you blow the horn and as if by magic, a lumberjack appears !")
    );

    @Test
    void testParseBasicFile() {
        this.parseAndAssertActions("basic-actions.xml", this.expectedBasicActions);
    }

    @Test
    void testParseExtendedFile() {
        this.parseAndAssertActions("extended-actions.xml", this.expectedExtendedActions);
    }

    @Test
    void testMakeActionsFileUtil() {
        String testBasicFileName = "custom-basic.xml";
        TestUtils.makeActionsFile(this.expectedBasicActions, testBasicFileName);
        this.parseAndAssertActions(testBasicFileName, this.expectedBasicActions);
        TestUtils.removeFile(testBasicFileName);

        String testExtendedFileName = "custom-extended.xml";
        TestUtils.makeActionsFile(this.expectedExtendedActions, testExtendedFileName);
        this.parseAndAssertActions(testExtendedFileName, this.expectedExtendedActions);
        TestUtils.removeFile(testExtendedFileName);
    }

    // should be all lowercase after parse
    @Test
    void testUpperCaseInput() {
        Set<GameAction> upperCasedActions = new HashSet<>();
        for (GameAction action : this.expectedExtendedActions) {
            upperCasedActions.add(new GameAction(
                this.getUpperCaseSet(action.getTriggers()),
                this.getUpperCaseSet(action.getSubjects()),
                action.getConsumedHealth(),
                action.getProducedHealth(),
                this.getUpperCaseSet(action.getConsumedEntities()),
                this.getUpperCaseSet(action.getProducedEntities()),
                action.getNarration().toUpperCase()
            ));
        }

        String testExtendedFileName = "custom-extended.xml";
        TestUtils.makeActionsFile(upperCasedActions, testExtendedFileName);
        this.parseAndAssertActions(testExtendedFileName, this.expectedExtendedActions);
        TestUtils.removeFile(testExtendedFileName);
    }

    private Set<String> getUpperCaseSet(Set<String> originSet) {
        Set<String> newSet = new HashSet<>();
        for (String string : originSet) {
            newSet.add(string.toUpperCase());
        }
        return newSet;
    }

    @Test
    void testNoKeywordInAction() {
        Set<GameAction> actions = Set.of(
            new GameAction(Set.of("get", "unlock"), Set.of("trapdoor", "key"),
                0, 0, Set.of("key"), Set.of("cellar"),
                "you unlock the trapdoor and see steps leading down into a cellar")
        );
        String testExtendedFileName = "custom-extended.xml";
        File actionsFile = TestUtils.makeActionsFile(actions, testExtendedFileName);
        try {
            new ActionsParser(actionsFile);
            fail("Parsing file with keyword in action throw exception");
        } catch (Exception e) {
            // expected exception, test passes
        }
    }

    @Test
    void testParseInvalidFile() {
        // not exist file
        File actionsFile = Paths.get(TestUtils.getFilePath("nonexistent.xml"))
            .toAbsolutePath().toFile();
        try {
            new ActionsParser(actionsFile);
            fail("Parsing nonexistent file should throw exception");
        } catch (Exception e) {
            // expected exception, test passes
        }
    }

    /* Assert */
    // reusable method to parse and assert actions from a file
    private void parseAndAssertActions(String fileName, Set<GameAction> expectedActions) {
        File actionsFile = Paths.get(TestUtils.getFilePath(fileName)).toAbsolutePath().toFile();
        try {
            ActionsParser actionsParser = new ActionsParser(actionsFile);
            Set<GameAction> loadedActions = actionsParser.getActions();
            // check if loaded actions exactly match expected actions regardless of order
            assertTrue(this.checkIsActionsSame(loadedActions, expectedActions),
                "Loaded actions should exactly match expected actions");
        } catch (Exception e) {
            fail("Should not throw exception when parsing valid file");
        }
    }

    /* Utility */
    // check if actual and expected GameAction list are same
    private boolean checkIsActionsSame(Set<GameAction> actions1, Set<GameAction> actions2) {
        if (actions1.size() != actions2.size()) return false;
        for (GameAction action1 : actions1) {
            if (!this.checkContainAction(action1, actions2)) return false;
        }
        return true;
    }

    private boolean checkContainAction(GameAction targetAction, Set<GameAction> actionSet) {
        for (GameAction action : actionSet) {
            if (this.checkIsActionSame(action, targetAction)) return true;
        }
        return false;
    }

    // check if actual and expected GameAction are same
    private boolean checkIsActionSame(GameAction action1, GameAction action2) {
        return this.checkIsStrSetSame(action1.getTriggers(), action2.getTriggers()) &&
               this.checkIsStrSetSame(action1.getSubjects(), action2.getSubjects()) &&
               this.checkIsStrSetSame(action1.getConsumedEntities(), action2.getConsumedEntities()) &&
               this.checkIsStrSetSame(action1.getProducedEntities(), action2.getProducedEntities()) &&
               action1.getConsumedHealth() == action2.getConsumedHealth() &&
               action1.getProducedHealth() == action2.getProducedHealth();
    }

    private boolean checkIsStrSetSame(Set<String> set1, Set<String> set2) {
        Set<String> copiedSet2 = new HashSet<>(set2);
        if (set1.size() != copiedSet2.size()) return false;

        for (String targetStr : set1) {
            if (!copiedSet2.contains(targetStr)) return false;
            copiedSet2.remove(targetStr);
        }
        return true;
    }
}