package com.example.studentmobileapp;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainAppApplication extends Application {
    private static Stage primaryStage;
    private static HostServices HOST_SERVICES;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        HOST_SERVICES = getHostServices();

        setRoot("/com/example/studentmobileapp/pages/main/stumain-page1.fxml");

        stage.setTitle("Student Mobile App");
        stage.setWidth(400);
        stage.setMinWidth(400);
        stage.setMaxWidth(400);
        stage.setHeight(790);
        stage.setMinHeight(790);
        stage.setMaxHeight(790);
        stage.setResizable(false);
        stage.show();
    }

    public static void setRoot(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(MainAppApplication.class.getResource(fxmlPath));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static HostServices getHostServicesInstance() {
        return HOST_SERVICES;
    }
    public static void main(String[] args) {
        launch(args);
    }
}