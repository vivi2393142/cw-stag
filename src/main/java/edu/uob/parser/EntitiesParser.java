package edu.uob.parser;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.EntityType;
import edu.uob.data.EntityData;
import edu.uob.data.InteractableEntityData;
import edu.uob.data.PathData;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class EntitiesParser {
    List<Graph> entitySections;
    EntityData requireLocation;

    EntityData[] locations;
    PathData[] paths;
    InteractableEntityData[] entities;

    private enum Tag {
        DESCRIPTION("description"), ARTEFACTS("artefacts"),
        FURNITURE("furniture"), CHARACTERS("characters");

        private final String value;
        Tag(String value) {this.value = value;}
    }

    public EntitiesParser(File entitiesFile, EntityData requireLocation) throws Exception {
        this.requireLocation = requireLocation;
        this.entitySections = this.getSectionsFromReader(entitiesFile);
        this.parse();
    }

    private List<Graph> getSectionsFromReader(File entitiesFile) throws Exception {
        FileReader reader = new FileReader(entitiesFile);

        Parser parser = new Parser();
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        return wholeDocument.getSubgraphs();
    }

    private void parse() {
        this.parseLocationAndEntity();
        this.parsePath();
    }

    /* Getter */
    public EntityData[] getLocations() {return this.locations;}
    public PathData[] getPaths() {return this.paths;}
    public InteractableEntityData[] getEntities() {return this.entities;}

    /* Utility */
    private void parseLocationAndEntity() { // TODO: refactor, reduce length
        // 1. initialize locations
        List<Graph> locations = this.entitySections.get(0).getSubgraphs();
        int locationSize = locations.size();

        EntityData[] loadedLocations = new EntityData[locationSize];
        Set<InteractableEntityData> loadedEntities = new HashSet<>();

        // 2. add location & entity
        for (int i = 0; i < locationSize; i++) {
            // 2-1. add location
            Graph location = locations.get(i);
            Node node = location.getNodes(false).get(0);
            String locationName = node.getId().getId().toLowerCase();
            String locationDesc = node.getAttribute(Tag.DESCRIPTION.value).toLowerCase();
            loadedLocations[i] = new EntityData(locationName, locationDesc);

            // 2-2. add entity
            List<Graph> entityGroupList = location.getSubgraphs();
            for (Graph entityGroup : entityGroupList) {
                String entityType = entityGroup.getId().getId().toLowerCase();
                this.parseEntityGroup(entityGroup, locationName, entityType, loadedEntities);
            }
        }

        // 3. if no requireLocation, add it
        if (!Arrays.asList(loadedLocations).contains(this.requireLocation)) {
            loadedLocations = Arrays.copyOf(loadedLocations, loadedLocations.length + 1);
            loadedLocations[loadedLocations.length - 1] = this.requireLocation;
        }

        this.locations = loadedLocations;
        this.entities = loadedEntities.toArray(new InteractableEntityData[0]);
    }

    private void parseEntityGroup(Graph entityGroup, String locationName, String type,
                                  Set<InteractableEntityData> loadedEntities) {
        List<Node> nodes = entityGroup.getNodes(false);
        for (Node node : nodes) {
            String name = node.getId().getId().toLowerCase();
            String desc = node.getAttribute(Tag.DESCRIPTION.value).toLowerCase();

            InteractableEntityData entityData =
                new InteractableEntityData(name, desc, locationName, this.getEntityType(type));
            loadedEntities.add(entityData);
        }
    }

    private EntityType getEntityType(String entityType) {
        if (Objects.equals(entityType, Tag.ARTEFACTS.value)) return EntityType.ARTEFACT;
        if (Objects.equals(entityType, Tag.FURNITURE.value)) return EntityType.FURNITURE;
        if (Objects.equals(entityType, Tag.CHARACTERS.value)) return EntityType.CHARACTER;
        return null;
    }

    public void parsePath() {
        List<Edge> paths = this.entitySections.get(1).getEdges();
        int pathSize = paths.size();

        PathData[] loadedPaths = new PathData[pathSize];
        for (int i = 0; i < pathSize; i++) {
            Edge path = paths.get(i);
            Node fromLocation = path.getSource().getNode();
            String fromName = fromLocation.getId().getId().toLowerCase();

            Node toLocation = path.getTarget().getNode();
            String toName = toLocation.getId().getId().toLowerCase();

            loadedPaths[i] = new PathData(fromName, toName);
        }
        this.paths = loadedPaths;
    }
}
