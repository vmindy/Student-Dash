package com.vmindy.library;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Main JavaFX Application for the Library Occupancy Dashboard system.
 * 
 * This application creates and displays both the Staff Dashboard and Student Dashboard,
 * registers them with the OccupancyService, and starts the occupancy simulation.
 * 
 * This is a mock/simulation implementation using placeholder data.
 * It does not integrate with real sensors.
 */
public class MainFX extends Application {

    private OccupancyService occupancyService;
    private StaffDashboardFX staffDashboard;
    private StudentDashboardFX studentDashboard;

    /**
     * Main entry point for the JavaFX application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes and starts the application.
     * Creates both dashboards, registers them with the service, and starts the simulation.
     * 
     * @param primaryStage the primary stage (not used directly - dashboards create their own stages)
     */
    @Override
    public void start(Stage primaryStage) {
        // Get the singleton OccupancyService instance
        occupancyService = OccupancyService.getInstance();

        // Create the dashboard UIs
        staffDashboard = new StaffDashboardFX();
        studentDashboard = new StudentDashboardFX();

        // Register dashboards as listeners for occupancy updates
        occupancyService.addListener(staffDashboard);
        occupancyService.addListener(studentDashboard);

        // Position and show both dashboards
        staffDashboard.getStage().setX(50);
        staffDashboard.getStage().setY(100);
        staffDashboard.show();

        studentDashboard.getStage().setX(520);
        studentDashboard.getStage().setY(100);
        studentDashboard.show();

        // Perform initial update to populate dashboards with current data
        Platform.runLater(() -> {
            OccupancyModel model = occupancyService.getModel();
            staffDashboard.onOccupancyUpdated(model);
            studentDashboard.onOccupancyUpdated(model);
        });

        // Start the occupancy simulation
        occupancyService.startSimulation();

        // Register shutdown hook to clean up resources when closing the window
        staffDashboard.getStage().setOnCloseRequest(event -> shutdownApplication());
        studentDashboard.getStage().setOnCloseRequest(event -> shutdownApplication());

        System.out.println("Library Occupancy Dashboard System started.");
        System.out.println("Staff Dashboard and Student Dashboard are now active.");
        System.out.println("Simulation is running - occupancy values will update every 2 seconds.");
    }

    /**
     * Shuts down the application cleanly.
     * Stops the simulation, removes listeners, and exits the application.
     */
    private void shutdownApplication() {
        System.out.println("Shutting down application...");

        // Remove listeners
        if (occupancyService != null) {
            if (staffDashboard != null) {
                occupancyService.removeListener(staffDashboard);
            }
            if (studentDashboard != null) {
                occupancyService.removeListener(studentDashboard);
            }

            // Shutdown the service (stops simulation)
            occupancyService.shutdown();
        }

        // Close all stages
        if (staffDashboard != null && staffDashboard.getStage().isShowing()) {
            staffDashboard.getStage().close();
        }
        if (studentDashboard != null && studentDashboard.getStage().isShowing()) {
            studentDashboard.getStage().close();
        }

        // Exit the JavaFX application
        Platform.exit();
    }

    /**
     * Called when the application is stopping.
     * Ensures cleanup is performed if not already done.
     */
    @Override
    public void stop() {
        // Additional cleanup if needed
        if (occupancyService != null) {
            occupancyService.shutdown();
        }
    }
}
