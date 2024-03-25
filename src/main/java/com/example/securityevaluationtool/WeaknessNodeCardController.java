package com.example.securityevaluationtool;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class WeaknessNodeCardController {

    public final String SCENE_TITLE = "Weakness Details";

    @FXML
    private Label cweIdLabel;

    @FXML
    private Label likelihoodLabel;

    @FXML
    private TextArea linkedCveTextArea;

    @FXML
    private TextArea mitigationsTextArea;

    @FXML
    private Label nameLabel;

    @FXML
    private TextArea weaknessDescriptionTextArea;

    public void setData(String name, String cweId, String description, String likelihoodOfExploit, String linkedCVEs, String mitigations) {
        nameLabel.setText(nameLabel.getText() + name);
        cweIdLabel.setText(cweIdLabel.getText() + cweId);
        likelihoodLabel.setText(likelihoodLabel.getText() + likelihoodOfExploit);

        weaknessDescriptionTextArea.setText(description);
        weaknessDescriptionTextArea.setEditable(false);
        weaknessDescriptionTextArea.setWrapText(true);

        linkedCveTextArea.setText(linkedCVEs);
        linkedCveTextArea.setEditable(false);
        linkedCveTextArea.setWrapText(true);

        mitigationsTextArea.setText(mitigations);
        mitigationsTextArea.setWrapText(true);
        mitigationsTextArea.setEditable(false);
    }
}
