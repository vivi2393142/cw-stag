package edu.uob.parser;

import edu.uob.GameAction;
import edu.uob.Keyword;
import edu.uob.ValueUtils;
import edu.uob.exception.UserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

// parse xml actions file
public class ActionsParser {
    Set<GameAction> actions = new HashSet<>();

    // attribute keywords used in action file
    private enum Tag {
        TRIGGERS("triggers"), SUBJECTS("subjects"), CONSUMED("consumed"),
        PRODUCED("produced"), NARRATION("narration"), KEYPHRASE("keyphrase"),
        ENTITY("entity");

        private final String value;
        Tag(String value) {this.value = value;}
    }

    public ActionsParser(File actionsFile) throws Exception {
        NodeList actionNodes = this.getActionNodesFromFile(actionsFile); // get xml nodes
        this.parseActions(actionNodes); // parse nodes to action
    }

    // get nodes from xml file
    private NodeList getActionNodesFromFile(File actionsFile) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(actionsFile);
        Element root = document.getDocumentElement();
        return root.getChildNodes();
    }

    private void parseActions(NodeList nodes) throws UserException {
        // only the odd items are actually actions
        for (int i = 1; i < nodes.getLength(); i += 2) {
            Element action = (Element) nodes.item(i);
            // 1. get action attributes
            Set<String> triggers = this.getAttrFromAction(action, Tag.TRIGGERS, Tag.KEYPHRASE);
            Set<String> subjects = this.getAttrFromAction(action, Tag.SUBJECTS, Tag.ENTITY);
            String narration = action.getElementsByTagName(Tag.NARRATION.value)
                .item(0).getTextContent().toLowerCase();
            Set<String> consumed = this.getAttrFromAction(action, Tag.CONSUMED, Tag.ENTITY);
            int consumedHealth = this.retrieveHealth(consumed); // retrieve health from consumed

            Set<String> produced = this.getAttrFromAction(action, Tag.PRODUCED, Tag.ENTITY);
            int producedHealth = this.retrieveHealth(produced); // retrieve health from produced
            // 2. check no keyword in attributes
            this.validateNoKeyword(triggers);
            this.validateNoKeyword(subjects);
            this.validateNoKeyword(consumed);
            this.validateNoKeyword(produced);
            // 3. turn into GameAction
            GameAction newAction = new GameAction(triggers, subjects,
                consumedHealth, producedHealth, consumed, produced, narration);
            this.actions.add(newAction);
        }
    }

    /* Getter */
    public Set<GameAction> getActions() {return this.actions;}

    /* Utility */
    private Set<String> getAttrFromAction(Element action, Tag parent, Tag child) {
        Set<String> items = new HashSet<>();
        Element parentNode = (Element) action.getElementsByTagName(parent.value).item(0);
        if (parentNode == null) return items;

        NodeList childNodeList = parentNode.getElementsByTagName(child.value);
        for (int j = 0; j < childNodeList.getLength(); j++) {
            String item = childNodeList.item(j).getTextContent().trim().toLowerCase();
            items.add(item);
        }

        return items;
    }

    // get health attribute from 'entity' in action file
    private int retrieveHealth(Set<String> targetSet) {
        String keyword = Keyword.HEALTH.name().toLowerCase();
        if (targetSet.contains(keyword)) {
            targetSet.remove(keyword);
            return 1;
        }
        return 0;
    }

    /* Validate */
    private void validateNoKeyword(Set<String> inputList) throws UserException {
        for (String input : inputList) {
            if (ValueUtils.matchesAnyKeyword(input)) {
                throw new UserException("Cannot use keyword in action");
            }
        }
    }
}
