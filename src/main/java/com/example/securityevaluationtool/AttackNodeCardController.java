package com.example.securityevaluationtool;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class AttackNodeCardController {

    public final String SCENE_TITLE = "Attack Details";
    @FXML
    public Label nameLabel;
    @FXML
    public Label capecIdLabel;
    @FXML
    public Label likelihoodLabel;
    @FXML
    public Label severityLabel;
    @FXML
    public TextArea taxonomyMappingTextArea;
    @FXML
    public TextArea mitigationsTextArea;
    @FXML
    public TextArea attackDescriptionTextArea;

    public void initialize() {
        // Initialization logic (if needed)
    }

    // TODO:
    //  Get Mitigations to show on the card
    //  Redesign Card FXML (Remove h-box, v-box, make them simple labels/icons. Only mitigations should be a V-Box because of the checkboxes)

    public void setData(String name, int capecId, String description, String likelihood, String severity, String taxonomyMapping, String mitigations) {
        nameLabel.setText(nameLabel.getText() + name);
        capecIdLabel.setText(capecIdLabel.getText() + capecId);
        likelihoodLabel.setText(likelihoodLabel.getText() + likelihood);
        severityLabel.setText(severityLabel.getText() + severity);

        attackDescriptionTextArea.setText(description);
        attackDescriptionTextArea.setEditable(false);
        attackDescriptionTextArea.setWrapText(true);

        taxonomyMappingTextArea.setText(taxonomyMapping);
        taxonomyMappingTextArea.setEditable(false);
        taxonomyMappingTextArea.setWrapText(true);

        mitigationsTextArea.setText(mitigations);
        mitigationsTextArea.setWrapText(true);
        mitigationsTextArea.setEditable(false);
    }
}
