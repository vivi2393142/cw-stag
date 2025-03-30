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
    final EntityData requireLocation;

    final Set<EntityData> locations = new HashSet<>();
    final Set<PathData> paths = new HashSet<>();
    final Set<InteractableEntityData> entities = new HashSet<>();
    EntityData startLocation = null;

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
    public EntityData getStartLocation() {return this.startLocation;}
    public Set<EntityData> getLocations() {return this.locations;}
    public Set<PathData> getPaths() {return this.paths;}
    public Set<InteractableEntityData> getEntities() {return this.entities;}

    /* Utility */
    private void parseLocationAndEntity() throws UserException {
        // 1. initialize locations and entities list
        List<Graph> locations = this.entitySections.get(0).getSubgraphs();

        // 1. check has required location in file and add it if not exist
        boolean hasRequireLocation = this.checkHasRequireLocation(locations);
        if (!hasRequireLocation) this.locations.add(this.requireLocation);

        // 3. add location & entity
        this.getLocationAndEntity(locations, this.locations, this.entities);

        // 4. validate no keyword in entity name
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

    private void getLocationAndEntity(List<Graph> locations, Set<EntityData> loadedLocations,
                                      Set<InteractableEntityData> loadedEntities) {
        for (Graph locationGraph : locations) {
            // 1. add location
            Node node = locationGraph.getNodes(false).get(0);
            String name = this.getNameFromNode(node);
            String desc = this.getDescFromNode(node);
            EntityData newLocation = new EntityData(name, desc);
            loadedLocations.add(newLocation);

            // 2. if is first, set to start location
            if (this.startLocation == null) this.startLocation = newLocation;

            // 3. add entity
            List<Graph> entityGroupList = locationGraph.getSubgraphs();
            for (Graph entityGroup : entityGroupList) {
                String entityType = entityGroup.getId().getId().toLowerCase();
                this.parseEntityGroup(entityGroup, name, entityType, loadedEntities);
            }
        }
    }

    /* Utility */
    public void parsePath() {
        List<Edge> paths = this.entitySections.get(1).getEdges();
        for (Edge path : paths) {
            // 1. get from location
            Node fromLocation = path.getSource().getNode();
            String fromName = this.getNameFromNode(fromLocation);

            // 2. get to location
            Node toLocation = path.getTarget().getNode();
            String toName = this.getNameFromNode(toLocation);

            // 3. load path
            this.paths.add(new PathData(fromName, toName));
        }
    }

    // get entity names and descriptions from entity graph
    private void parseEntityGroup(Graph entityGroup, String locationName, String type,
                                  Set<InteractableEntityData> loadedEntities) {
        List<Node> nodes = entityGroup.getNodes(false);
        for (Node node : nodes) {
            InteractableEntityData entityData =
                new InteractableEntityData(
                    this.getNameFromNode(node),
                    this.getDescFromNode(node),
                    locationName, this.getEntityType(type));
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
    private <T extends EntityData> void validateNoKeyword(Set<T> entityDataList)
        throws UserException {
        for (T entity : entityDataList) {
            if (ValueUtils.matchesAnyKeyword(entity.getName())) {
                throw new UserException("Cannot use keyword in entity name");
            }
        }
    }
}
