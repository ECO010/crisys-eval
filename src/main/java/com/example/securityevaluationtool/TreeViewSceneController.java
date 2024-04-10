package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
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

    // Get the required DAOs to access the DB
    private final EvaluationDAO evaluationDAO = new EvaluationDAO();
    private final CommonWeaknessEnumerationDAO commonWeaknessEnumerationDAO = new CommonWeaknessEnumerationDAO();

    // Field(s) and method(s) for getting data from previous controller
    private int yearTo;
    private int yearFrom;
    private Evaluation currentEvaluation;
    private List<EvaluationAsset> retrievedEvaluationAssets;

    public void getCurrentEvaluation(Evaluation currentEvaluation) {
        this.currentEvaluation = currentEvaluation;
    }

    public void getEvaluationAssets(List<EvaluationAsset> retrievedEvaluationAssets) {
        this.retrievedEvaluationAssets = retrievedEvaluationAssets;
    }

    public void getYearTo(int yearTo) {
        this.yearTo = yearTo;
    }

    public void getYearFrom(int yearFrom) {
        this.yearFrom = yearFrom;
    }

    @FXML
    private TreeView<String> attackTreeView;

    @FXML
    public Button continueBtn;

    @FXML
    public Button returnBtn;

    // Method to set the root node of the TreeView
    public void setRootNode(TreeItem<String> rootNode, boolean isAttachContextMenu) {
        attackTreeView.setRoot(rootNode);
        if (isAttachContextMenu) {
            attachContextMenu();
        }
    }

    // Attach context menu to each tree item
    private void attachContextMenu() {

        attackTreeView.setCellFactory(tree -> new TreeCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setContextMenu(null);
                }
                else {
                    // Check if the value represents a CWE or CAPEC
                    if (item.startsWith("CWE-")) {
                        setText(item); // Display CWE name
                        // Create context menu for CWE
                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem viewCweDetailsItem = new MenuItem("View Weakness Details");
                        contextMenu.getItems().add(viewCweDetailsItem);
                        viewCweDetailsItem.setOnAction(event -> onViewWeaknessDetails());
                        setContextMenu(contextMenu);
                    }
                    else if (item.startsWith("CAPEC-")) {
                        setText(item); // Display CAPEC name
                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem viewDetailsItem = new MenuItem("View Attack Details");
                        contextMenu.getItems().add(viewDetailsItem);
                        viewDetailsItem.setOnAction(event -> onViewAttackDetails());
                        setContextMenu(contextMenu);
                    }
                    else {
                        // Handle other cases if needed
                        setText(item); // Display the value as is
                        setContextMenu(null); // No context menu
                    }
                }
            }
        });
    }

    // Method to handle "View Attack Details" action
    private void onViewAttackDetails() {
        // Implement logic to display attack details
        // This method will be called when the "View Attack Details" menu item is clicked
        // You can retrieve the selected tree item using: attackTreeView.getSelectionModel().getSelectedItem()
        TreeItem<String> selectedItem = attackTreeView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            // Retrieve the name of the attack pattern associated with the selected tree item
            String selectedAttackPatternDisplay = selectedItem.getValue();

            // Convert the attack pattern display to an AttackPattern object
            AttackPattern selectedAttackPattern = AttackPattern.fromStringToAttackPattern(selectedAttackPatternDisplay);

            // Get Mitigations for the selected attack pattern
            assert selectedAttackPattern != null;
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
                sb.append("- ").append(mitigation.trim()).append("\n");
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
            System.out.println(yearFrom);
            System.out.println(yearTo);
            System.out.println(currentEvaluation.getCriticalSystemName());
            System.out.println(retrievedEvaluationAssets.size());
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

    // Method to handle "View Weakness Details" action
    private void onViewWeaknessDetails() {
        TreeItem<String> selectedItem = attackTreeView.getSelectionModel().getSelectedItem();

        // Get the evalId of the first asset in the evaluation, it will be the same for all evaluation assets
        // We are using this and the assetName of the selected node to query the assetType from the DB
        int evaluationId = retrievedEvaluationAssets.get(0).getEvaluationID();
        String assetName = selectedItem.getParent().getValue();
        String assetType = evaluationDAO.getAssetTypeFromAssetName(evaluationId, assetName);

        if (selectedItem != null) {
            // Retrieve the name of the attack pattern associated with the selected tree item
            String selectedWeaknessDisplay = selectedItem.getValue();

            // Convert the attack pattern display to an AttackPattern object
            CommonWeaknessEnumeration selectedWeakness = CommonWeaknessEnumeration.fromStringToCWE(selectedWeaknessDisplay);

            // Get Mitigations for the selected attack pattern
            assert selectedWeakness != null;
            List<WeaknessMitigation> weaknessMitigations = selectedWeakness.getWeaknessMitigations();
            // Change list of mitigations to list of strings
            List<String> mitigationsStrings = weaknessMitigations.stream()
                    .map(WeaknessMitigation::getMitigationDescription)
                    .map(String::valueOf)
                    .collect(Collectors.toList());

            // Handcraft the mitigations as a single string.
            // I know this is terrible. I'll see if I can find another way later
            StringBuilder sb = new StringBuilder();
            for (String mitigation : mitigationsStrings) {
                sb.append("- ").append(mitigation.trim()).append("\n");
            }
            String mitigationsDisplay = sb.toString();

            // Fetch CVEs from ICSAssetVulnerability. Make sure it's filtered by the selected years, CweNumber, Asset Type
            String linkedCVEs = commonWeaknessEnumerationDAO.getLinkedCVEs(selectedWeakness.getCweId(), assetType, yearFrom, yearTo);

            // Printing out values to test
            System.out.println(selectedWeakness);
            System.out.println(yearFrom);
            System.out.println(yearTo);
            System.out.println(currentEvaluation.getCriticalSystemName());
            System.out.println(retrievedEvaluationAssets.size());
            System.out.println(assetType);
            System.out.println(selectedWeakness.getCweId());
            System.out.println(selectedWeakness.getName());
            System.out.println(selectedWeakness.getDescription());
            System.out.println(selectedWeakness.getLikelihoodOfExploit());
            System.out.println(linkedCVEs);
            System.out.println(mitigationsDisplay);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("weakness-node-card.fxml"));
                Parent root = loader.load();

                WeaknessNodeCardController cardController = loader.getController();

                // Splitting CWE to get the literal ID number
                String[] split = selectedWeakness.getCweId().split("-");
                String cweId = split[1];

                // setData for the selected attack pattern to be displayed on the card
                cardController.setData(selectedWeakness.getName(),
                        cweId,
                        selectedWeakness.getDescription().trim(), // trimming description because it shows up weird in the DB
                        (selectedWeakness.getLikelihoodOfExploit() != null && !selectedWeakness.getLikelihoodOfExploit().isEmpty()) ? selectedWeakness.getLikelihoodOfExploit() : "Undetermined Likelihood",
                        linkedCVEs,
                        !weaknessMitigations.isEmpty() ? mitigationsDisplay.trim() : "This Weakness does not have any recommended Mitigations"); // trimming description because it shows up weird in the DB

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

    // Need all nodes expanded so the user can see all nodes connected to the asset
    // Snapshot of the treeView scene that is showing (note treeView is null because of the way the application is designed, how can we work around this)
    // Don't want to show the root node only the children and their descendants
    // put them in a pdf and let the user choose where to save them
    @FXML
    private void onSaveTreeAsPDF() {
        TreeItem<String> rootNode = attackTreeView.getRoot();
        if (rootNode != null) {
            // Expand all nodes
            expandTree(rootNode);

            // Capture the tree view as an image
            WritableImage image = captureTreeView();

            // Save the image to a PDF file
            saveTreesToPDF(image);
        }
        else {
            // Handle the case where the root node is null
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No attack tree data available to save.");
            alert.showAndWait();
        }
    }

    public WritableImage captureTreeView() {
        // Get the width and height of the TreeView
        double width = attackTreeView.getWidth();
        double height = attackTreeView.getHeight();

        // Create a new WritableImage with the specified width and height
        WritableImage image = new WritableImage((int) width, (int) height);

        // Snapshot the TreeView to get a snapshot of its current appearance
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT); // Make background transparent
        Image snapshot = attackTreeView.snapshot(params, null);

        // Get the PixelReader from the snapshot
        PixelReader pixelReader = snapshot.getPixelReader();

        // Get the PixelWriter from the WritableImage
        PixelWriter pixelWriter = image.getPixelWriter();

        // Iterate over each pixel in the snapshot and copy its color to the WritableImage
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the color of the pixel at (x, y) from the PixelReader
                Color color = pixelReader.getColor(x, y);
                // Set the color of the corresponding pixel in the WritableImage using the PixelWriter
                pixelWriter.setColor(x, y, color);
            }
        }

        // Return the WritableImage
        return image;
    }

    private void saveTreesToPDF(WritableImage image) {
        // Show a directory chooser to let the user select the save location
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Save Location");
        File selectedDirectory = directoryChooser.showDialog(attackTreeView.getScene().getWindow());

        if (selectedDirectory != null) {
            try {
                // Create a new PDF document
                PDDocument document = new PDDocument();
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                // Convert the WritableImage to a BufferedImage
                BufferedImage bufferedImage = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_RGB);
                PixelReader pixelReader = image.getPixelReader();
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        bufferedImage.setRGB(x, y, pixelReader.getArgb(x, y));
                    }
                }

                // Create a PDImageXObject from the BufferedImage
                PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);

                // Add the image to the PDF page
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.drawImage(pdImage, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
                contentStream.close();

                // Save the PDF document to the selected directory
                File pdfFile = new File(selectedDirectory, "attack_tree.pdf");
                document.save(pdfFile);
                document.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Save Successful");
                alert.setHeaderText(null);
                alert.setContentText("Attack tree saved as PDF: " + pdfFile.getAbsolutePath());
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception
            }
        }
    }

    private void expandTree(TreeItem<String> root) {
        // Expand the current node
        root.setExpanded(true);

        // Recursively expand all child nodes
        for (TreeItem<String> child : root.getChildren()) {
            expandTree(child);
        }
    }

    @FXML
    private void onReturnClick(ActionEvent event) {
        DataManager.getInstance().clearAllData();
        DataManager.getInstance().clearOpenStages();
        try {
            // Load the FXML file of the landing scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("landing-scene.fxml"));
            Parent root = loader.load();

            // Get the controller of the landing scene
            LandingSceneController landingSceneController = loader.getController();

            // Get a list of all open windows
            List<Window> openWindows = Window.getWindows();

            // List of Card windows
            List<Stage> stagesToClose = new ArrayList<>();
            // Iterate through the open windows and close all open windows
            for (Window window : openWindows) {
                if (window instanceof Stage) {
                    Stage stage = (Stage) window;
                    stagesToClose.add(stage);
                }
            }
            closeAllWindows(stagesToClose);
            // Get the current stage from the event source
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene of the current stage to the landing scene
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(landingSceneController.SCENE_TITLE);

            // Show the stage if it's not already showing
            if (!currentStage.isShowing()) {
                currentStage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading error
        }
    }

    // Opens The survey window if it isn't already open, brings it into focus if it is.
    @FXML
    private void onContinueEvaluation(ActionEvent event) {
        // Hide the tree view scene, but store it
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        DataManager.getInstance().addOpenStage(currentStage);

        // Check if preparedness window is already open.
        // If it is, bring it into focus
        Stage preparednessStage = getPreparednessStage();
        if (preparednessStage != null) {
            preparednessStage.toFront();
            // close attack and weakness cards
            closeAllAttackCards();
            closeAllWeaknessCards();
        }
        // else create a new instance of the preparedness window
        else {
            // close attack and weakness cards
            closeAllAttackCards();
            closeAllWeaknessCards();

            currentStage.hide();

            System.out.println(currentEvaluation.getCriticalSystemName());
            System.out.println(retrievedEvaluationAssets.size());

            // TESTING
            for (EvaluationAsset evalAsset: retrievedEvaluationAssets) {
                System.out.println("Current Asset Name: "+evalAsset.getAssetName());
                System.out.println("Current Asset Eval ID: "+evalAsset.getEvaluationID());
            }

            // Navigate to the Tree Prompt Screen
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("preparedness-window.fxml"));
                Parent root = loader.load();
                PreparednessWindowController preparednessWindowController = loader.getController();

                // Pass data unto next scene
                preparednessWindowController.getCurrentEvaluation(currentEvaluation);
                preparednessWindowController.getEvaluationAssets(retrievedEvaluationAssets);
                preparednessWindowController.getGeneratedTree(attackTreeView);
                preparednessWindowController.getYearFrom(yearFrom);
                preparednessWindowController.getYearTo(yearTo);
                preparednessWindowController.initializeWithData();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle(preparednessWindowController.SCENE_TITLE);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                // Handle loading error
            }
        }
    }

    // Method to close all attack cards
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

    // Method to close all open Weakness Cards
    private void closeAllWeaknessCards() {
        // Get a list of all open windows
        List<Window> openWindows = Window.getWindows();

        // List of Card windows
        List<Stage> cardViewStages = new ArrayList<>();

        // Get the controller of the Evaluation End
        WeaknessNodeCardController weaknessNodeCardController = new WeaknessNodeCardController();

        // Iterate through the open windows and close all open windows
        for (Window window : openWindows) {
            if (window instanceof Stage) {
                Stage stage = (Stage) window;
                if (stage.getTitle().equals(weaknessNodeCardController.SCENE_TITLE)) {
                    cardViewStages.add(stage);
                }
            }
        }
        // Close All CardView Stages
        closeAllWindows(cardViewStages);
    }

    // Method to close all open windows
    public void closeAllWindows(List<Stage> stagesToClose) {
        for (Stage stage : stagesToClose) {
            stage.close();
        }
    }

    // Method to get the preparedness stage if it's already open
    private Stage getPreparednessStage() {
        // new Instance of the Preparedness Window controller to check the scene title
        PreparednessWindowController preparednessWindowController = new PreparednessWindowController();
        // Iterate through all open stages
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage) {
                Stage stage = (Stage) window;
                // Check if the stage is the preparedness window
                if (preparednessWindowController.SCENE_TITLE.equals(stage.getTitle())) {
                    return stage;
                }
            }
        }
        // If the preparedness window is not found, return null
        return null;
    }
}
