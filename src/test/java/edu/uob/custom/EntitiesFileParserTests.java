package edu.uob.custom;

import edu.uob.EntityType;
import edu.uob.data.EntityData;
import edu.uob.data.InteractableEntityData;
import edu.uob.data.PathData;
import edu.uob.parser.EntitiesParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class EntitiesFileParserTests {
    // expected basic data
    EntityData[] expectedBasicLocations = {
        new EntityData("cabin", "a log cabin in the woods"),
        new EntityData("forest", "a dark forest"),
        new EntityData("cellar", "a dusty cellar"),
        new EntityData("storeroom", "storage for any entities not placed in the game")
    };
    PathData[] expectedBasicPaths = {
        new PathData("cabin", "forest"),
        new PathData("forest", "cabin"),
        new PathData("cellar", "cabin")
    };
    InteractableEntityData[] expectedBasicEntities = {
        new InteractableEntityData(
            "axe", "a razor sharp axe", "cabin", EntityType.ARTEFACT),
        new InteractableEntityData(
            "potion", "magic potion", "cabin", EntityType.ARTEFACT),
        new InteractableEntityData(
            "trapdoor", "wooden trapdoor", "cabin", EntityType.FURNITURE),
        new InteractableEntityData(
            "key", "brass key", "forest", EntityType.ARTEFACT),
        new InteractableEntityData(
            "tree", "a big tree", "forest", EntityType.FURNITURE),
        new InteractableEntityData(
            "elf", "angry elf", "cellar", EntityType.CHARACTER),
        new InteractableEntityData(
            "log", "a heavy wooden log", "storeroom", EntityType.ARTEFACT)
    };

    // expected extended data
    EntityData[] expectedExtendedLocations = {
        new EntityData("cabin", "a log cabin in the woods"),
        new EntityData("forest", "a deep dark forest"),
        new EntityData("cellar", "a dusty cellar"),
        new EntityData("riverbank", "a grassy riverbank"),
        new EntityData("clearing", "a clearing in the woods"),
        new EntityData("storeroom",
            "storage for any entities not placed in the game")
    };

    PathData[] expectedExtendedPaths = {
        new PathData("cabin", "forest"),
        new PathData("forest", "cabin"),
        new PathData("cellar", "cabin"),
        new PathData("forest", "riverbank"),
        new PathData("riverbank", "forest"),
        new PathData("clearing", "riverbank")
    };

    InteractableEntityData[] expectedExtendedEntities = {
        new InteractableEntityData("potion", "a bottle of magic potion",
            "cabin", EntityType.ARTEFACT),
        new InteractableEntityData("axe", "a razor sharp axe",
            "cabin", EntityType.ARTEFACT),
        new InteractableEntityData("coin", "a silver coin",
            "cabin", EntityType.ARTEFACT),
        new InteractableEntityData("trapdoor",
            "a locked wooden trapdoor in the floor", "cabin",
            EntityType.FURNITURE),
        new InteractableEntityData("key", "a rusty old key",
            "forest", EntityType.ARTEFACT),
        new InteractableEntityData("tree", "a tall pine tree",
            "forest", EntityType.FURNITURE),
        new InteractableEntityData("elf", "an angry looking elf",
            "cellar", EntityType.CHARACTER),
        new InteractableEntityData("horn", "an old brass horn",
            "riverbank", EntityType.ARTEFACT),
        new InteractableEntityData("river", "a fast flowing river",
            "riverbank", EntityType.FURNITURE),
        new InteractableEntityData("ground",
            "it looks like the soil has been recently disturbed", "clearing",
            EntityType.FURNITURE),
        new InteractableEntityData("lumberjack", "a burly wood cutter",
            "storeroom", EntityType.CHARACTER),
        new InteractableEntityData("log", "a heavy wooden log",
            "storeroom", EntityType.ARTEFACT),
        new InteractableEntityData("shovel", "a sturdy shovel",
            "storeroom", EntityType.ARTEFACT),
        new InteractableEntityData("gold", "a big pot of gold",
            "storeroom", EntityType.ARTEFACT),
        new InteractableEntityData("hole", "a deep hole in the ground",
            "storeroom", EntityType.FURNITURE)
    };

    @Test
    void testParseBasicFile() {
        this.parseAndAssertEntities("basic-entities.dot",
            this.expectedBasicLocations, this.expectedBasicPaths, this.expectedBasicEntities);
    }

    @Test
    void testParseExtendedFile() {
        this.parseAndAssertEntities("extended-entities.dot",
            this.expectedExtendedLocations, this.expectedExtendedPaths,
            this.expectedExtendedEntities);
    }

    @Test
    void testMakeEntitiesFileUtil() {
        String testBasicFileName = "test-basic.dot";
        TestUtils.makeEntitiesFile(
            this.expectedBasicLocations, this.expectedBasicPaths, this.expectedBasicEntities,
            testBasicFileName);
        this.parseAndAssertEntities(testBasicFileName,
            this.expectedBasicLocations, this.expectedBasicPaths, this.expectedBasicEntities);
        TestUtils.removeFile(testBasicFileName);

        String testExtendedFileName = "test-extend.dot";
        TestUtils.makeEntitiesFile(
            this.expectedExtendedLocations, this.expectedExtendedPaths,
            this.expectedExtendedEntities, testExtendedFileName);
        this.parseAndAssertEntities(
            testExtendedFileName, this.expectedExtendedLocations,
            this.expectedExtendedPaths, this.expectedExtendedEntities);
        TestUtils.removeFile(testExtendedFileName);
    }

    // should be all lowercase after parse
    @Test
    void testUpperCaseInput() {
        EntityData[] upperCasedLocations = new EntityData[this.expectedExtendedLocations.length];
        int i = 0;
        for (EntityData location : this.expectedExtendedLocations) {
            upperCasedLocations[i] = new EntityData(
                location.getName().toUpperCase(),
                location.getDescription().toUpperCase()
            );
            i++;
        }

        PathData[] upperCasedPaths = new PathData[this.expectedExtendedPaths.length];
        int j = 0;
        for (PathData path : this.expectedExtendedPaths) {
            upperCasedPaths[j] = new PathData(
                path.getFromLocationName().toUpperCase(),
                path.getToLocationName().toUpperCase()
            );
            j++;
        }

        InteractableEntityData[] upperCasedEntities =
            new InteractableEntityData[this.expectedExtendedEntities.length];
        int k = 0;
        for (InteractableEntityData entity : this.expectedExtendedEntities) {
            upperCasedEntities[k] = new InteractableEntityData(
                entity.getName().toUpperCase(),
                entity.getDescription().toUpperCase(),
                entity.getLocationName().toUpperCase(),
                entity.getType()
            );
            k++;
        }

        String testExtendedFileName = "test-extended.dot";
        TestUtils.makeEntitiesFile(upperCasedLocations, upperCasedPaths, upperCasedEntities,
            testExtendedFileName);
        this.parseAndAssertEntities(testExtendedFileName, this.expectedExtendedLocations,
            this.expectedExtendedPaths, this.expectedExtendedEntities);
        TestUtils.removeFile(testExtendedFileName);
    }

    @Test
    void testNoKeywordInEntity() {
        String testExtendedFileName = "custom-fail-extended.dot";

        // keyword in location
        int locationCtn = this.expectedBasicLocations.length;
        EntityData[] locations = new EntityData[locationCtn + 1];
        for (int i = 0; i < locationCtn; i++) locations[i] = this.expectedBasicLocations[i];
        locations[locationCtn] = new EntityData("get", "invalid location");

        this.assertFailParse(testExtendedFileName,
            locations, this.expectedBasicPaths, this.expectedBasicEntities,
            "Parsing file with keyword in location throw exception"
        );

        // keyword in entity
        int entCtn = this.expectedBasicEntities.length;
        InteractableEntityData[] entities = new InteractableEntityData[entCtn + 1];
        for (int j = 0; j < entCtn; j++) entities[j] = this.expectedBasicEntities[j];
        entities[entCtn] = new InteractableEntityData("get", "invalid",
            "cabin", EntityType.ARTEFACT);

        this.assertFailParse(testExtendedFileName,
            this.expectedBasicLocations, this.expectedBasicPaths, entities,
            "Parsing file with keyword in entity name throw exception"
        );
    }

    // add storeroom if not in file
    @Test
    void testAddStoreroom() {
        EntityData[] locationsWithoutStoreroom = {
            new EntityData("cabin", "a log cabin in the woods"),
            new EntityData("forest", "a dark forest"),
            new EntityData("cellar", "a dusty cellar"),
        };
        String fileName = "custom-entities.dot";
        File entitiesFile = TestUtils.makeEntitiesFile(locationsWithoutStoreroom,
            this.expectedBasicPaths, this.expectedBasicEntities, fileName);

        try {
            EntitiesParser parser1 = new EntitiesParser(entitiesFile, null);
            assertFalse(this.hasStoreroom(parser1.getLocations()),
                "Should not add storeroom if not declare");

            EntitiesParser parser2 =
                new EntitiesParser(entitiesFile, new EntityData("storeroom", ""));
            assertTrue(this.hasStoreroom(parser2.getLocations()),
                "Should add storeroom if declare");

            TestUtils.removeFile(fileName);
        } catch (Exception e) {
            fail("Should not throw exception when parsing valid file");
        }
    }

    boolean hasStoreroom(EntityData[] locations) {
        for (EntityData data : locations) {
            if (data.getName().equalsIgnoreCase("storeroom")) return true;
        }
        return false;
    }

    @Test
    void testParseInvalidFile() {
        File entitiesFile = Paths.get(TestUtils.getFilePath("nonexistent.dot"))
            .toAbsolutePath().toFile();
        try {
            new EntitiesParser(entitiesFile, null);
            fail("Parsing nonexistent file should throw exception");
        } catch (Exception e) {
            // expected exception, test passes
        }
    }

    /* Assert */
    void assertFailParse(String fileName, EntityData[] locations, PathData[] paths,
                         InteractableEntityData[] entities, String errorMessage) {
        File entitiesFile = TestUtils.makeEntitiesFile(locations, paths, entities, fileName);
        try {
            new EntitiesParser(entitiesFile, null);
            fail(errorMessage);
        } catch (Exception e) {
            // expected exception, test passes
            TestUtils.removeFile(fileName);
        }
    }

    // parse and assert entities from a file against expected data
    private void parseAndAssertEntities(String fileName, EntityData[] expectedLocations,
                                        PathData[] expectedPaths, InteractableEntityData[] expectedEntities) {
        File entitiesFile = Paths.get(TestUtils.getFilePath(fileName)).toAbsolutePath().toFile();
        EntityData[] loadedLocations;
        PathData[] loadedPaths;
        InteractableEntityData[] loadedEntities;
        try {
            EntitiesParser entitiesParser = new EntitiesParser(entitiesFile, null);
            loadedLocations = entitiesParser.getLocations();
            loadedPaths = entitiesParser.getPaths();
            loadedEntities = entitiesParser.getEntities();
        } catch (Exception e) {
            fail("Should not throw exception when parsing valid file");
            return;
        }

        assertTrue(this.checkIsEntityDataSame(loadedLocations, expectedLocations),
            "Loaded locations should exactly match expected locations");
        assertTrue(this.checkIsPathDataSame(loadedPaths, expectedPaths),
            "Loaded paths should exactly match expected paths");
        assertTrue(this.checkIsInteractableEntityDataSame(loadedEntities, expectedEntities),
            "Loaded entities should exactly match expected entities");
    }

    /* Utility */
    // check if two EntityData arrays are identical regardless of order
    private boolean checkIsEntityDataSame(EntityData[] entities1, EntityData[] entities2) {
        if (entities1.length != entities2.length) return false;

        EntityData[] tempEntities2 = Arrays.copyOf(entities2, entities2.length);
        boolean[] matched = new boolean[entities2.length];

        for (EntityData entity1 : entities1) {
            boolean found = false;
            for (int j = 0; j < tempEntities2.length; j++) {
                if (!matched[j] && checkIsEntityDataEqual(entity1, tempEntities2[j])) {
                    matched[j] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    private boolean checkIsEntityDataEqual(EntityData entity1, EntityData entity2) {
        return entity1.getName().equals(entity2.getName()) &&
               entity1.getDescription().equals(entity2.getDescription());
    }

    // check if two PathData arrays are identical regardless of order
    private boolean checkIsPathDataSame(PathData[] paths1, PathData[] paths2) {
        if (paths1.length != paths2.length) return false;

        PathData[] tempPaths2 = Arrays.copyOf(paths2, paths2.length);
        boolean[] matched = new boolean[paths2.length];

        for (PathData path1 : paths1) {
            boolean found = false;
            for (int j = 0; j < tempPaths2.length; j++) {
                if (!matched[j] && checkIsPathDataEqual(path1, tempPaths2[j])) {
                    matched[j] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    private boolean checkIsPathDataEqual(PathData path1, PathData path2) {
        return path1.getFromLocationName().equals(path2.getFromLocationName()) &&
               path1.getToLocationName().equals(path2.getToLocationName());
    }

    // check if two InteractableEntityData arrays are identical regardless of order
    private boolean checkIsInteractableEntityDataSame(InteractableEntityData[] entities1,
                                                      InteractableEntityData[] entities2) {
        if (entities1.length != entities2.length) return false;

        InteractableEntityData[] tempEntities2 = Arrays.copyOf(entities2, entities2.length);
        boolean[] matched = new boolean[entities2.length];

        for (InteractableEntityData entity1 : entities1) {
            boolean found = false;
            for (int j = 0; j < tempEntities2.length; j++) {
                if (!matched[j] && checkIsInteractableEntityDataEqual(entity1, tempEntities2[j])) {
                    matched[j] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    private boolean checkIsInteractableEntityDataEqual(InteractableEntityData entity1,
                                                       InteractableEntityData entity2) {
        return entity1.getName().equals(entity2.getName()) &&
               entity1.getDescription().equals(entity2.getDescription()) &&
               entity1.getLocationName().equals(entity2.getLocationName()) &&
               entity1.getType() == entity2.getType();
    }
}
