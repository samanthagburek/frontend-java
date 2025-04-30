package com.example.frontendjava.frontend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class LogsViewController {

    private final FrontendService service = new FrontendService();
    private final VBox logContainer = new VBox(10);

    public VBox createView() {
        ScrollPane scrollPane = new ScrollPane(logContainer);
        scrollPane.setFitToWidth(true);

        VBox layout = new VBox(10, scrollPane);
        layout.setPadding(new Insets(10));
        layout.setPrefSize(800, 600);

        fetchLogEntries();

        return layout;
    }

    private void fetchLogEntries() {
        service.fetchLogEntries(new FrontendService.FrontendCallback() {
            @Override
            public void onSuccess(String response) {
                Platform.runLater(() -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(response);
                        logContainer.getChildren().clear();

                        for (JsonNode log : root) {
                            VBox logCard = new VBox(5);
                            logCard.setStyle("-fx-padding: 10; -fx-border-color: #999; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-color: #f9f9f9;");

                            String logText = String.format(
                                    "Timestamp: %s\nSource: %s:%d\nDestination: %s:%d\nProtocol: %s\nPacket Size: %d",
                                    log.get("timestamp").asText(),
                                    log.get("sourceIp").asText(), log.get("sourcePort").asInt(),
                                    log.get("destinationIp").asText(), log.get("destinationPort").asInt(),
                                    log.get("protocol").asText(),
                                    log.get("packetSize").asInt()
                            );

                            Label logLabel = new Label(logText);
                            logLabel.setWrapText(true);

                            logCard.getChildren().add(logLabel);

                            // If ICMP info exists
                            if (log.hasNonNull("icmpType") && log.hasNonNull("icmpCode")) {
                                Label icmpLabel = new Label("ICMP Type: " + log.get("icmpType").asInt() + ", Code: " + log.get("icmpCode").asInt());
                                logCard.getChildren().add(icmpLabel);
                            }

                            // Show alert info if available
                            if (log.has("alerts") && log.get("alerts").isArray() && log.get("alerts").size() > 0) {
                                Label alertsLabel = new Label("⚠ Alerts: " + log.get("alerts").size());
                                logCard.getChildren().add(alertsLabel);
                            }

                            logContainer.getChildren().add(logCard);
                        }

                    } catch (Exception e) {
                        logContainer.getChildren().setAll(new Label("Failed to parse logs: " + e.getMessage()));
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> {
                    logContainer.getChildren().setAll(new Label("Error fetching logs: " + throwable.getMessage()));
                });
            }
        });
    }
}
