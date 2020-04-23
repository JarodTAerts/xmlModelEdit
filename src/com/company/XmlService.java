package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This is a static service class which deals with getting information about and transforming JDOM xml documents
 */
class XmlService {
    /**
     * Looks through JDOM document and gets a workable list of Nodes with a particular name
     *
     * @param doc         Document to search thorough
     * @param elementName Name of element to grab (i.e. 'Component')
     * @return List of JDOM Nodes
     */
    static ArrayList<Node> getNodes(Document doc, String elementName) {
        ArrayList<Node> nodes = new ArrayList<>();
        NodeList nList = doc.getDocumentElement().getElementsByTagName(elementName);
        for(int i = 0; i<nList.getLength(); i++){
            nodes.add(nList.item(i));
        }

        return nodes;
    }

    /**
     * Checks and sees if there is a Component Node type with a particular name tag in a List of Nodes
     * and returns it
     *
     * @param list     List of nodes to search through
     * @param nodeName Name to search for in component name tag
     * @return Node if found, null if not
     */
    private static Node getComponentFromNodeList(ArrayList<Node> list, String nodeName) {
        for (Node node : list) {
            if (nodeName.equals(Objects.requireNonNull(getChild(node, "name")).getTextContent())) {
                return node;
            }
        }
        return null;
    }

    /**
     * Checks and sees if there is a Component Node type with a particular name tag in a List of Nodes
     *
     * @param list     List of nodes to search through
     * @param nodeName Name to search for in component name tag
     * @return true, false if node is found
     */
    private static boolean listContainsComponent(ArrayList<Node> list, String nodeName) {
        for (Node node : list) {
            if (nodeName.equals(Objects.requireNonNull(getChild(node, "name")).getTextContent())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a child of a node based on that child's element name
     *
     * @param parent Parent node
     * @return Node if child found, else null
     */
    private static Node getChild(Node parent, String name) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (name.equals(child.getNodeName())) {
                return child;
            }
        }
        return null;
    }

    /**
     * Removes a node from the JDOM document along with the extra whitespace left behind
     *
     * @param node Node to remove from the document
     */
    private static void removeNode(Node node) {
        Node parent = node.getParentNode();

        // Try to remove whitespace before and after
        Node prev = node.getPreviousSibling();
        Node next = node.getNextSibling();
        if (prev != null &&
                prev.getNodeType() == Node.TEXT_NODE &&
                prev.getNodeValue().trim().length() == 0) {
            parent.removeChild(prev);
        }
        if (next != null &&
                next.getNodeType() == Node.TEXT_NODE &&
                next.getNodeValue().trim().length() == 0) {
            parent.removeChild(next);
        }

        // Remove actual node we want to
        parent.removeChild(node);
    }

    /**
     * Looks through two lists of nodes and deletes Component nodes from first list if they are found in the second
     *
     * @param baseComponents  List of Component nodes from base.xml file of AAB
     * @param splitComponents List of Component nodes from all split_features.xml files of AAB
     */
    static void deleteDuplicateComponents(ArrayList<Node> baseComponents, ArrayList<Node> splitComponents) {
        // We have to go through the list backwards since we are removing things while we go
        for (int i = baseComponents.size() - 1; i >= 0; i--) {
            Node currentNode = baseComponents.get(i);
            String nodeName = Objects.requireNonNull(getChild(currentNode, "name")).getTextContent();
            if(listContainsComponent(splitComponents,nodeName)){
                // Remove the node if it is found in any of the split dynamic features
                removeNode(currentNode);
                baseComponents.remove(i);
            }
        }
    }

    /**
     * Looks through two lists of nodes and replaces Component nodes from first list if they are found in the second
     * Replaces those in first with value of those found in second
     *
     * @param baseComponents  List of Component nodes from base.xml file of AAB
     * @param splitComponents List of Component nodes from all split_features.xml files of AAB
     */
    static void replaceDuplicateComponents(ArrayList<Node> baseComponents, ArrayList<Node> splitComponents) {
        // We have to go through the list backwards since we are removing things while we go
        for (int i = baseComponents.size() - 1; i >= 0; i--) {
            Node currentNode = baseComponents.get(i);
            String nodeName = Objects.requireNonNull(getChild(currentNode, "name")).getTextContent();
            Node splitNode = getComponentFromNodeList(splitComponents,nodeName);
            if(splitNode != null){
                // Remove the node if it is found and replace it with the one found in the split xml
                Node parentNode = currentNode.getParentNode();
                Document doc = parentNode.getOwnerDocument();

                removeNode(currentNode);
                baseComponents.remove(i);

                Node importedNode = doc.importNode(splitNode, true);
                parentNode.appendChild(importedNode);
                baseComponents.add(splitNode);
            }
        }
    }

    /**
     * Finds all the Component nodes from multiple xml files and adds them into one list.
     *
     * @param filePaths List of paths of xml files to extract Component nodes from
     * @return List of nodes holding Components
     * @throws Exception IO Exception probably
     */
    static ArrayList<Node> getAllComponentsFromSplits(ArrayList<String> filePaths) throws Exception {
        ArrayList<Node> bigList = new ArrayList<>();
        for (String path : filePaths) {
            Document doc = XmlIO.readXmlFromFile(path);
            ArrayList<Node> theseCompeonts = getNodes(doc, "Component");

            bigList.addAll(theseCompeonts);
        }
        return bigList;
    }
}
