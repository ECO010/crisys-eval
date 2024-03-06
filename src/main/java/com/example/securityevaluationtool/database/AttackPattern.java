package com.example.securityevaluationtool.database;

/**
 * TO-DO: Get weaknesses from CWE and link them to CAPEC
 */

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

public class AttackPattern {
    /**
     * List of ICS attack patterns from the CAPEC website
     */
    public static final String[] ICS_CAPEC_IDS = {
            "1",//Accessing Functionality Not Properly Constrained by ACLs
            "57",//Utilizing REST's Trust in the System Resource to Obtain Sensitive Data
            "65",//Sniff Application Code
            "70",//Try Common or Default Usernames and Passwords
            "94",//Adversary in the Middle (AiTM)
            "98",//Phishing
            "125",//Flooding
            "130",//Excessive Allocation
            "131",//Resource Leak Exposure
            "141",//Cache Poisoning
            "148",//Content Spoofing
            "151",//Identity Spoofing
            "158",//Sniffing Network Traffic
            "163",//Spear Phishing
            "165",//File Manipulation
            "169",//Footprinting
            "177",//Create files with the same name as files protected with a higher classification
            "180",//Exploiting Incorrectly Configured Access Control Security Levels
            "184",//Software Integrity Attack
            "191",//Read Sensitive Constants Within an Executable
            "227",//Sustained Client Engagement
            "268",//Audit Log Manipulation
            "292",//Host Discovery
            "309",//Network Topology Mapping
            "312",//Active OS Fingerprinting
            "313",//Passive OS Fingerprinting
            "438",//Modification During Manufacture
            "439",//Manipulation During Distribution
            "441",//Malicious Logic Insertion
            "457",//USB Memory Attacks
            "473",//Signature Spoof
            "504",//Task Impersonation
            "540",//Overread Buffers
            "547",//Physical Destruction of Device or Component
            "552",//Install Rootkit
            "555",//Remote Services with Stolen Credentials
            "560",//Use of Known Domain Credentials
            "573",//Process Footprinting
            "580",//System Footprinting
            "603",//Blockage
            "607",//Obstruction
            "635",//Alternative Execution Due to Deceptive Filenames
            "648",//Collect Data from Screen Capture
            "649",//Adding a Space to a File Extension
            "690",//Metadata Spoofing
            "691",//Spoof Open-Source Software Metadata
            "692" //Spoof Version Control System Commit Metadata
    };
    public String name;
    public String likelihood;
    public String severity;
    public int capecId;
    public String description;
    private List<AttackIndicator> attackIndicators;
    private List<Mitigation> mitigations; // try and see if these can also be pulled from ATT&CK
    private List<RelatedAttack> relatedAttacks;
    private List<ExampleInstance> exampleInstances;
    private List<AttackStep> attackSteps;
    private List<Consequence> consequences;
    private List<TaxonomyMapping> taxonomyMappings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLikelihood() {
        return likelihood;
    }

    public void setLikelihood(String likelihood) {
        this.likelihood = likelihood;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public int getCapecId() {
        return capecId;
    }

    public void setCapecId(int capecId) {
        this.capecId = capecId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AttackIndicator> getIndicators() {
        return attackIndicators;
    }

    public void setIndicators(List<AttackIndicator> attackIndicators) {
        this.attackIndicators = attackIndicators;
    }

    public List<Mitigation> getMitigations() {
        return mitigations;
    }

    public void setMitigations(List<Mitigation> mitigations) {
        this.mitigations = mitigations;
    }

    public List<RelatedAttack> getRelatedAttacks() {
        return relatedAttacks;
    }

    public void setRelatedAttacks(List<RelatedAttack> relatedAttacks) {
        this.relatedAttacks = relatedAttacks;
    }

    public List<ExampleInstance> getExampleInstances() {
        return exampleInstances;
    }

    public void setExampleInstances(List<ExampleInstance> exampleInstances) {
        this.exampleInstances = exampleInstances;
    }

    public List<AttackStep> getAttackSteps() {
        return attackSteps;
    }

    public void setAttackSteps(List<AttackStep> attackSteps) {
        this.attackSteps = attackSteps;
    }

    public List<Consequence> getConsequences() {
        return consequences;
    }

    public void setConsequences(List<Consequence> consequences) {
        this.consequences = consequences;
    }

    public List<TaxonomyMapping> getTaxonomyMappings() {
        return taxonomyMappings;
    }

    public void setTaxonomyMappings(List<TaxonomyMapping> taxonomyMappings) {
        this.taxonomyMappings = taxonomyMappings;
    }

    public List<AttackPattern> parseXMLDataFromCAPEC() {
        // List of attack patterns that will be saved to the db
        List<AttackPattern> attackPatternsToSave = new ArrayList<>();

        String filePath = "C:\\Users\\okonj\\Downloads\\CAPEC xmls\\capec_latest.xml";
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
                    
                    // Setting the attack Pattern Name
                    attackPattern.setName(attackPatternName);
                    // Setting the Attack Pattern ID
                    attackPattern.setCapecId(Integer.parseInt(attackPatternCapecID));

                    // Get the description of the attack pattern from CAPEC and set it in our Attack Pattern object
                    NodeList attackDescriptionInstance = attackPatternElement.getElementsByTagName("Description");
                    String attackDescriptionText = attackDescriptionInstance.item(0).getTextContent();
                    attackPattern.setDescription(attackDescriptionText);

                    // Get the likelihood and Severity of the attack pattern from CAPEC and set it in our Attack Pattern object
                    NodeList attackLikelihood = attackPatternElement.getElementsByTagName("Likelihood_Of_Attack");
                    NodeList attackSeverity = attackPatternElement.getElementsByTagName("Typical_Severity");
                    String attackLikelihoodText = (attackLikelihood.item(0) != null) ? attackLikelihood.item(0).getTextContent() : "";
                    String attackSeverityText = (attackSeverity.item(0) != null) ? attackSeverity.item(0).getTextContent() : "";
                    attackPattern.setLikelihood(attackLikelihoodText);
                    attackPattern.setSeverity(attackSeverityText);

                    // Get related attack patterns from CAPEC (parentOf, ChildOf, CanPrecede, CanFollow)
                    // Create new list of related attacks as well as a related attack object.
                    // Each attack pattern can have more than one related attack
                    List<RelatedAttack> relatedAttacksList = new ArrayList<>();
                    NodeList relatedAttacks = attackPatternElement.getElementsByTagName("Related_Attack_Pattern");

                    // For each related attack we get the nature,
                    // the original attack's capecId and the capecId of the attack it's related to
                    if (relatedAttacks != null) {
                        for (int j = 0; j < relatedAttacks.getLength(); j++) {
                            // create a new related attack each time
                            RelatedAttack relatedAttack_ = new RelatedAttack();

                            // Specify the tags we are fetching the data from
                            Element relatedAttack = (Element) relatedAttacks.item(j);
                            String nature = relatedAttack.getAttribute("Nature");
                            String relatedAttackCapecID = relatedAttack.getAttribute("CAPEC_ID");

                            relatedAttack_.setNature(nature);
                            relatedAttack_.setRelationFromCapecId(Integer.parseInt(attackPatternCapecID));
                            relatedAttack_.setRelationToCapecId(Integer.parseInt(relatedAttackCapecID));
                            relatedAttacksList.add(relatedAttack_);
                        }
                    }
                    // If there are no relationships print it to the screen and skip insert.
                    else {
                        attackPattern.setRelatedAttacks(new ArrayList<>()); // Assign an empty list.
                        System.out.println("Warning: Attack pattern " + attackPatternCapecID + " has no relationships, it could be An attacker's final goal");
                    }
                    // We add our list of related attacks for our attack pattern
                    attackPattern.setRelatedAttacks(relatedAttacksList);

                    // Get execution flow of the attack
                    // Again we must relate it to the attack pattern ID in question
                    NodeList executionFlow = attackPatternElement.getElementsByTagName("Execution_Flow");
                    if (executionFlow.getLength() > 0) {
                        Element execution = (Element) executionFlow.item(0);
                        NodeList attackSteps = execution.getElementsByTagName("Attack_Step");

                        // Create a list of attack steps and an attack step object for the attack pattern
                        List<AttackStep> attackStepList = new ArrayList<>();

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

                                // Create a new AttackStep object for each iteration
                                AttackStep attackStep = new AttackStep();
                                attackStep.setCapecId(Integer.parseInt(attackPatternCapecID));
                                attackStep.setStep(step);
                                attackStep.setPhase(phase);
                                attackStep.setAttackStepDescription(attackStepDescription);

                                attackStepList.add(attackStep);
                            }
                        }
                        // In the end we add the list of attack steps for our attack pattern
                        attackPattern.setAttackSteps(attackStepList);
                    }
                    // If there is no execution flow node print it to screen and just skip adding it to the table
                    else {
                        attackPattern.setAttackSteps(new ArrayList<>()); // Assign an empty list
                        System.out.println("Warning: Attack pattern " + attackPatternCapecID + " has no attack steps.");
                    }

                    // Get consequences/impact of the attack
                    NodeList consequences = attackPatternElement.getElementsByTagName("Consequence");
                    if (consequences.getLength() > 0) {

                        // Create a list of consequences and a consequence object for the attack pattern
                        List<Consequence> consequenceList = new ArrayList<>();

                        // for each consequence, proceed to fetch the data and set them accordingly to the object
                        for (int l = 0; l < consequences.getLength(); l++) {
                            Element consequence = (Element) consequences.item(l);

                            // Create a new Consequence object for each consequence
                            Consequence consequence_ = new Consequence();

                            // set the capec id
                            consequence_.setCapecId(Integer.parseInt(attackPatternCapecID));

                            //String impactString = impact.item(0) != null ? impact.item(0).getTextContent() : "";

                            // Get scope details
                            // Handle multiple Scope elements
                            List<String> scopes = new ArrayList<>();
                            NodeList scopeElements = consequence.getElementsByTagName("Scope");
                            for (int m = 0; m < scopeElements.getLength(); m++) {
                                Node scopeItem = scopeElements.item(m);
                                String scope = scopeItem != null ? scopeItem.getTextContent() : "";
                                scopes.add(scope);
                            }
                            consequence_.setScopes(scopes);

                            // Set the capecid and the impact
                            // Handle multiple Impact elements
                            List<String> impacts = new ArrayList<>();
                            NodeList impactElements = consequence.getElementsByTagName("Impact");
                            for (int n = 0; n < impactElements.getLength(); n++) {
                                Node impactItem = impactElements.item(n);
                                String impact = impactItem != null ? impactItem.getTextContent() : "";
                                impacts.add(impact);
                            }
                            consequence_.setImpacts(impacts);

                            // add the consequence to the list of consequences
                            consequenceList.add(consequence_);
                        }
                        // In the end we add the list of consequences for our attack pattern
                        attackPattern.setConsequences(consequenceList);
                    }
                    // If there is no consequence print it to the screen and skip insert.
                    else {
                        attackPattern.setConsequences(new ArrayList<>()); // Assign an empty list.
                        System.out.println("Warning: Attack pattern " + attackPatternCapecID + " has no consequences");
                    }

                    // Get Example instances of the attack
                    NodeList exampleInstances = attackPatternElement.getElementsByTagName("Example_Instances");
                    if (exampleInstances.getLength() > 0) {
                        // Create a list of example instances and an example instance object for the attack pattern
                        List<ExampleInstance> exampleInstanceList = new ArrayList<>();
                        Element exampleInstance = (Element) exampleInstances.item(0);
                        NodeList examples = exampleInstance.getElementsByTagName("Example");

                        // for each example instance, create an object and set the needed information
                        for (int l = 0; l < examples.getLength(); l++) {
                            Element example = (Element) examples.item(l);

                            // Create a new example instance and set the capec id
                            ExampleInstance exampleInstance_ = new ExampleInstance();
                            exampleInstance_.setCapecId(Integer.parseInt(attackPatternCapecID));

                            // Go through paragraph tags to get example descriptions for each example instance object
                            NodeList paragraphs = example.getElementsByTagName("xhtml:p");
                            String updatedDescription = " ";
                            for (int p = 0; p < paragraphs.getLength(); p++) {
                                Element paragraph = (Element) paragraphs.item(p);

                                // Append the current paragraph text to the existing description
                                updatedDescription = updatedDescription + paragraph.getTextContent();
                            }
                            String exampleDescription = updatedDescription;
                            exampleInstance_.setExampleInstanceDescription(exampleDescription);
                            exampleInstanceList.add(exampleInstance_);
                        }
                        // In the end we add the list of example instances for our attack pattern
                        attackPattern.setExampleInstances(exampleInstanceList);
                    }
                    // If there is no example instance print it to the screen and skip insert.
                    else {
                        attackPattern.setExampleInstances(new ArrayList<>()); // Assign an empty list.
                        System.out.println("Warning: Attack pattern " + attackPatternCapecID + " has no example instances");
                    }

                    // Get Possible Mitigations for the attack
                    NodeList mitigationInstances = attackPatternElement.getElementsByTagName("Mitigations");
                    if (mitigationInstances.getLength() > 0) {
                        // Create a list of mitigations and a mitigation object for our attack pattern
                        Element mitigationInstance = (Element) mitigationInstances.item(0);
                        NodeList mitigations = mitigationInstance.getElementsByTagName("Mitigation");
                        List<Mitigation> mitigationList = new ArrayList<>();

                        // for each mitigation, proceed to fetch the data and set them accordingly to the object
                        for (int q = 0; q < mitigations.getLength(); q++) {
                            // New mitigation for each node
                            Mitigation mitigation_ = new Mitigation();
                            Element mitigation = (Element) mitigations.item(q);

                            // Get and set text and capecId
                            String mitigationText = mitigation.getTextContent();
                            mitigation_.setCapecId(Integer.parseInt(attackPatternCapecID));
                            mitigation_.setMitigationDescription(mitigationText);

                            // add each mitigation to the list
                            mitigationList.add(mitigation_);
                        }
                        // In the end we add the list of mitigations for our attack pattern
                        attackPattern.setMitigations(mitigationList);
                    }
                    // If there is no mitigation print it to the screen and skip insert.
                    else {
                        attackPattern.setMitigations(new ArrayList<>()); // Assign an empty list.
                        System.out.println("Warning: Attack pattern " + attackPatternCapecID + " has no mitigations");
                    }

                    // Get Possible Indicators that the attack could have happened or is about to happen
                    NodeList indicatorsList = attackPatternElement.getElementsByTagName("Indicators");
                    if (indicatorsList.getLength() > 0) {
                        // Create a list of indicators and an indicator object for our attack pattern
                        Element indicatorsElement = (Element) indicatorsList.item(0);
                        NodeList indicators = indicatorsElement.getElementsByTagName("Indicator");
                        List<AttackIndicator> attackIndicatorList = new ArrayList<>();

                        // for each indicator, create a new object and proceed to fetch the data
                        for (int r = 0; r < indicators.getLength(); r++) {
                            Element indicator = (Element) indicators.item(r);
                            AttackIndicator attackIndicator_ = new AttackIndicator();

                            String indicatorText = indicator.getTextContent();

                            // set fields values for each indicator object
                            attackIndicator_.setCapecId(Integer.parseInt(attackPatternCapecID));
                            attackIndicator_.setIndicator(indicatorText);

                            // add to the list
                            attackIndicatorList.add(attackIndicator_);
                        }
                        // In the end we add the list of indicators for our attack pattern
                        attackPattern.setIndicators(attackIndicatorList);
                    }
                    // If there is no indicator print it to the screen and skip insert.
                    else {
                        attackPattern.setIndicators(new ArrayList<>()); // Assign an empty list.
                        System.out.println("Warning: Attack pattern " + attackPatternCapecID + " has no indicators");
                    }

                    // Get the Taxonomy Mappings for the current Attack Pattern
                    // Create a list of Taxonomy mappings and a taxonomy mapping object for our attack pattern
                    NodeList taxonomyMappings = attackPatternElement.getElementsByTagName("Taxonomy_Mapping");
                    List<TaxonomyMapping> taxonomyMappingList = new ArrayList<>();

                    if (taxonomyMappings != null) {
                        // Loop through the Taxonomy Mappings for this Attack Pattern creating an object each time
                        for (int j = 0; j < taxonomyMappings.getLength(); j++) {
                            Element taxonomyMapping = (Element) taxonomyMappings.item(j);

                            TaxonomyMapping taxonomyMapping_ = new TaxonomyMapping();

                            // Check if the Taxonomy Name is "ATTACK"
                            String taxonomyName = taxonomyMapping.getAttribute("Taxonomy_Name");
                            if (taxonomyName.equals("ATTACK")) {
                                // set the capecId
                                taxonomyMapping_.setCapecId(Integer.parseInt(attackPatternCapecID));

                                // Get and set the ATT&CK mapping ID
                                String entryID = taxonomyMapping.getElementsByTagName("Entry_ID").item(0).getTextContent();
                                taxonomyMapping_.setAttackTechniqueId(entryID);

                                // Get and set the ATT&CK mapping name
                                String entryName = taxonomyMapping.getElementsByTagName("Entry_Name").item(0).getTextContent();
                                taxonomyMapping_.setAttackTechniqueName(entryName);

                                // add to the list
                                taxonomyMappingList.add(taxonomyMapping_);
                            }
                        }
                    }
                    // If there is no mapping print it to the screen and skip insert.
                    else {
                        attackPattern.setTaxonomyMappings(new ArrayList<>()); // Assign an empty list.
                        System.out.println("Warning: Attack pattern " + attackPatternCapecID + " has no ATT&CK mappings");
                    }
                    // In the end we add the list of taxonomy mappings for our attack pattern
                    attackPattern.setTaxonomyMappings(taxonomyMappingList);
                }
                attackPatternsToSave.add(attackPattern);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: XML file not found at specified path.");
            e.printStackTrace();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Error parsing XML file:");
            e.printStackTrace();
        }
        return attackPatternsToSave;
    }


    /**
     * Main method just to add stuff to the db will delete once satisfied with the db
     *
     * @param args
     */
    public static void main(String[] args) {
        AttackPatternDAO attackPatternDAO = new AttackPatternDAO();
        AttackPattern attackPattern = new AttackPattern();

        // Assume you have a list of AttackPattern objects obtained from XML parsing
        List<AttackPattern> attackPatterns = attackPattern.parseXMLDataFromCAPEC();

        attackPatternDAO.saveAttackPatterns(attackPatterns);

        /*RelatedAttackDAO relatedAttackDAO = new RelatedAttackDAO();

        for (AttackPattern pattern : attackPatterns) {
            //attackStepDAO.saveAttackSteps(pattern.getAttackSteps());
            relatedAttackDAO.saveRelatedAttacks(pattern.getRelatedAttacks());
        }*/
    }

    @Override
    public String toString() {
        return name + " (" + capecId + ")";
    }
}
