package com.example.securityevaluationtool;

import javafx.scene.control.TreeItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class XMLGenerator {

    public static void generateXML(TreeItem<String> rootNode, String outputFilePath) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("attack-tree");
            doc.appendChild(rootElement);

            generateXMLRecursive(doc, rootElement, rootNode);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(outputFilePath));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateXMLRecursive(Document doc, Element parent, TreeItem<String> treeItem) {
        Element elementNode = doc.createElement("node");
        parent.appendChild(elementNode);

        Element nameElement = doc.createElement("name");
        nameElement.setTextContent(treeItem.getValue());
        elementNode.appendChild(nameElement);

        if (treeItem.getChildren().isEmpty()) {
            // Leaf node
            return;
        }

        Element childrenElement = doc.createElement("children");
        elementNode.appendChild(childrenElement);

        for (TreeItem<String> childItem : treeItem.getChildren()) {
            generateXMLRecursive(doc, childrenElement, childItem);
        }
    }
}