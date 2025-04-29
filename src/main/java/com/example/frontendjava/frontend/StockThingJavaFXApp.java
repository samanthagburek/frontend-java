package com.example.frontendjava.frontend;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StockThingJavaFXApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainViewController controller = new MainViewController();
        Scene scene = new Scene(controller.createMainView());

        primaryStage.setTitle("StockThingJava Frontend");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

