package com.example.securityevaluationtool.database;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AttackCategory {
    private String categoryName;
    private int categoryId;
    private List<Integer> capecIds;
    private String categorySummary;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public List<Integer> getCapecIds() {
        return capecIds;
    }

    public void setCapecIds(List<Integer> capecIds) {
        this.capecIds = capecIds;
    }

    public String getCategorySummary() {
        return categorySummary;
    }

    public void setCategorySummary(String categorySummary) {
        this.categorySummary = categorySummary;
    }

    public List<AttackCategory> parseXMLDataFromCAPEC() {
        List<AttackCategory> attackCategoriesToSave = new ArrayList<>();

        String filePath = "C:\\Users\\okonj\\Desktop\\SWANSEA FOLDER\\Dissertation (Project)\\CAPEC & CWE\\CAPEC xmls\\Mechanisms of Attack.xml\\1000.xml";

        try {
            // Create a FileInputStream to read the local file
            FileInputStream inputStream = new FileInputStream(filePath);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            // Get elements by tag name "Category"
            NodeList categoryNodeList = document.getElementsByTagName("Category");

            for (int i = 0; i < categoryNodeList.getLength(); i++) {
                // Initialize a new Category
                AttackCategory attackCategory = new AttackCategory();

                Node attackCategoryNode = categoryNodeList.item(i);

                if (attackCategoryNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element attackCategoryElement = (Element) attackCategoryNode;

                    // Get and Set the Attack Category ID, Name and Status (Not adding Deprecated)
                    String attackCategoryID = attackCategoryElement.getAttribute("ID");
                    attackCategory.setCategoryId(Integer.parseInt(attackCategoryID));

                    String attackCategoryName = attackCategoryElement.getAttribute("Name");
                    attackCategory.setCategoryName(attackCategoryName);

                    // Get the summary of the attack category
                    NodeList attackCategorySummaryInstance = attackCategoryElement.getElementsByTagName("Summary");
                    String summaryText = attackCategorySummaryInstance.item(0).getTextContent();
                    attackCategory.setCategorySummary(summaryText);

                    // Get the CAPECId's for this Category
                    // Get the Relationships element
                    NodeList relationshipsList = attackCategoryElement.getElementsByTagName("Relationships");
                    if (relationshipsList.getLength() > 0) {
                        Element relationshipsElement = (Element) relationshipsList.item(0);
                        // Get the Has_Member elements
                        NodeList hasMemberList = relationshipsElement.getElementsByTagName("Has_Member");
                        List<Integer> capecIds = new ArrayList<>();
                        for (int j = 0; j < hasMemberList.getLength(); j++) {
                            Element hasMemberElement = (Element) hasMemberList.item(j);
                            String capecId = hasMemberElement.getAttribute("CAPEC_ID");
                            capecIds.add(Integer.parseInt(capecId));
                        }
                        // Set the CAPEC IDs for the attack category
                        attackCategory.setCapecIds(capecIds);
                    }
                }
                attackCategoriesToSave.add(attackCategory);
            }

        } catch (FileNotFoundException e) {
            System.err.println("Error: XML file not found at specified path.");
            e.printStackTrace();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Error parsing XML file:");
            e.printStackTrace();
        }
        return attackCategoriesToSave;
    }

    public static void main(String[] args) {
        AttackCategoryDAO attackCategoryDAO = new AttackCategoryDAO();
        AttackCategory attackCategory = new AttackCategory();

        // Assume you have a list of AttackPattern objects obtained from XML parsing
        List<AttackCategory> attackCategories = attackCategory.parseXMLDataFromCAPEC();

        attackCategoryDAO.saveAttackCategories(attackCategories);

        for (AttackCategory attackCategory_ : attackCategories) {
            attackCategoryDAO.saveCAPECAttackCategoriesRelationships(attackCategory_);
        }
    }
}
