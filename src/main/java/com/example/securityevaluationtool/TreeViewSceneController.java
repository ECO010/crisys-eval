package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.AttackPattern;
import com.example.securityevaluationtool.database.TaxonomyMapping;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Similar to the TreePrompt, just display the tree and work with interactions.
 * Displaying a sort of report card for each attack.
 */
public class TreeViewSceneController {
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
                                        (selectedAttackPattern.getTaxonomyMappings() != null && !selectedAttackPattern.getTaxonomyMappings().isEmpty()) ? taxonomyMappingsToString : "This Attack Pattern does not have any MITRE ATT&CK Mappings");

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Attack Details");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                // Handle loading error
            }
        }
    }
}
