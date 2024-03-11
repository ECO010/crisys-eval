package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.AttackPattern;
import com.example.securityevaluationtool.database.Mitigation;
import com.example.securityevaluationtool.database.TaxonomyMapping;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Similar to the TreePrompt, just display the tree and work with interactions.
 * Displaying a sort of report card for each attack.
 */
public class TreeViewSceneController {

    public final String SCENE_TITLE = "Tree View Scene";
    @FXML
    private TreeView<AttackPattern> attackTreeView;

    // Method to set the root node of the TreeView
    public void setRootNode(TreeItem<AttackPattern> rootNode) {
        attackTreeView.setRoot(rootNode);
        attachContextMenu();
    }

    // Attach context menu to each tree item
    // TODO: Need to look at this method again to understand how it works because I don't quite get it atm
    private void attachContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem viewDetailsItem = new MenuItem("View Attack Details");
        contextMenu.getItems().add(viewDetailsItem);

        viewDetailsItem.setOnAction(event -> onViewAttackDetails());

        attackTreeView.setCellFactory(tree -> new TreeCell<>() {
            @Override
            protected void updateItem(AttackPattern item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setContextMenu(null); // Clear context menu
                } else {
                    setText(item.toString()); // Display attack name
                    setContextMenu(contextMenu); // Set context menu
                }
            }
        });
    }

    // Method to handle "View Attack Details" action
    private void onViewAttackDetails() {
        // Implement logic to display attack details
        // This method will be called when the "View Attack Details" menu item is clicked
        // You can retrieve the selected tree item using: attackTreeView.getSelectionModel().getSelectedItem()
        TreeItem<AttackPattern> selectedItem = attackTreeView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            // Retrieve the attack pattern associated with the selected tree item
            AttackPattern selectedAttackPattern = selectedItem.getValue();

            // Get Mitigations for the selected attack pattern
            List<Mitigation> mitigations = selectedAttackPattern.getMitigations();
            // Change list of mitigations to list of strings
            List<String> mitigationsStrings = mitigations.stream()
                    .map(Mitigation::getMitigationDescription)
                    .map(String::valueOf)
                    .collect(Collectors.toList());

            // Handcraft the mitigations as a single string.
            // I know this is terrible. I'll see if I can find another way later
            StringBuilder sb = new StringBuilder();
            for (String mitigation : mitigationsStrings) {
                sb.append("- ").append(mitigation).append("\n");
            }
            String mitigationsDisplay = sb.toString();

            // Logic to change the taxonomy mapping (MITRE ATT&CK TechniqueId) objects into a single string
            List<TaxonomyMapping> taxonomyMappings = selectedAttackPattern.getTaxonomyMappings();
            // Map each object to its ID as a string
            List<String> idStrings = taxonomyMappings.stream()
                    .map(TaxonomyMapping::getAttackTechniqueId)
                    .map(String::valueOf) // Convert ID to string
                    .collect(Collectors.toList());
            // Join the ID strings with a comma delimiter
            String taxonomyMappingsToString = String.join(", ", idStrings);

            // Printing out values to test
            System.out.println(selectedAttackPattern);
            System.out.println(selectedAttackPattern.getCapecId());
            System.out.println(selectedAttackPattern.getName());
            System.out.println(selectedAttackPattern.getDescription());
            System.out.println(selectedAttackPattern.getLikelihood());
            System.out.println(selectedAttackPattern.getSeverity());
            System.out.println(taxonomyMappingsToString);
            System.out.println(mitigationsDisplay);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("attack-node-card.fxml"));
                Parent root = loader.load();

                AttackNodeCardController cardController = loader.getController();

                // setData for the selected attack pattern to be displayed on the card
                cardController.setData(selectedAttackPattern.getName(),
                                        selectedAttackPattern.getCapecId(),
                                        selectedAttackPattern.getDescription().trim(), // trimming description because it shows up weird in the DB
                                        (selectedAttackPattern.getLikelihood() != null && !selectedAttackPattern.getLikelihood().isEmpty()) ? selectedAttackPattern.getLikelihood() : "Undetermined Likelihood",
                                        (selectedAttackPattern.getSeverity() != null && !selectedAttackPattern.getSeverity().isEmpty()) ? selectedAttackPattern.getSeverity() : "Undetermined Severity",
                                        (!taxonomyMappings.isEmpty()) ? taxonomyMappingsToString : "This Attack Pattern does not have any MITRE ATT&CK Mappings",
                                        !mitigations.isEmpty() ? mitigationsDisplay.trim() : "This Attack Pattern does not have any recorded Mitigations"); // trimming description because it shows up weird in the DB

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle(cardController.SCENE_TITLE);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                // Handle loading error
            }
        }
    }

    @FXML
    private void onSaveTreeAsPDF() {
        System.out.println("TODO");

        /*// Create a new PDF document
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // Write the tree visualization to the PDF
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText(treeVisualization);
                contentStream.endText();
            }

            // Display a file chooser dialog for the user to select the save location
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save PDF");
            int userSelection = fileChooser.showSaveDialog(new Frame());
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                // Save the PDF to the selected file location
                document.save(fileToSave);
                JOptionPane.showMessageDialog(null, "PDF saved successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }*/
    }


    // Open New Window
    // Based on the mitigations shown throughout the tree (root and its descendants) how prepared are you to prevent this attack?
    // Can't recall Mitigations? Option to View Mitigations again (just go back to treeView)
    // Options: Extremely Prepared, Somewhat Prepared, Neither Prepared Nor Unprepared, Somewhat Unprepared, Extremely Unprepared (Make them an Enum and factor them into score calculation)
    // get final score using linked Cumulative CVSS (from linked CWE's) and response
    // or using likelihood/severity of attack pattern and response
    // no mitigations? no score just linked CWE's and CVE's for the analyst to do some research on.
    @FXML
    private void onContinueEvaluation(/*ActionEvent event*/) {
        // close attack cards
        closeAllAttackCards();

        // Navigate to the Tree Prompt Screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("preparedness-window.fxml"));
            Parent root = loader.load();

            PreparednessWindowController preparednessWindowController = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(preparednessWindowController.SCENE_TITLE);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading error
        }
        //System.out.println("TODO");
    }

    private void closeAllAttackCards() {
        // Get a list of all open windows
        List<Window> openWindows = Window.getWindows();

        // List of Card windows
        List<Stage> cardViewStages = new ArrayList<>();

        // Get the controller of the Evaluation End
        AttackNodeCardController attackNodeCardController = new AttackNodeCardController();

        // Iterate through the open windows and close all open windows
        for (Window window : openWindows) {
            if (window instanceof Stage) {
                Stage stage = (Stage) window;
                if (stage.getTitle().equals(attackNodeCardController.SCENE_TITLE)) {
                    cardViewStages.add(stage);
                }
            }
        }
        // Close All CardView Stages
        closeAllWindows(cardViewStages);
    }

    private void closeAllWindows(List<Stage> stagesToClose) {
        for (Stage stage : stagesToClose) {
            stage.close();
        }
    }
}
