package com.example.frontendjava.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class MainViewController {

    private final VBox mainLayout = new VBox(20);
    private final Label statusLabel = new Label();

    public void clearLayout() {
        mainLayout.getChildren().clear(); // Clear all children from the VBox
    }

    public VBox createMainView(Stage stage) {
        clearLayout(); // Reset the layout
        mainLayout.setPadding(new Insets(40));
        mainLayout.setAlignment(Pos.CENTER);

        Label welcome = new Label("Welcome to the Network Intrusion Simulator");
        welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button uploadButton = new Button("Upload PCAP File");
        uploadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select PCAP File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PCAP Files", "*.pcap"));
            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                uploadPcapFile(selectedFile.toPath());
            }
        });

        Button simulateButton = new Button("Example Attack Simulation");
        simulateButton.setOnAction(e -> {
            statusLabel.setText("Simulation trigger requested.");
            simulateAttack();
        });

        mainLayout.getChildren().addAll(welcome, uploadButton, simulateButton, statusLabel);
        return mainLayout;
    }

    private void uploadPcapFile(Path filePath) {
        try {
            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();  // Random boundary string
            String CRLF = "\r\n";

            // Read file content
            byte[] fileBytes = Files.readAllBytes(filePath);
            String fileName = filePath.getFileName().toString();

            // Build multipart body manually
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream writer = new DataOutputStream(outputStream);

            // --- Start multipart/form-data ---
            writer.writeBytes("--" + boundary + CRLF);
            writer.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + CRLF);
            writer.writeBytes("Content-Type: application/octet-stream" + CRLF);
            writer.writeBytes(CRLF);
            writer.write(fileBytes);
            writer.writeBytes(CRLF);
            writer.writeBytes("--" + boundary + "--" + CRLF);  // End boundary
            writer.flush();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/pcap/upload"))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(outputStream.toByteArray()))
                    .build();

            HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> Platform.runLater(() -> {
                        statusLabel.setText("Success: " + response);
                        System.out.println("File upload successful: " + response);
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            statusLabel.setText("Upload failed: " + ex.getMessage());
                            System.out.println("Upload failed: " + ex.getMessage());
                        });
                        return null;
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
            Platform.runLater(() -> {
                statusLabel.setText("Exception: " + ex.getMessage());
                System.out.println("Exception during upload: " + ex.getMessage());
            });
        }
    }


    private void simulateAttack() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/rulesimulate/attack"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Simulation Complete");
                    alert.setHeaderText("Attack Simulated");
                    alert.setContentText("Detection alerts returned:\n" + response);
                    alert.showAndWait();
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Simulation Failed");
                        errorAlert.setContentText("Could not simulate attack: " + ex.getMessage());
                        errorAlert.showAndWait();
                    });
                    return null;
                });
    }

}
