package com.company;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;

public class Main {

    /**
     * Args Definition:
     *  - 0: path to base.xml file to be edited
     *  - 1: path to folder containing the split_apk.xml files
     *  - 2: char for whether to delete or replace. 'd' for delete, 'r' for replace
     * @param args
     */
    public static void main(String[] args) {
        String baseXmlFilePath = args[0];
        String splitXmlFolderPath = args[1];
        boolean delete = args[2].equals("d");

        System.out.println("Base apk xml path: "+baseXmlFilePath+"\n" +
                "Split xml folder path: "+splitXmlFolderPath+"\n" +
                "Delete?: "+delete+" = "+args[2]);

        try {
            Document doc = XmlIO.readXmlFromFile(baseXmlFilePath);

            // Get the components for both base and split xml files
            FilenameFilter xmlFileFilter = (dir, name) -> {
                if (name.endsWith(".xml"))
                    return true;

                return false;
            };
            File[] files = (new File(splitXmlFolderPath)).listFiles(xmlFileFilter);

            ArrayList<String> filePaths = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                filePaths.add(files[i].getPath());
            }
            ArrayList<Node> splitComponents = XmlService.getAllComponentsFromSplits(filePaths);
            ArrayList<Node> baseComponents = XmlService.getNodes(doc, "Component");

            if (delete) {
                deleteComponents(baseComponents, splitComponents, doc, baseXmlFilePath);
            } else {
                replaceComponents(baseComponents, splitComponents, doc, baseXmlFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void replaceComponents(ArrayList<Node> baseComponents, ArrayList<Node> splitComponents, Document doc, String baseXmlPath){
        try {
            System.out.println("Number of Components in Base Apk Before Replacing: "+baseComponents.size());
            System.out.println("Number of Components in all Split Apks: "+splitComponents.size());

            XmlService.replaceDuplicateComponents(baseComponents,splitComponents);

            NodeList nList = doc.getDocumentElement().getElementsByTagName("Component");

            System.out.println("Number of Components in Base Apk After Replacing (Should be same): "+nList.getLength());

            XmlIO.writeXmlToFile(doc, baseXmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteComponents(ArrayList<Node> baseComponents, ArrayList<Node> splitComponents, Document doc, String baseXmlPath){
        try {
            System.out.println("Number of Components in Base Apk Before Removing: "+baseComponents.size());
            System.out.println("Number of Components in all Split Apks: "+splitComponents.size());

            XmlService.deleteDuplicateComponents(baseComponents,splitComponents);

            NodeList nList = doc.getDocumentElement().getElementsByTagName("Component");

            System.out.println("Number of Components in Base Apk After Removing: "+nList.getLength());

            XmlIO.writeXmlToFile(doc, baseXmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
