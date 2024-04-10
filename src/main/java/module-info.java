module com.example.securityevaluationtool {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires org.json;
    requires org.apache.commons.csv;
    requires org.apache.pdfbox;
    requires java.desktop;

    opens com.example.securityevaluationtool to javafx.fxml;
    exports com.example.securityevaluationtool;
    exports com.example.securityevaluationtool.database;
    opens com.example.securityevaluationtool.database to javafx.fxml;
}