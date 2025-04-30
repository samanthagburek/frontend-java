package com.example.frontendjava.frontend;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StockThingJavaFXApp extends Application {

    private BorderPane root;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        // Navigation
        VBox sidebar = new VBox(10);
        Button editRulesButton = new Button("Edit Detection Rules");
        Button logsButton = new Button("Logs");
        Button alertsButton = new Button("Alerts");

        sidebar.getChildren().addAll(editRulesButton, logsButton, alertsButton);
        root.setLeft(sidebar);

        // Default page
//        MainViewController mainView = new MainViewController();
//        root.setCenter(mainView.createMainView());

        // Button actions
        editRulesButton.setOnAction(e -> {
            EditRulesViewController view = new EditRulesViewController();
            root.setCenter(view.createView());
        });

        logsButton.setOnAction(e -> {
            LogsViewController logsView = new LogsViewController();
            root.setCenter(logsView.createView());
        });

        alertsButton.setOnAction(e -> {
            AlertsViewController alertsView = new AlertsViewController();
            root.setCenter(alertsView.createView());
        });

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("StockThingJava Frontend");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


