module javafx.jdbc {
    requires javafx.fxml;
    requires javafx.controls;

    opens gui;
    exports application;
}