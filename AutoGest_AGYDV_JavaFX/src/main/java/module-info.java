module agdv {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;
    requires retrofit2;
    requires retrofit2.converter.gson;
    requires java.sql;
    requires gson;
    requires javafx.swing;
    requires javafx.web;
    requires okhttp3;
    requires java.desktop;


    opens agdv to javafx.fxml;
    exports agdv;
    exports agdv.controller;
    opens agdv.controller to javafx.fxml;
    exports agdv.util.dialog;
    opens agdv.util.dialog to javafx.fxml;
    exports agdv.util;
    opens agdv.util to javafx.fxml;
    exports agdv.util.card;
    opens agdv.util.card to javafx.fxml;
    exports agdv.model;
    opens agdv.model to javafx.fxml;
    exports agdv.util.selfBackup;
    opens agdv.util.selfBackup to javafx.fxml;

}