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

// TODO: Don't forget to link CVE's from the ICSAssetVulnerability table
public class CommonWeaknessEnumeration {
    private String cweId;
    private String name;
    private String description;
    private String likelihoodOfExploit;
    private List<WeaknessMitigation> weaknessMitigations;
    public int capecId;

    public String getCweId() {
        return cweId;
    }

    public void setCweId(String cweId) {
        this.cweId = cweId;
    }

    public int getCapecId() {
        return capecId;
    }

    public void setCapecId(int capecId) {
        this.capecId = capecId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLikelihoodOfExploit() {
        return likelihoodOfExploit;
    }

    public void setLikelihoodOfExploit(String likelihoodOfExploit) {
        this.likelihoodOfExploit = likelihoodOfExploit;
    }

    public List<WeaknessMitigation> getWeaknessMitigations() {
        return weaknessMitigations;
    }

    public void setWeaknessMitigations(List<WeaknessMitigation> weaknessMitigations) {
        this.weaknessMitigations = weaknessMitigations;
    }

    public List<CommonWeaknessEnumeration> parseXMLDataFromCWE() {
        // List of attack patterns that will be saved to the db
        List<CommonWeaknessEnumeration> weaknessesToSave = new ArrayList<>();

        String filePath = "C:\\Users\\okonj\\Desktop\\SWANSEA FOLDER\\Dissertation (Project)\\cwec_latest.xml\\cwec_v4.13.xml";
        try {
            // Create a FileInputStream to read the local file
            FileInputStream inputStream = new FileInputStream(filePath);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            // Get elements by tag name "Weakness"
            NodeList nodeList = document.getElementsByTagName("Weakness");

            for (int i = 0; i < nodeList.getLength(); i++) {
                // Initialize a new Weakness
                CommonWeaknessEnumeration weaknessEnumeration = new CommonWeaknessEnumeration();

                Node weaknessNode = nodeList.item(i);
                if (weaknessNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element weaknessElement = (Element) weaknessNode;

                    // Get the CWE ID and Name
                    String weaknessID = weaknessElement.getAttribute("ID");
                    String weaknessName = weaknessElement.getAttribute("Name");

                    // Setting the weakness name
                    weaknessEnumeration.setName(weaknessName);
                    // Setting the CWE ID
                    weaknessEnumeration.setCweId("CWE-"+weaknessID);

                    // Get the description of the weakness and set it in our Weakness Object
                    NodeList weaknessDescriptionInstance = weaknessElement.getElementsByTagName("Description");
                    String weaknessDescriptionText = weaknessDescriptionInstance.item(0).getTextContent().trim();
                    weaknessEnumeration.setDescription(weaknessDescriptionText);

                    // Get the likelihood and Severity of the attack pattern from CAPEC and set it in our Attack Pattern object
                    NodeList likelihoodOfExploit = weaknessElement.getElementsByTagName("Likelihood_Of_Exploit");
                    String likelihoodOfExploitText = (likelihoodOfExploit.item(0) != null) ? likelihoodOfExploit.item(0).getTextContent().trim() : "";
                    weaknessEnumeration.setLikelihoodOfExploit(likelihoodOfExploitText);

                    // Get Possible Mitigations for the weakness
                    NodeList mitigationInstances = weaknessElement.getElementsByTagName("Mitigation");
                    if (mitigationInstances.getLength() > 0) {
                        // Create a list of mitigations and a mitigation object for our attack pattern
                        Element mitigationInstance = (Element) mitigationInstances.item(0);
                        NodeList mitigations = mitigationInstance.getElementsByTagName("Description");
                        List<WeaknessMitigation> mitigationList = new ArrayList<>();

                        // for each mitigation, proceed to fetch the data and set them accordingly to the object
                        for (int q = 0; q < mitigations.getLength(); q++) {
                            // New mitigation for each node
                            WeaknessMitigation mitigation = new WeaknessMitigation();
                            Element mitigationElement = (Element) mitigations.item(q);

                            // Get and set text and capecId
                            String mitigationText = mitigationElement.getTextContent().trim();
                            mitigation.setCweId(weaknessEnumeration.getCweId());
                            mitigation.setMitigationDescription(mitigationText);

                            // add each mitigation to the list
                            mitigationList.add(mitigation);
                        }
                        // In the end we add the list of mitigations for our attack pattern
                        weaknessEnumeration.setWeaknessMitigations(mitigationList);
                    }
                    // If there is no mitigation print it to the screen and skip insert.
                    else {
                        weaknessEnumeration.setWeaknessMitigations(new ArrayList<>()); // Assign an empty list.
                        System.out.println("Warning: Weakness " + weaknessID + " has no mitigations");
                    }
                }
                weaknessesToSave.add(weaknessEnumeration);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: XML file not found at specified path.");
            e.printStackTrace();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Error parsing XML file:");
            e.printStackTrace();
        }
        return weaknessesToSave;
    }

    public static void main(String[] args) {
        // Create DAO's needed
        CommonWeaknessEnumeration weaknessEnumeration = new CommonWeaknessEnumeration();
        CommonWeaknessEnumerationDAO weaknessEnumerationDAO = new CommonWeaknessEnumerationDAO();
        WeaknessMitigationDAO weaknessMitigationDAO = new WeaknessMitigationDAO();


        // Create a list of weakness objects gotten from parsing the XML file
        List<CommonWeaknessEnumeration> weaknessEnumerationList = weaknessEnumeration.parseXMLDataFromCWE();
        weaknessEnumerationDAO.saveWeaknesses(weaknessEnumerationList);

        // Get and save all mitigations for each weakness
        for (CommonWeaknessEnumeration weakness : weaknessEnumerationList) {
            weaknessMitigationDAO.saveMitigations(weakness.getWeaknessMitigations());
        }
    }

    // Method to parse a string representation and create an AttackPattern object
    public static CommonWeaknessEnumeration fromStringToCWE(String cweString) {
        CommonWeaknessEnumeration cwe = new CommonWeaknessEnumeration();
        CommonWeaknessEnumerationDAO commonWeaknessEnumerationDAO = new CommonWeaknessEnumerationDAO();
        WeaknessMitigationDAO weaknessMitigationDAO = new WeaknessMitigationDAO();

        String[] parts = cweString.split(": ", 2);
        if (parts.length != 2) {
            // Invalid format, return null or throw an exception
            return null;
        }

        // Extract CAPEC ID from the first part of the string
        String cweId = parts[0];

        // Extract name from the second part of the string
        String name = parts[1];

        cwe.setCweId(cweId);
        cwe.setName(name);
        cwe.setDescription(commonWeaknessEnumerationDAO.getWeaknessDescriptionFromDB(cweId));
        cwe.setLikelihoodOfExploit(commonWeaknessEnumerationDAO.getWeaknessLikelihoodFromDB(cweId));
        cwe.setWeaknessMitigations(weaknessMitigationDAO.getMitigationsForWeakness(cweId));
        // Get CVE Links

        // Create and return AttackPattern object
        return cwe;
    }

    @Override
    public String toString() {
        return cweId + ": " + name;
    }
}
