package edu.uob.custom;

import edu.uob.EntityType;
import edu.uob.GameAction;
import edu.uob.data.EntityData;
import edu.uob.data.InteractableEntityData;
import edu.uob.data.PathData;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;

public class TestUtils {
    private TestUtils() {}

    /* File */
    public static String getFilePath(String fileName) {
        return new StringBuilder()
            .append("config").append(File.separator).append(fileName).toString();
    }

    // make actions file
    public static File makeActionsFile(Set<GameAction> actions, String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<actions>\n");
        for (GameAction action : actions) {
            sb.append("<action>\n");

            sb.append(makeActionAttrContent(action.getTriggers(),
                "triggers", "keyphrase", null));
            sb.append(makeActionAttrContent(action.getSubjects(),
                "subjects", "entity", null));

            sb.append(makeActionAttrContent(action.getConsumedEntities(),
                "consumed", "entity",
                makeHealthAttrContent(action.getConsumedHealth())));
            sb.append(makeActionAttrContent(action.getProducedEntities(),
                "produced", "entity",
                makeHealthAttrContent(action.getProducedHealth())));

            sb.append("<narration>\n");
            sb.append(action.getNarration());
            sb.append("</narration>\n");
            sb.append("</action>\n");
        }
        sb.append("</actions>\n");

        try {
            String filePath = getFilePath(new StringBuilder().append(fileName).toString());
            File file = new File(filePath);
            Files.write(Paths.get(filePath), sb.toString().getBytes());
            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public static String makeActionAttrContent(Set<String> attributes, String attrGroupName,
                                               String attrName, String otherAttrContent) {
        StringBuilder sb = new StringBuilder();

        sb.append("<").append(attrGroupName).append(">\n");
        for (String attr : attributes) {
            sb.append("<").append(attrName).append(">")
                .append(attr)
                .append("</").append(attrName).append(">\n");
        }
        if (otherAttrContent != null) sb.append(otherAttrContent);
        sb.append("</").append(attrGroupName).append(">\n");

        return sb.toString();
    }

    public static String makeHealthAttrContent(int health) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < health; i++) {
            sb.append("<entity>health</entity>");
        }
        return sb.toString();
    }

    // make entities file
    public static File makeEntitiesFile(EntityData startLocation,
                                        Set<EntityData> locations, Set<PathData> paths,
                                        Set<InteractableEntityData> entities, String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph layout {\n")
            .append("splines = ortho;\n")
            .append("graph [ranksep=\"1\" nodesep=\"1\"];\n")
            .append("node [shape = \"rect\"];\n")
            .append("subgraph locations {\n");

        // process each location and its entities
        appendLocationGroup(sb, startLocation, 0, entities);
        int idx = 1;
        for (EntityData location : locations) {
            if (!location.getName().equalsIgnoreCase(startLocation.getName())) {
                appendLocationGroup(sb, location, idx, entities);
                idx++;
            }
        }
        sb.append("}\n"); // close locations

        // paths
        sb.append("subgraph paths {\n");
        for (PathData path : paths) {
            sb.append(path.getFromLocationName()).append(" -> ")
                .append(path.getToLocationName()).append(";\n");
        }
        sb.append("}\n"); // close paths
        sb.append("}\n"); // close digraph

        // write to file
        try {
            String filePath = getFilePath(fileName);
            File file = new File(filePath);
            Files.write(Paths.get(filePath), sb.toString().getBytes());
            return file;
        } catch (Exception e) {
            System.err.println(
                new StringBuilder("Failed to write entities file: ").append(e.getMessage()));
            return null;
        }
    }

    private static void appendLocationGroup(StringBuilder sb, EntityData location, int idx,
                                            Set<InteractableEntityData> entities) {
        String locName = location.getName();

        sb.append("subgraph cluster").append(idx).append(" {\n")
            .append("node [shape = \"none\"];\n")
            .append(locName)
            .append(" [description = \"").append(location.getDescription()).append("\"];\n");

        // artefacts
        sb.append("subgraph artefacts {\n")
            .append("node [shape = \"diamond\"];\n");
        appendEntitiesByType(sb, entities, locName, EntityType.ARTEFACT);
        sb.append("}\n");

        // furniture
        sb.append("subgraph furniture {\n")
            .append("node [shape = \"hexagon\"];\n");
        appendEntitiesByType(sb, entities, locName, EntityType.FURNITURE);
        sb.append("}\n");

        // characters
        sb.append("subgraph characters {\n")
            .append("node [shape = \"ellipse\"];\n");
        appendEntitiesByType(sb, entities, locName, EntityType.CHARACTER);
        sb.append("}\n");

        sb.append("}\n"); // close cluster
    }

    private static void appendEntitiesByType(StringBuilder sb, Set<InteractableEntityData> entities,
                                             String locationName, EntityType type) {
        for (InteractableEntityData entity : entities) {
            if (entity.getType() == type && Objects.equals(entity.getLocationName(), locationName)) {
                sb.append(entity.getName())
                    .append(" [description = \"").append(entity.getDescription()).append("\"];\n");
            }
        }
    }

    // other
    public static void removeFile(String fileName) {
        try {
            String filePath = getFilePath(new StringBuilder().append(fileName).toString());
            Files.deleteIfExists(Paths.get(filePath));
        } catch (Exception e) {}
    }
}
