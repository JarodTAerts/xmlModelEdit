package com.company;
import org.w3c.dom.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

class Main {

    /**
     * Args Definition:
     *  - 0: path to base.xml file to be edited
     *  - 1: path to folder containing the split_apk.xml files
     *  - 2: char for whether to delete or replace. 'd' for delete, 'r' for replace
     * @param args Command line args passed into program
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            printHelp();
            System.exit(1);
        }

        String baseXmlFilePath = args[0];
        String splitXmlFolderPath = args[1];
        boolean delete = args[2].equals("d");

        try {
            Document doc = XmlIO.readXmlFromFile(baseXmlFilePath);

            // Get the components for both base and split xml files
            FilenameFilter xmlFileFilter = (dir, name) -> name.endsWith(".xml");
            File[] files = (new File(splitXmlFolderPath)).listFiles(xmlFileFilter);

            ArrayList<String> filePaths = new ArrayList<>();
            for (File file : Objects.requireNonNull(files)) {
                filePaths.add(file.getPath());
            }
            ArrayList<Node> splitComponents = XmlService.getAllComponentsFromSplits(filePaths);
            ArrayList<Node> baseComponents = XmlService.getNodes(doc, "Component");

            // Either delete or replace components based on the command line arguments
            if (delete) {
                deleteComponents(baseComponents, splitComponents, doc, baseXmlFilePath);
            } else {
                replaceComponents(baseComponents, splitComponents, doc, baseXmlFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            printHelp();
            System.exit(1);
        }
    }

    private static void printHelp() {
        System.out.println("This is a command line tool to adjust the merged xml models of android apks following the" +
                "Android App Bundle (AAB) Paradigm. This tool removes or replaces the redundant component definitions in the base.xml" +
                "of the AAB base on the concrete definitions which are in the split_feature.xml files. In order to run this program you must" +
                "provide at least 3 parameters in the following order. These are not named parameters:\n" +
                "\t1. File path to base.xml file to be edited\n" +
                "\t2. File path to folder containing all the split_apk.xml files for the base apk\n" +
                "\t3. Either 'd' or 'r' which tells the tool whether to delete the components in the base.xml or to replace them.\n\n" +
                "Example call: java xmlModelEdit ./base.xml ./splitXmlFiles d\n\n" +
                "After running this tool will have rewritten the base.xml file with the deleted or replaced Component declarations.\n\n" +
                "Thank you for using this tool.");
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
            printHelp();
            System.exit(1);
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
            printHelp();
            System.exit(1);
        }
    }
}
