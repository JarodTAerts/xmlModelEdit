package com.company;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * This is a static service class that reads and writes JDOM xml Documents from and to disk
 */
class XmlIO {
    /**
     * Read the xml document from a file
     *
     * @param filePath Path to xml file to read
     * @return JDOM xml Document
     * @throws Exception IO exception probably
     */
    static Document readXmlFromFile(String filePath) throws Exception {
        File inputFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(inputFile);
    }

    /**
     * Write JDOM xml document to file
     *
     * @param doc      JDOM xml document to write
     * @param filePath Path of file to write to
     * @throws Exception IO probably
     */
    static void writeXmlToFile(Document doc, String filePath) throws Exception {
        Transformer tr = TransformerFactory.newInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty(OutputKeys.METHOD, "xml");

        // send DOM to file
        tr.transform(new DOMSource(doc),
                new StreamResult(new FileOutputStream(filePath)));
    }
}
