package com.example.studentmobileapp;

import com.example.studentmobileapp.model.OccupancySimulator;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application entry point.
 * Launches the Student dashboard as the primary window and opens the Staff dashboard
 * in a separate stage. Starts the OccupancySimulator to provide live occupancy data.
 */
public class MainAppApplication extends Application {
    private static Stage primaryStage;
    private static Stage staffStage;
    private static HostServices HOST_SERVICES;
    private static OccupancySimulator simulator;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        HOST_SERVICES = getHostServices();

        // Start the occupancy simulator
        simulator = OccupancySimulator.getInstance();
        simulator.start();

        // Load and show the Student dashboard (primary window)
        setRoot("/com/example/studentmobileapp/pages/main/stumain-page1.fxml");

        stage.setTitle("Student Mobile App - UTA Library");
        stage.setWidth(400);
        stage.setMinWidth(400);
        stage.setMaxWidth(400);
        stage.setHeight(790);
        stage.setMinHeight(790);
        stage.setMaxHeight(790);
        stage.setResizable(false);
        stage.show();

        // Open Staff dashboard in a separate window
        openStaffDashboard();
    }

    @Override
    public void stop() throws Exception {
        // Stop the simulator when the application closes
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

    /**
     * Opens the Staff dashboard in a separate stage.
     */
    public static void openStaffDashboard() {
        if (staffStage != null && staffStage.isShowing()) {
            staffStage.toFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                MainAppApplication.class.getResource("/com/example/staffdashwebsite/pages/maindash/maindash-page.fxml"));
            Parent root = loader.load();

            staffStage = new Stage();
            staffStage.setTitle("Staff Dashboard - UTA Library Occupancy");
            staffStage.setScene(new Scene(root, 1000, 700));
            staffStage.setMinWidth(800);
            staffStage.setMinHeight(600);
            staffStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load Staff dashboard: " + e.getMessage());
        }
    }

    /**
     * Closes the Staff dashboard window if open.
     */
    public static void closeStaffDashboard() {
        if (staffStage != null) {
            staffStage.close();
            staffStage = null;
        }
    }

    public static HostServices getHostServicesInstance() {
        return HOST_SERVICES;
    }

    public static OccupancySimulator getSimulator() {
        return simulator;
    }

    public static void main(String[] args) {
        launch(args);
    }
}