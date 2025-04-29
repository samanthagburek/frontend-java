package com.example.frontendjava.frontend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

public class MainViewController {

    private final FrontendService service = new FrontendService();

    private VBox rulesContainer;

    public VBox createMainView() {
        Button fetchButton = new Button("Fetch Detection Rules");
        rulesContainer = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(rulesContainer);
        scrollPane.setFitToWidth(true);

        fetchButton.setOnAction(event -> fetchRules());

        VBox layout = new VBox(10, fetchButton, scrollPane);
        layout.setPadding(new Insets(10));
        layout.setPrefSize(600, 400);
        return layout;
    }

    private void fetchRules() {
        service.fetchDetectionRules(new FrontendService.FrontendCallback() {
            @Override
            public void onSuccess(String response) {
                Platform.runLater(() -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(response);
                        rulesContainer.getChildren().clear();

                        for (JsonNode rule : root) {
                            VBox ruleCard = new VBox(5);
                            ruleCard.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1;");

                            TextField nameField = new TextField(rule.get("name").asText());
                            TextField descField = new TextField(rule.get("description").asText());
                            TextField logicField = new TextField(rule.get("ruleLogic").asText());

                            CheckBox enabledBox = new CheckBox("Enabled");
                            enabledBox.setSelected(rule.get("enabled").asBoolean());

                            Spinner<Integer> thresholdSpinner = new Spinner<>(1, 1000, rule.get("threshold").asInt());
                            Spinner<Integer> timeWindowSpinner = new Spinner<>(1, 60, rule.get("timeWindowSeconds").asInt());

                            ruleCard.getChildren().addAll(
                                    new Label("Name:"), nameField,
                                    new Label("Description:"), descField,
                                    new Label("Logic:"), logicField,
                                    enabledBox,
                                    new Label("Threshold:"), thresholdSpinner,
                                    new Label("Time Window (seconds):"), timeWindowSpinner
                            );

                            rulesContainer.getChildren().add(ruleCard);
                        }

                    } catch (Exception e) {
                        rulesContainer.getChildren().setAll(new Label("Failed to parse response: " + e.getMessage()));
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> {
                    rulesContainer.getChildren().setAll(new Label("Error: " + throwable.getMessage()));
                });
            }
        });
    }
}
