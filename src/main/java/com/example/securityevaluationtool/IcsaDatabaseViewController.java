package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.ICSAssetVulnerability;
import com.example.securityevaluationtool.database.ICSAssetVulnerabilityDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

//TODO: Show Product and Vendor, maybe add filter for Vendor and/or product.
public class IcsaDatabaseViewController {

    @FXML
    private TableView<ICSAssetVulnerability> table;

    @FXML
    private TableColumn<ICSAssetVulnerability, String> assetType;

    @FXML
    private TableColumn<ICSAssetVulnerability, Double> cumulativeCVSS;

    @FXML
    private TableColumn<ICSAssetVulnerability, String> cveNumber;

    @FXML
    private TableColumn<ICSAssetVulnerability, String> cvssSeverity;

    @FXML
    private TableColumn<ICSAssetVulnerability, String> cweNumber;

    @FXML
    private TableColumn<ICSAssetVulnerability, String> icsaAdvisoryTitle;

    @FXML
    private TableColumn<ICSAssetVulnerability, String> icsaCertNumber;

    @FXML
    private TableColumn<ICSAssetVulnerability, Integer> icsaid;

    @FXML
    private TableColumn<ICSAssetVulnerability, String> product;

    @FXML
    private TableColumn<ICSAssetVulnerability, Integer> releaseYear;

    @FXML
    private TableColumn<ICSAssetVulnerability, String> criticalInfrastructureSector;

    @FXML
    private TableColumn<ICSAssetVulnerability, String> vendor;

    @FXML
    private ComboBox<String> assetTypeComboBox;

    @FXML
    private ComboBox<String> cvssSeverityComboBox;

    @FXML
    private ComboBox<Integer> yearComboBox;

    private final ICSAssetVulnerabilityDAO icsAssetVulnerabilityDAO = new ICSAssetVulnerabilityDAO();

    private Integer selectedYear;
    private String selectedAssetType;
    private String selectedCVSSSeverity;

    @FXML
    private void initialize() {
        assetType.setCellValueFactory(new PropertyValueFactory<>("assetType"));
        cumulativeCVSS.setCellValueFactory(new PropertyValueFactory<>("cumulativeCVSS"));
        cveNumber.setCellValueFactory(new PropertyValueFactory<>("cveNumber"));
        cweNumber.setCellValueFactory(new PropertyValueFactory<>("cweNumber"));
        releaseYear.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        icsaAdvisoryTitle.setCellValueFactory(new PropertyValueFactory<>("icsCertAdvisoryTitle"));
        icsaid.setCellValueFactory(new PropertyValueFactory<>("icsadID"));
        icsaCertNumber.setCellValueFactory(new PropertyValueFactory<>("icsCertNumber"));
        product.setCellValueFactory(new PropertyValueFactory<>("product"));
        vendor.setCellValueFactory(new PropertyValueFactory<>("vendor"));
        criticalInfrastructureSector.setCellValueFactory(new PropertyValueFactory<>("criticalInfrastructureSector"));
        cvssSeverity.setCellValueFactory(new PropertyValueFactory<>("cvssSeverity"));

        // Call the DAO method to fetch data
        List<ICSAssetVulnerability> icsAssetVulnerabilities = icsAssetVulnerabilityDAO.getICSVulDataFromDB();

        // Populate the TableView with the data fetched initially
        table.getItems().addAll(icsAssetVulnerabilities);

        // Get options for year
        List<Integer> distinctYear = icsAssetVulnerabilityDAO.getDistinctYear();
        ObservableList<Integer> yearOptions = FXCollections.observableList(distinctYear);
        yearComboBox.setItems(yearOptions);

        // Get options for severity
        List<String> distinctSeverity = icsAssetVulnerabilityDAO.getDistinctSeverity();
        ObservableList<String> severityOptions = FXCollections.observableList(distinctSeverity);
        cvssSeverityComboBox.setItems(severityOptions);

        // Get options for asset types
        List<String> assetTypes = icsAssetVulnerabilityDAO.getAssetTypes();
        ObservableList<String> assetTypeOptions = FXCollections.observableList(assetTypes);
        assetTypeComboBox.setItems(assetTypeOptions);
    }

    @FXML
    private void onBackClick(ActionEvent event) {
        try {
            // Load the FXML file of the landing scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("landing-scene.fxml"));
            Parent root = loader.load();

            // Get the controller of the landing scene
            LandingSceneController landingSceneController = loader.getController();

            // Set the landing scene controller as the previous controller
            // This allows for communication between scenes if needed
            // landingSceneController.setPreviousController(this);

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

    @FXML
    private void onYearComboClick() {
        selectedYear = yearComboBox.getValue();
        updateTableView();
    }

    @FXML
    private void onSeverityComboClick() {
        selectedCVSSSeverity = cvssSeverityComboBox.getValue();
        updateTableView();
    }

    @FXML
    private void onAssetTypeComboClick() {
        selectedAssetType = assetTypeComboBox.getValue();
        updateTableView();
    }

    private void updateTableView() {
        // Construct SQL query dynamically based on selected filter criteria
        String query = "SELECT ICSAID, Year, CVENumber, CWENumber, InfrastructureSector, CVSSSeverity, \n" +
                "       CumulativeCVSS, ICSCertAdvisoryTitle, ICSCertNumber, Product, AssetType, Vendor\n" +
                "FROM ICSAssetVulnerability " +
                "WHERE 1 = 1"; // Start with a base query

        // Add filter criteria based on selected values
        if (selectedYear != null) {
            query += "\nAND Year = ?";
        }
        if (selectedAssetType != null) {
            query += "\nAND assetType = ?";
        }
        if (selectedCVSSSeverity != null) {
            query += "\nAND cvssSeverity = ?";
        }

        // Fetch filtered data from the database based on constructed query and parameters
        List<ICSAssetVulnerability> filteredData = icsAssetVulnerabilityDAO.getFilteredData(query, selectedYear, selectedAssetType, selectedCVSSSeverity);

        // Clear existing data in the TableView
        table.getItems().clear();

        // Populate TableView with filtered data
        table.getItems().addAll(filteredData);
    }
}
