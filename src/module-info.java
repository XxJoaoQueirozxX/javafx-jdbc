module javafx.jdbc {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;

    opens db;
    opens model.dao;
    opens model.entities;
    opens model.services;
    opens gui;
    exports application;
}