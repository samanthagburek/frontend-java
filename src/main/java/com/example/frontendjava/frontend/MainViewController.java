package com.example.frontendjava.frontend;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class MainViewController {

    private final FrontendService service = new FrontendService();

    private TextArea outputArea;

    public VBox createMainView() {
        Button fetchButton = new Button("Fetch Detection Rules");
        outputArea = new TextArea();

        fetchButton.setOnAction(event -> fetchRules());

        VBox layout = new VBox(10, fetchButton, outputArea);
        layout.setPrefSize(600, 400);
        return layout;
    }

    private void fetchRules() {
        service.fetchDetectionRules(new FrontendService.FrontendCallback() {
            @Override
            public void onSuccess(String response) {
                Platform.runLater(() -> outputArea.setText(response));
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> outputArea.setText("Error: " + throwable.getMessage()));
            }
        });
    }
}
