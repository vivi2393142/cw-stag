package edu.uob.parser;

import edu.uob.GameAction;
import edu.uob.Keyword;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ActionsParser {
    NodeList actionNodes;
    GameAction[] actions;

    private enum Tag {
        TRIGGERS("triggers"), SUBJECTS("subjects"), CONSUMED("consumed"),
        PRODUCED("produced"), NARRATION("narration"), KEYPHRASE("keyphrase"),
        ENTITY("entity");

        private final String value;
        Tag(String value) {this.value = value;}
    }

    public ActionsParser(File actionsFile) throws Exception {
        this.actionNodes = this.getActionNodesFromFile(actionsFile);
        this.parse();
    }

    private NodeList getActionNodesFromFile(File actionsFile) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(actionsFile);
        Element root = document.getDocumentElement();
        return root.getChildNodes();
    }

    private void parse() {
        Set<GameAction> actions = new HashSet<>();

        // only the odd items are actually actions
        for (int i = 1; i < actionNodes.getLength(); i += 2) {
            Element action = (Element) this.actionNodes.item(i);

            Set<String> triggers = this.getAttrFromAction(action, Tag.TRIGGERS, Tag.KEYPHRASE);
            Set<String> subjects = this.getAttrFromAction(action, Tag.SUBJECTS, Tag.ENTITY);
            String narration = action.getElementsByTagName(Tag.NARRATION.value)
                .item(0).getTextContent().toLowerCase();

            Set<String> consumed = this.getAttrFromAction(action, Tag.CONSUMED, Tag.ENTITY);
            int consumedHealth = this.retrieveHealth(consumed); // retrieve health from consumed

            Set<String> produced = this.getAttrFromAction(action, Tag.PRODUCED, Tag.ENTITY);
            int producedHealth = this.retrieveHealth(produced); // retrieve health from produced

            GameAction newAction = new GameAction(triggers, subjects,
                consumedHealth, producedHealth, consumed, produced, narration);
            actions.add(newAction);
        }

        this.actions = actions.toArray(new GameAction[0]);
    }

    /* Getter */
    public GameAction[] getActions() {return this.actions;}

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

    private int retrieveHealth(Set<String> targetSet) {
        String keyword = Keyword.HEALTH.name().toLowerCase();
        if (targetSet.contains(keyword)) {
            targetSet.remove(keyword);
            return 1;
        }
        return 0;
    }
}
