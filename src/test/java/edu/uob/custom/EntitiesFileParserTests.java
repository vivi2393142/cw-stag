package edu.uob.custom;

import edu.uob.EntityType;
import edu.uob.data.EntityData;
import edu.uob.data.InteractableEntityData;
import edu.uob.data.PathData;
import edu.uob.parser.EntitiesParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class EntitiesFileParserTests {
    // expected basic data
    final EntityData basicStartLocation =
        new EntityData("cabin", "a log cabin in the woods");
    Set<EntityData> expectedBasicLocations = Set.of(
        this.basicStartLocation,
        new EntityData("forest", "a dark forest"),
        new EntityData("cellar", "a dusty cellar"),
        new EntityData("storeroom", "storage for any entities not placed in the game")
    );
    Set<PathData> expectedBasicPaths = Set.of(
        new PathData("cabin", "forest"),
        new PathData("forest", "cabin"),
        new PathData("cellar", "cabin")
    );
    Set<InteractableEntityData> expectedBasicEntities = Set.of(
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
    );

    // expected extended data
    final EntityData extendedStartLocation =
        new EntityData("cabin", "a log cabin in the woods");
    Set<EntityData> expectedExtendedLocations = Set.of(
        this.extendedStartLocation,
        new EntityData("forest", "a deep dark forest"),
        new EntityData("cellar", "a dusty cellar"),
        new EntityData("riverbank", "a grassy riverbank"),
        new EntityData("clearing", "a clearing in the woods"),
        new EntityData("storeroom",
            "storage for any entities not placed in the game")
    );

    Set<PathData> expectedExtendedPaths = Set.of(
        new PathData("cabin", "forest"),
        new PathData("forest", "cabin"),
        new PathData("cellar", "cabin"),
        new PathData("forest", "riverbank"),
        new PathData("riverbank", "forest"),
        new PathData("clearing", "riverbank")
    );

    Set<InteractableEntityData> expectedExtendedEntities = Set.of(
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
    );

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
        TestUtils.makeEntitiesFile(this.basicStartLocation,
            this.expectedBasicLocations, this.expectedBasicPaths, this.expectedBasicEntities,
            testBasicFileName);
        this.parseAndAssertEntities(testBasicFileName,
            this.expectedBasicLocations, this.expectedBasicPaths, this.expectedBasicEntities);
        TestUtils.removeFile(testBasicFileName);

        String testExtendedFileName = "test-extend.dot";
        TestUtils.makeEntitiesFile(this.extendedStartLocation,
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
        EntityData upperCasedStartLocation = null;
        Set<EntityData> upperCasedLocations = new HashSet<>();
        for (EntityData location : this.expectedExtendedLocations) {
            EntityData newLocation = new EntityData(
                location.getName().toUpperCase(),
                location.getDescription().toUpperCase()
            );
            if (newLocation.getName().equalsIgnoreCase(this.extendedStartLocation.getName())) {
                upperCasedStartLocation = newLocation;
            }
            upperCasedLocations.add(newLocation);
        }

        Set<PathData> upperCasedPaths = new HashSet<>();
        for (PathData path : this.expectedExtendedPaths) {
            upperCasedPaths.add(new PathData(
                path.getFromLocationName().toUpperCase(),
                path.getToLocationName().toUpperCase()
            ));
        }

        Set<InteractableEntityData> upperCasedEntities = new HashSet<>();
        for (InteractableEntityData entity : this.expectedExtendedEntities) {
            upperCasedEntities.add(new InteractableEntityData(
                entity.getName().toUpperCase(),
                entity.getDescription().toUpperCase(),
                entity.getLocationName().toUpperCase(),
                entity.getType()
            ));
        }

        String testExtendedFileName = "test-extended.dot";
        TestUtils.makeEntitiesFile(upperCasedStartLocation, upperCasedLocations,
            upperCasedPaths, upperCasedEntities, testExtendedFileName);
        this.parseAndAssertEntities(testExtendedFileName, this.expectedExtendedLocations,
            this.expectedExtendedPaths, this.expectedExtendedEntities);
        TestUtils.removeFile(testExtendedFileName);
    }

    @Test
    void testNoKeywordInEntity() {
        String testExtendedFileName = "custom-extended.dot";

        // keyword in location
        EntityData startLocationWithKeyword =
            new EntityData("get", "invalid location");
        Set<EntityData> locationsWithKeyword = Set.of(startLocationWithKeyword);
        Set<PathData> paths = new HashSet<>();
        Set<InteractableEntityData> entities = Set.of(
            new InteractableEntityData("key", "key detail",
                "get", EntityType.ARTEFACT)
        );
        this.assertFailParse(testExtendedFileName, startLocationWithKeyword,
            locationsWithKeyword, paths, entities,
            "Parsing file with keyword in location throw exception"
        );

        // keyword in entity
        EntityData startLocation = new EntityData("cabin", "valid location");
        Set<EntityData> locations = Set.of(startLocation);
        Set<InteractableEntityData> entitiesWithKeyword = Set.of(
            new InteractableEntityData("look", "invalid artefact",
                "cabin", EntityType.ARTEFACT)
        );
        this.assertFailParse(testExtendedFileName, startLocation,
            locations, paths, entitiesWithKeyword,
            "Parsing file with keyword in entity name throw exception"
        );
    }

    // add storeroom if not in file
    @Test
    void testAddStoreroom() {
        EntityData startLocation = new EntityData("cabin", "a log cabin in the woods");
        Set<EntityData> locationsWithoutStoreroom = Set.of(
            startLocation,
            new EntityData("forest", "a dark forest"),
            new EntityData("cellar", "a dusty cellar")
        );
        String fileName = "custom-entities.dot";
        File entitiesFile = TestUtils.makeEntitiesFile(startLocation, locationsWithoutStoreroom,
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

    boolean hasStoreroom(Set<EntityData> locations) {
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
    void assertFailParse(String fileName, EntityData startLocation, Set<EntityData> locations,
                         Set<PathData> paths, Set<InteractableEntityData> entities,
                         String errorMessage) {
        File entitiesFile = TestUtils.makeEntitiesFile(startLocation, locations, paths, entities,
            fileName);
        try {
            new EntitiesParser(entitiesFile, null);
            fail(errorMessage);
        } catch (Exception e) {
            // expected exception, test passes
            TestUtils.removeFile(fileName);
        }
    }

    // parse and assert entities from a file against expected data
    private void parseAndAssertEntities(String fileName, Set<EntityData> expectedLocations,
                                        Set<PathData> expectedPaths,
                                        Set<InteractableEntityData> expectedEntities) {
        File entitiesFile = Paths.get(TestUtils.getFilePath(fileName)).toAbsolutePath().toFile();
        try {
            EntitiesParser entitiesParser = new EntitiesParser(entitiesFile, null);
            Set<EntityData> loadedLocations = entitiesParser.getLocations();
            Set<PathData> loadedPaths = entitiesParser.getPaths();
            Set<InteractableEntityData> loadedEntities = entitiesParser.getEntities();
            assertTrue(this.checkIsEntityDataSame(loadedLocations, expectedLocations),
                "Loaded locations should exactly match expected locations");
            assertTrue(this.checkIsPathDataSame(loadedPaths, expectedPaths),
                "Loaded paths should exactly match expected paths");
            assertTrue(this.checkIsEntitiesSame(loadedEntities, expectedEntities),
                "Loaded entities should exactly match expected entities");
        } catch (Exception e) {
            fail("Should not throw exception when parsing valid file");
            return;
        }
    }

    /* Utility */
    // check if two EntityData arrays are identical regardless of order
    private boolean checkIsEntityDataSame(Set<EntityData> dataSet1, Set<EntityData> dataSet2) {
        if (dataSet1.size() != dataSet2.size()) return false;
        for (EntityData data1 : dataSet1) {
            if (!this.checkContainData(data1, dataSet2)) return false;
        }
        return true;
    }

    private boolean checkContainData(EntityData targetData, Set<EntityData> dataSet) {
        for (EntityData data : dataSet) {
            boolean isSame =
                data.getName().equals(targetData.getName()) &&
                data.getDescription().equals(targetData.getDescription());
            if (isSame) return true;
        }
        return false;
    }

    // check if two PathData arrays are identical regardless of order
    private boolean checkIsPathDataSame(Set<PathData> paths1, Set<PathData> paths2) {
        if (paths1.size() != paths2.size()) return false;
        for (PathData path1 : paths1) {
            if (!this.checkContainPath(path1, paths2)) return false;
        }
        return true;
    }

    private boolean checkContainPath(PathData targetPath, Set<PathData> pathSet) {
        for (PathData path : pathSet) {
            boolean isSame =
                path.getFromLocationName().equalsIgnoreCase(targetPath.getFromLocationName()) &&
                path.getToLocationName().equalsIgnoreCase(targetPath.getToLocationName());
            if (isSame) return true;
        }
        return false;
    }

    // check if two InteractableEntityData arrays are identical regardless of order
    private boolean checkIsEntitiesSame(Set<InteractableEntityData> entities1,
                                        Set<InteractableEntityData> entities2) {
        if (entities1.size() != entities2.size()) return false;
        for (InteractableEntityData entity1 : entities1) {
            if (!this.checkContainInteractableEntity(entity1, entities2)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkContainInteractableEntity(InteractableEntityData targetEntity,
                                                   Set<InteractableEntityData> entitySet) {
        for (InteractableEntityData entity : entitySet) {
            boolean isSame =
                entity.getName().equals(targetEntity.getName()) &&
                entity.getDescription().equals(targetEntity.getDescription()) &&
                entity.getLocationName().equals(targetEntity.getLocationName()) &&
                entity.getType() == targetEntity.getType();
            if (isSame) return true;
        }
        return false;
    }
}
