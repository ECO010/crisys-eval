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

public class AttackStepTechnique {
    private String technique;
    private String attackStep;
    private int capecId;

    public String getAttackStep() {
        return attackStep;
    }

    public void setAttackStep(String attackStep) {
        this.attackStep = attackStep;
    }

    public String getTechnique() {
        return technique;
    }

    public void setTechnique(String technique) {
        this.technique = technique;
    }

    public int getCapecId() {
        return capecId;
    }

    public void setCapecId(int capecId) {
        this.capecId = capecId;
    }

    public List<AttackStepTechnique> parseXMLDataFromCAPEC() {
        // List of attack patterns that will be saved to the db
        List<AttackStepTechnique> attackStepTechniquesToSave = new ArrayList<>();

        String filePath = "your file path here";
        try {
            // Create a FileInputStream to read the local file
            FileInputStream inputStream = new FileInputStream(filePath);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            // Get elements by tag name "Attack_Pattern"
            NodeList nodeList = document.getElementsByTagName("Attack_Pattern");

            for (int i = 0; i < nodeList.getLength(); i++) {
                // Initialize a new Attack Pattern
                AttackPattern attackPattern = new AttackPattern();

                Node attackPatternNode = nodeList.item(i);
                if (attackPatternNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element attackPatternElement = (Element) attackPatternNode;

                    // Get the Attack Pattern ID and Name
                    String attackPatternCapecID = attackPatternElement.getAttribute("ID");
                    attackPattern.setCapecId(Integer.parseInt(attackPatternCapecID));
                    String attackPatternName = attackPatternElement.getAttribute("Name");
                    attackPattern.setName(attackPatternName);

                    // Get execution flow of the attack
                    // Again we must relate it to the attack pattern ID in question
                    NodeList executionFlow = attackPatternElement.getElementsByTagName("Execution_Flow");
                    if (executionFlow.getLength() > 0) {
                        Element execution = (Element) executionFlow.item(0);
                        NodeList attackSteps = execution.getElementsByTagName("Attack_Step");

                        // Create a list of attack steps and an attack step object for the attack pattern
                        List<AttackStep> attackStepList = new ArrayList<>();
                        List<AttackStepTechnique> attackStepTechniqueList = new ArrayList<>();

                        if (attackSteps.getLength() > 0) {
                            // For each attack step, proceed to fetch the data and set them accordingly to the object
                            for (int k = 0; k < attackSteps.getLength(); k++) {
                                Element attackStepElement = (Element) attackSteps.item(k);
                                //if (attackStepElement != null) {

                                Node stepElementItem = attackStepElement.getElementsByTagName("Step").item(0);
                                Node phaseElementItem = attackStepElement.getElementsByTagName("Phase").item(0);
                                Node descriptionElementItem = attackStepElement.getElementsByTagName("Description").item(0);

                                String step = stepElementItem != null ? stepElementItem.getTextContent() : "";
                                String phase = phaseElementItem != null ? phaseElementItem.getTextContent() : "";
                                String attackStepDescription = descriptionElementItem != null ? descriptionElementItem.getTextContent() : "";

                                // Create a list of indicators and an indicator object for our attack pattern
                                NodeList attackStepTechniques = attackStepElement.getElementsByTagName("Technique");
                                // for each indicator, create a new object and proceed to fetch the data
                                for (int l = 0; l < attackStepTechniques.getLength(); l++) {
                                    Element attackStepTechnique = (Element) attackStepTechniques.item(l);
                                    AttackStepTechnique stepTechnique = new AttackStepTechnique();

                                    String techniqueTextContent = attackStepTechnique.getTextContent();

                                    // set fields values for each indicator object
                                    stepTechnique.setCapecId(Integer.parseInt(attackPatternCapecID));
                                    stepTechnique.setTechnique(techniqueTextContent);
                                    stepTechnique.setAttackStep(step);

                                    // add to the list
                                    attackStepTechniqueList.add(stepTechnique);
                                }

                                // Create a new AttackStep object for each iteration
                                AttackStep attackStep = new AttackStep();
                                attackStep.setCapecId(Integer.parseInt(attackPatternCapecID));
                                attackStep.setStep(step);
                                attackStep.setPhase(phase);
                                attackStep.setAttackStepDescription(attackStepDescription);
                                attackStep.setAttackStepTechniques(attackStepTechniqueList);

                                attackStepList.add(attackStep);
                            }
                        }
                        // In the end we add the list of attack steps for our attack pattern
                        attackPattern.setAttackSteps(attackStepList);
                        attackStepTechniquesToSave.addAll(attackStepTechniqueList);
                    }
                    // If there is no execution flow node print it to screen and just skip adding it to the table
                    else {
                        attackPattern.setAttackSteps(new ArrayList<>()); // Assign an empty list
                        System.out.println("Warning: Attack pattern " + attackPatternCapecID + " has no attack steps.");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: XML file not found at specified path.");
            e.printStackTrace();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Error parsing XML file:");
            e.printStackTrace();
        }
        return attackStepTechniquesToSave;
    }

    public static void main(String[] args) {
        AttackStepDAO attackStepDAO = new AttackStepDAO();
        AttackStepTechnique attackStepTechnique = new AttackStepTechnique();

        // Assume you have a list of AttackPattern objects obtained from XML parsing
        List<AttackStepTechnique> attackStepTechniques = attackStepTechnique.parseXMLDataFromCAPEC();
        attackStepDAO.saveAttackStepTechniques(attackStepTechniques);
    }

    @Override
    public String toString() {
        return "Technique for step "+ attackStep+ ": "+ technique.trim();
    }
}
