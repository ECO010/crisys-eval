package com.example.securityevaluationtool;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class PDFGenerator {

    public static void generatePDF(String xmlFilePath, String outputFilePath, PageSize pageSize) {
        try {
            // Load XML file
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document xmlDoc = dBuilder.parse(xmlFile);
            xmlDoc.getDocumentElement().normalize();

            // Create PDF document
            PdfWriter writer = new PdfWriter(outputFilePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document pdfDocument = new Document(pdf, pageSize);

            // Set to track visited nodes
            Set<String> visitedNodes = new HashSet<>();


            // Draw tree from XML
            drawTreeFromXML(xmlDoc.getDocumentElement(), pdfDocument, 0, visitedNodes);

            // Close PDF document
            pdfDocument.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void drawTreeFromXML(Element element, Document pdfDocument, int indentationLevel, Set<String> visitedNodes) {
        NodeList nodes = element.getElementsByTagName("node");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element nodeElement = (Element) nodes.item(i);
            String nodeName = nodeElement.getElementsByTagName("name").item(0).getTextContent();

            // Check if the node has been visited already
            if (!visitedNodes.contains(nodeName)) {
                // Mark the node as visited
                visitedNodes.add(nodeName);

                Paragraph paragraph = new Paragraph();
                // Add indentation based on the current level
                for (int j = 0; j < indentationLevel; j++) {
                    paragraph.add("->");
                }
                // Add the node name to the paragraph
                paragraph.add(nodeName);
                // Add the paragraph to the PDF doc
                pdfDocument.add(paragraph);

                NodeList children = nodeElement.getElementsByTagName("children");
                if (children.getLength() > 0) {
                    // Recursively draw children with increased indentation level
                    for (int j = 0; j < children.getLength(); j++) {
                        Element childElement = (Element) children.item(j);
                        drawTreeFromXML(childElement, pdfDocument, indentationLevel + 1, visitedNodes);
                    }
                }
            }
        }
    }
}
