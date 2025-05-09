package com.example.frontendjava.frontend;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StockThingJavaFXApp extends Application {

    private BorderPane root;
    private MainViewController mainView; // Store reference to the MainViewController
    private Button homeButton; // Home button to toggle visibility

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        // Navigation
        VBox sidebar = new VBox(10);
        Button editRulesButton = new Button("Edit Detection Rules");
        Button addRulesButton = new Button("Add Detection Rules");
        Button logsButton = new Button("Logs");
        Button alertsButton = new Button("Alerts");
        homeButton = new Button("Home"); // Button to go back to file upload view
        homeButton.setVisible(false); // Initially hide the Home button

        sidebar.getChildren().addAll(editRulesButton, addRulesButton, logsButton, alertsButton, homeButton);
        root.setLeft(sidebar);

        // Default page
        MainViewController mainView = new MainViewController();
        root.setCenter(mainView.createMainView(primaryStage));

        // Button actions
        editRulesButton.setOnAction(e -> {
            EditRulesViewController view = new EditRulesViewController();
            root.setCenter(view.createView());
            homeButton.setVisible(true);
        });

        addRulesButton.setOnAction(e -> {
            PostRuleController view = new PostRuleController();
            root.setCenter(view.getPostRuleView());
            homeButton.setVisible(true);

        });

        logsButton.setOnAction(e -> {
            LogsViewController logsView = new LogsViewController();
            root.setCenter(logsView.createView());
            homeButton.setVisible(true);
        });

        alertsButton.setOnAction(e -> {
            AlertsViewController alertsView = new AlertsViewController();
            root.setCenter(alertsView.createView());
            homeButton.setVisible(true);
        });

        // Home button action
        homeButton.setOnAction(e -> {
            root.setCenter(mainView.createMainView(primaryStage));
            homeButton.setVisible(false); // Hide the Home button when on Main View
        });


        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Pcap Checker Frontend");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


