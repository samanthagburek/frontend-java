package com.example.frontendjava.frontend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class AlertsViewController {

    private final FrontendService service = new FrontendService();
    private VBox alertsContainer;

    public VBox createView() {
        Button fetchButton = new Button("Fetch Alerts");
        alertsContainer = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(alertsContainer);
        scrollPane.setFitToWidth(true);

        fetchButton.setOnAction(event -> fetchAlerts());

        VBox layout = new VBox(10, fetchButton, scrollPane);
        layout.setPadding(new Insets(10));
        layout.setPrefSize(600, 400);
        return layout;
    }

    private void fetchAlerts() {
        service.fetchAlerts(new FrontendService.FrontendCallback() {
            @Override
            public void onSuccess(String response) {
                Platform.runLater(() -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(response);
                        alertsContainer.getChildren().clear();

                        for (JsonNode alert : root) {
                            VBox card = new VBox(5);
                            card.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1;");

                            String type = alert.path("alertType").asText();
                            String message = alert.path("message").asText();
                            String timestamp = alert.path("timestamp").asText();

                            Label typeLabel = new Label("Type: " + type);
                            Label messageLabel = new Label("Message: " + message);
                            Label timestampLabel = new Label("Timestamp: " + timestamp);

                            card.getChildren().addAll(typeLabel, messageLabel, timestampLabel);

                            // Optional: Show related rule ID and log entry ID
                            JsonNode ruleNode = alert.path("rule");
                            JsonNode logNode = alert.path("logEntry");

                            if (!ruleNode.isMissingNode() && ruleNode.has("id")) {
                                card.getChildren().add(new Label("Rule ID: " + ruleNode.get("id").asText()));
                            }
                            if (!logNode.isMissingNode() && logNode.has("id")) {
                                card.getChildren().add(new Label("Log Entry ID: " + logNode.get("id").asText()));
                            }

                            alertsContainer.getChildren().add(card);
                        }

                    } catch (Exception e) {
                        alertsContainer.getChildren().setAll(new Label("Failed to parse response: " + e.getMessage()));
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> {
                    alertsContainer.getChildren().setAll(new Label("Error: " + throwable.getMessage()));
                });
            }
        });
    }
}
