module javafx.jdbc {
    requires javafx.fxml;
    requires javafx.controls;

    opens model.entities;
    opens model.services;
    opens gui;
    exports application;
}