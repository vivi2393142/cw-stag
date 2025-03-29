package edu.uob.parser;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.EntityType;
import edu.uob.ValueUtils;
import edu.uob.data.EntityData;
import edu.uob.data.InteractableEntityData;
import edu.uob.data.PathData;
import edu.uob.exception.UserException;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class EntitiesParser {
    List<Graph> entitySections;
    EntityData requireLocation;

    EntityData[] locations;
    PathData[] paths;
    InteractableEntityData[] entities;

    // attribute keywords used in entity file
    private enum Tag {
        DESCRIPTION("description"), ARTEFACTS("artefacts"),
        FURNITURE("furniture"), CHARACTERS("characters");

        private final String value;
        Tag(String value) {this.value = value;}
    }

    public EntitiesParser(File entitiesFile, EntityData requireLocation) throws Exception {
        this.requireLocation = requireLocation;
        this.entitySections = this.getSectionsFromReader(entitiesFile);
        this.parseFile();
    }

    // get list of graph from file reader
    private List<Graph> getSectionsFromReader(File entitiesFile) throws Exception {
        FileReader reader = new FileReader(entitiesFile);

        Parser parser = new Parser();
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        return wholeDocument.getSubgraphs();
    }

    private void parseFile() throws UserException {
        this.parseLocationAndEntity();
        this.parsePath();
    }

    /* Getter */
    public EntityData[] getLocations() {return this.locations;}
    public PathData[] getPaths() {return this.paths;}
    public InteractableEntityData[] getEntities() {return this.entities;}

    /* Utility */
    private void parseLocationAndEntity() throws UserException {
        // 1-1. check has required location in file
        List<Graph> locations = this.entitySections.get(0).getSubgraphs();
        boolean hasRequireLocation = this.checkHasRequireLocation(locations);
        // 1-2. get location list size based on hasRequireLocation
        int locationCtn = locations.size();
        if (!hasRequireLocation) locationCtn++;

        // 2-1. initialize locations and entities list
        EntityData[] loadedLocations = new EntityData[locationCtn];
        Set<InteractableEntityData> loadedEntities = new HashSet<>();
        // 2-2 add require location in if needed
        if (!hasRequireLocation) loadedLocations[locationCtn - 1] = this.requireLocation;

        // 3. add location & entity
        this.getLocationAndEntity(locations, loadedLocations, loadedEntities);

        // 4. load in parsed data
        this.locations = loadedLocations;
        this.entities = loadedEntities.toArray(new InteractableEntityData[0]);

        // 5. validate no keyword in entity name
        this.validateNoKeyword(this.locations);
        this.validateNoKeyword(this.entities);
    }

    private boolean checkHasRequireLocation(List<Graph> locations) {
        // return true if no need to check
        if (this.requireLocation == null) return true;

        // if name equal to requireLocation, return true
        String requireLocationName = this.requireLocation.getName();
        for (Graph locationGraph : locations) {
            Node node = locationGraph.getNodes(false).get(0);
            String locationName = this.getNameFromNode(node);
            if (locationName.equalsIgnoreCase(requireLocationName)) return true;
        }

        // no require location in list
        return false;
    }

    private void getLocationAndEntity(List<Graph> locations, EntityData[] loadedLocations,
                                      Set<InteractableEntityData> loadedEntities) {
        for (int i = 0; i < locations.size(); i++) {
            // 1. add location
            Graph location = locations.get(i);
            Node node = location.getNodes(false).get(0);
            String locationName = this.getNameFromNode(node);
            String locationDesc = this.getDescFromNode(node);
            loadedLocations[i] = new EntityData(locationName, locationDesc);

            // 2. add entity
            List<Graph> entityGroupList = location.getSubgraphs();
            for (Graph entityGroup : entityGroupList) {
                String entityType = entityGroup.getId().getId().toLowerCase();
                this.parseEntityGroup(entityGroup, locationName, entityType, loadedEntities);
            }
        }
    }

    /* Utility */
    public void parsePath() {
        List<Edge> paths = this.entitySections.get(1).getEdges();
        int pathSize = paths.size();

        PathData[] loadedPaths = new PathData[pathSize];
        for (int i = 0; i < pathSize; i++) {
            Edge path = paths.get(i);

            // 1. get from location
            Node fromLocation = path.getSource().getNode();
            String fromName = this.getNameFromNode(fromLocation);

            // 2. get to location
            Node toLocation = path.getTarget().getNode();
            String toName = this.getNameFromNode(toLocation);

            // 3. load path
            loadedPaths[i] = new PathData(fromName, toName);
        }
        this.paths = loadedPaths;
    }

    // get entity names and descriptions from entity graph
    private void parseEntityGroup(Graph entityGroup, String locationName, String type,
                                  Set<InteractableEntityData> loadedEntities) {
        List<Node> nodes = entityGroup.getNodes(false);
        for (Node node : nodes) {
            String name = this.getNameFromNode(node);
            String desc = this.getDescFromNode(node);

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

    private String getNameFromNode(Node node) {
        return node.getId().getId().toLowerCase();
    }

    private String getDescFromNode(Node node) {
        return node.getAttribute(Tag.DESCRIPTION.value).toLowerCase();
    }

    /* Validate */
    private void validateNoKeyword(EntityData[] entityDataList) throws UserException {
        for (EntityData entity : entityDataList) {
            if (ValueUtils.matchesAnyKeyword(entity.getName())) {
                throw new UserException("Cannot use keyword in entity name");
            }
        }
    }
}
