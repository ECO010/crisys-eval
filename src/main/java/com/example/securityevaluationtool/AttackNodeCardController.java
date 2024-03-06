package com.example.securityevaluationtool;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AttackNodeCardController {
    @FXML
    public Label nameLabel;
    @FXML
    public Label capecIdLabel;
    @FXML
    public Label descriptionLabel;
    @FXML
    public Label likelihoodLabel;
    @FXML
    public Label severityLabel;
    @FXML
    public Label taxonomyMappingLabel;
    @FXML
    public VBox mitigationBox;

    public void initialize() {
        // Initialization logic (if needed)
        // Create a context menu with "Copy" and "Search on Google" options
       /* MenuItem copyMenuItem = new MenuItem("Copy");
        copyMenuItem.setOnAction(e -> {
            // Copy the text when "Copy" is selected
            String text = taxonomyMappingLabel.getText();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(text);
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        });

        MenuItem searchGoogleMenuItem = new MenuItem("Search on Google");
        searchGoogleMenuItem.setOnAction(e -> {
            // Search for the text on Google when "Search on Google" is selected
            String text = taxonomyMappingLabel.getText();
            String searchUrl = "https://www.google.com/search?q=" + text;
            hostServices.showDocument(searchUrl);
        });

        // Set the context menu to the label
        ContextMenu contextMenu = new ContextMenu(copyMenuItem, searchGoogleMenuItem);
        taxonomyMappingLabel.setContextMenu(contextMenu);*/
    }

    // TODO:
    //  Get Mitigations to show on the card
    //  Redesign Card FXML (Remove h-box, v-box, make them simple labels/icons. Only mitigations should be a V-Box because of the checkboxes)

    public void setData(String name, int capecId, String description, String likelihood, String severity, String taxonomyMapping) {
        nameLabel.setText(nameLabel.getText() + name);
        capecIdLabel.setText(capecIdLabel.getText() + capecId);
        descriptionLabel.setText(descriptionLabel.getText() + description);
        likelihoodLabel.setText(likelihoodLabel.getText() + likelihood);
        severityLabel.setText(severityLabel.getText() + severity);
        taxonomyMappingLabel.setText(taxonomyMappingLabel.getText() + taxonomyMapping);
    }
}
