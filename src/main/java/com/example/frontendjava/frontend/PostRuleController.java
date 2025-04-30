package com.example.frontendjava.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class PostRuleController {

    private final FrontendService service = new FrontendService();
    private final VBox layout = new VBox(10);

    // UI Elements
    private final TextField nameField = new TextField();
    private final TextField descField = new TextField();
    private final TextField logicField = new TextField();
    private final CheckBox enabledBox = new CheckBox("Enabled");
    private final Spinner<Integer> thresholdSpinner = new Spinner<>(1, 1000, 10);
    private final Spinner<Integer> timeWindowSpinner = new Spinner<>(1, 3600, 1);

    public PostRuleController() {
        layout.setPadding(new Insets(10));

        Button submitButton = new Button("Add Detection Rule");
        submitButton.setOnAction(e -> submitRule());

        layout.getChildren().addAll(
                new Label("Rule Name:"), nameField,
                new Label("Description:"), descField,
                new Label("Rule Logic:"), logicField,
                enabledBox,
                new Label("Threshold:"), thresholdSpinner,
                new Label("Time Window (seconds):"), timeWindowSpinner,
                submitButton
        );
    }

    public VBox getPostRuleView() {
        return layout;
    }

    private void submitRule() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode ruleData = mapper.createObjectNode();
            ruleData.put("name", nameField.getText());
            ruleData.put("description", descField.getText());
            ruleData.put("ruleLogic", logicField.getText());
            ruleData.put("enabled", enabledBox.isSelected());
            ruleData.put("threshold", thresholdSpinner.getValue());
            ruleData.put("timeWindowSeconds", timeWindowSpinner.getValue());

            String json = mapper.writeValueAsString(ruleData);

            service.postNewDetectionRule(json, new FrontendService.FrontendCallback() {
                @Override
                public void onSuccess(String response) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Rule created successfully!");
                        alert.showAndWait();
                        clearForm();
                    });
                }

                @Override
                public void onError(Throwable throwable) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Error creating rule: " + throwable.getMessage());
                        alert.showAndWait();
                    });
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        nameField.clear();
        descField.clear();
        logicField.clear();
        enabledBox.setSelected(false);
        thresholdSpinner.getValueFactory().setValue(10);
        timeWindowSpinner.getValueFactory().setValue(1);
    }
}
