package com.vmindy.library;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Student Dashboard JavaFX UI for viewing simplified library occupancy.
 * 
 * Displays:
 * - Current number of students inside the library
 * - Overall occupancy percentage
 * - Formula explanation
 * 
 * Implements OccupancyListener to receive real-time updates.
 * All UI updates are performed on the JavaFX Application Thread.
 */
public class StudentDashboardFX implements OccupancyListener {

    private final Stage stage;
    private final Label currentInsideLabel;
    private final Label overallPercentLabel;
    private final Label formulaLabel;

    /**
     * Creates the Student Dashboard UI.
     */
    public StudentDashboardFX() {
        stage = new Stage();
        stage.setTitle("Student Dashboard - Library Occupancy");

        // Main container
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #e3f2fd;");

        // Title
        Label titleLabel = new Label("Library Occupancy");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        titleLabel.setStyle("-fx-text-fill: #1565C0;");

        // Subtitle
        Label subtitleLabel = new Label("Student View");
        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitleLabel.setStyle("-fx-text-fill: #666;");

        // Current students inside section
        VBox currentSection = new VBox(5);
        currentSection.setAlignment(Pos.CENTER);
        currentSection.setPadding(new Insets(20));
        currentSection.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label currentTitleLabel = new Label("Students Currently Inside");
        currentTitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        currentTitleLabel.setStyle("-fx-text-fill: #555;");

        currentInsideLabel = new Label("0");
        currentInsideLabel.setFont(Font.font("System", FontWeight.BOLD, 48));
        currentInsideLabel.setStyle("-fx-text-fill: #2196F3;");

        currentSection.getChildren().addAll(currentTitleLabel, currentInsideLabel);

        // Overall occupancy percentage section
        VBox percentSection = new VBox(5);
        percentSection.setAlignment(Pos.CENTER);
        percentSection.setPadding(new Insets(20));
        percentSection.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label percentTitleLabel = new Label("Overall Occupancy");
        percentTitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        percentTitleLabel.setStyle("-fx-text-fill: #555;");

        overallPercentLabel = new Label("0.0%");
        overallPercentLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
        overallPercentLabel.setStyle("-fx-text-fill: #FF5722;");

        percentSection.getChildren().addAll(percentTitleLabel, overallPercentLabel);

        // Formula explanation
        VBox formulaSection = new VBox(5);
        formulaSection.setAlignment(Pos.CENTER);
        formulaSection.setPadding(new Insets(10));

        Label formulaTitleLabel = new Label("How it's calculated:");
        formulaTitleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        formulaTitleLabel.setStyle("-fx-text-fill: #777;");

        formulaLabel = new Label("Overall = totalCurrentStudents / maxCapacity");
        formulaLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        formulaLabel.setStyle("-fx-text-fill: #888;");

        formulaSection.getChildren().addAll(formulaTitleLabel, formulaLabel);

        // Add all components to root
        root.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            currentSection,
            percentSection,
            formulaSection
        );

        Scene scene = new Scene(root, 320, 400);
        stage.setScene(scene);
    }

    /**
     * Shows the student dashboard stage.
     */
    public void show() {
        stage.show();
    }

    /**
     * Gets the stage for positioning purposes.
     * 
     * @return the stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Called when occupancy data is updated.
     * Updates the UI to reflect the new occupancy values.
     * This method is called on the JavaFX Application Thread.
     * 
     * @param model the updated occupancy model
     */
    @Override
    public void onOccupancyUpdated(OccupancyModel model) {
        int totalStudents = model.getTotalCurrentStudents();
        int maxCapacity = model.getMaxCapacity();
        double overallPercent = model.overallOccupancyPercent();

        // Update the current students count (bold primary number)
        currentInsideLabel.setText(String.valueOf(totalStudents));

        // Update the overall percentage
        overallPercentLabel.setText(String.format("%.1f%%", overallPercent));

        // Update the formula with actual values
        formulaLabel.setText(String.format(
            "Overall = %d / %d = %.1f%%",
            totalStudents, maxCapacity, overallPercent
        ));
    }
}
