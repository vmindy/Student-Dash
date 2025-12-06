package com.example.studentmobileapp;

import com.example.studentmobileapp.model.OccupancySimulator;
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
    private static OccupancySimulator simulator;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        HOST_SERVICES = getHostServices();

        // Start the occupancy simulator
        simulator = OccupancySimulator.getInstance();
        simulator.start();

        // Open Student dashboard (primary stage)
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

        // Open Staff dashboard in a second stage
        openStaffDashboard();
    }

    /**
     * Opens the Staff dashboard in a separate window.
     */
    private void openStaffDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(MainAppApplication.class.getResource(
                "/com/example/studentmobileapp/pages/staff/staff-dash.fxml"));
            Parent staffRoot = loader.load();

            Stage staffStage = new Stage();
            staffStage.setTitle("Staff Dashboard - Library Occupancy");
            staffStage.setScene(new Scene(staffRoot));
            staffStage.setWidth(900);
            staffStage.setHeight(700);
            staffStage.setResizable(true);
            staffStage.show();
        } catch (IOException e) {
            System.err.println("Failed to open Staff dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        // Stop the simulator when the app closes
        if (simulator != null) {
            simulator.stop();
        }
        super.stop();
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