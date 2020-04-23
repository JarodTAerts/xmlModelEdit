package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

public class XmlService {
    public static ArrayList<Node> getNodes(Document doc, String componentType){
        ArrayList<Node> nodes = new ArrayList<Node>();
        NodeList nList = doc.getDocumentElement().getElementsByTagName(componentType);
        for(int i = 0; i<nList.getLength(); i++){
            nodes.add(nList.item(i));
        }

        return nodes;
    }

    public static Node getComponentFromNodeList(ArrayList<Node> list, String nodeName){
        for(int i = 0; i<list.size(); i++){
            Node node = list.get(i);
            if(nodeName.equals(getChild(node, "name").getTextContent())){
                return node;
            }
        }
        return null;
    }

    public static boolean listContainsComponent(ArrayList<Node> list, String nodeName){
        for(int i = 0; i<list.size(); i++){
            Node node = list.get(i);
            if(nodeName.equals(getChild(node, "name").getTextContent())){
                return true;
            }
        }
        return false;
    }

    public static Node getChild(Node parent, String name) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (name.equals(child.getNodeName())) {
                return child;
            }
        }
        return null;
    }

    public static void deleteDuplicateComponents(ArrayList<Node> baseComponents, ArrayList<Node> splitComponents){
        // We have to go through the list backwards since we are removing things while we go
        for(int i=baseComponents.size()-1; i>=0; i--){
            Node currentNode = baseComponents.get(i);
            String nodeName = getChild(currentNode, "name").getTextContent();
            if(listContainsComponent(splitComponents,nodeName)){
                // Remove the node if it is found in any of the split dynamic features
                currentNode.getParentNode().removeChild(currentNode);
                baseComponents.remove(i);
            }
        }
    }

    public static void replaceDuplicateComponents(ArrayList<Node> baseComponents, ArrayList<Node> splitComponents){
        // We have to go through the list backwards since we are removing things while we go
        for(int i=baseComponents.size()-1; i>=0; i--){
            Node currentNode = baseComponents.get(i);
            String nodeName = getChild(currentNode, "name").getTextContent();
            Node splitNode = getComponentFromNodeList(splitComponents,nodeName);
            if(splitNode != null){
                // Remove the node if it is found and replace it with the one found in the split xml
                Node parentNode = currentNode.getParentNode();
                Document doc = parentNode.getOwnerDocument();

                parentNode.removeChild(currentNode);
                baseComponents.remove(i);

                Node importedNode = doc.importNode(splitNode, true);
                parentNode.appendChild(importedNode);
                baseComponents.add(splitNode);
            }
        }
    }

    public static ArrayList<Node> getAllComponentsFromSplits(ArrayList<String> filePaths) throws Exception{
        ArrayList<Node> bigList = new ArrayList<>();
        for (String path : filePaths) {
                Document doc = XmlIO.readXmlFromFile(path);
                ArrayList<Node> theseCompeonts = getNodes(doc, "Component");

                for(int i=0; i<theseCompeonts.size(); i++) {
                    bigList.add(theseCompeonts.get(i));
                }
        }
        return bigList;
    }
}
