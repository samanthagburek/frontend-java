package com.example.frontendjava.frontend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class EditRulesViewController {

    private final FrontendService service = new FrontendService();
    private final VBox rulesContainer = new VBox(10);

    public VBox createView() {
        ScrollPane scrollPane = new ScrollPane(rulesContainer);
        scrollPane.setFitToWidth(true);

        VBox layout = new VBox(10, scrollPane);
        layout.setPadding(new Insets(10));
        layout.setPrefSize(600, 400);

        fetchRules();  // Automatically load rules when view is created

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
                            CheckBox enabledBox = new CheckBox("Enabled");
                            enabledBox.setSelected(rule.get("enabled").asBoolean());
                            Spinner<Integer> thresholdSpinner = new Spinner<>(1, 1000, rule.get("threshold").asInt());

                            ruleCard.getChildren().addAll(
                                    new Label("Name:"), nameField,
                                    new Label("Description:"), descField,
                                    enabledBox,
                                    new Label("Threshold:"), thresholdSpinner
                            );

                            int ruleId = rule.get("id").asInt();

                            Button saveButton = new Button("Save");
                            saveButton.setOnAction(e -> {
                                try {
                                    ObjectMapper localMapper = new ObjectMapper();
                                    ObjectNode patchData = localMapper.createObjectNode();

                                    if (!nameField.getText().equals(rule.get("name").asText())) {
                                        patchData.put("name", nameField.getText());
                                    }
                                    if (!descField.getText().equals(rule.get("description").asText())) {
                                        patchData.put("description", descField.getText());
                                    }
                                    if (enabledBox.isSelected() != rule.get("enabled").asBoolean()) {
                                        patchData.put("enabled", enabledBox.isSelected());
                                    }
                                    if (thresholdSpinner.getValue() != rule.get("threshold").asInt()) {
                                        patchData.put("threshold", thresholdSpinner.getValue());
                                    }

                                    String jsonPayload = localMapper.writeValueAsString(patchData);

                                    service.updateDetectionRulePatch(ruleId, jsonPayload, new FrontendService.FrontendCallback() {
                                        @Override
                                        public void onSuccess(String response) {
                                            Platform.runLater(() -> {
                                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Rule updated successfully!");
                                                alert.showAndWait();
                                            });
                                        }

                                        @Override
                                        public void onError(Throwable throwable) {
                                            Platform.runLater(() -> {
                                                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update rule: " + throwable.getMessage());
                                                alert.showAndWait();
                                            });
                                        }
                                    });
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            });

                            ruleCard.getChildren().add(saveButton);
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



