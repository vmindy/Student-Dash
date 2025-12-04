package com.vmindy.library;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

/**
 * Staff Dashboard JavaFX UI for viewing detailed library occupancy.
 * 
 * Displays:
 * - Per-floor occupancy with name, progress bar, and percentage
 * - Overall library occupancy statistics
 * - Formulas used for calculations
 * 
 * Implements OccupancyListener to receive real-time updates.
 * All UI updates are performed on the JavaFX Application Thread.
 */
public class StaffDashboardFX implements OccupancyListener {

    private final Stage stage;
    private final ListView<HBox> floorListView;
    private final Label totalStudentsLabel;
    private final Label maxCapacityLabel;
    private final Label overallPercentLabel;
    private final Label overallFormulaLabel;

    /**
     * Creates the Staff Dashboard UI.
     */
    public StaffDashboardFX() {
        stage = new Stage();
        stage.setTitle("Staff Dashboard - Library Occupancy");

        // Main container
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Title
        Label titleLabel = new Label("Library Occupancy - Staff View");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setStyle("-fx-text-fill: #333;");

        // Floor occupancy section header
        Label floorHeader = new Label("Floor-by-Floor Occupancy");
        floorHeader.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
        floorHeader.setStyle("-fx-text-fill: #555;");

        // Floor list view
        floorListView = new ListView<>();
        floorListView.setPrefHeight(180);
        floorListView.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");

        // Formula explanation for floor occupancy
        Label floorFormulaLabel = new Label("Formula: Floor Occupancy = occupiedSeats / totalSeats");
        floorFormulaLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        floorFormulaLabel.setStyle("-fx-text-fill: #777;");

        // Overall statistics section
        Label overallHeader = new Label("Overall Library Statistics");
        overallHeader.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
        overallHeader.setStyle("-fx-text-fill: #555;");

        // Current students
        HBox totalRow = new HBox(10);
        totalRow.setAlignment(Pos.CENTER_LEFT);
        Label totalLabel = new Label("Current Students:");
        totalStudentsLabel = new Label("0");
        totalStudentsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        totalStudentsLabel.setStyle("-fx-text-fill: #2196F3;");
        totalRow.getChildren().addAll(totalLabel, totalStudentsLabel);

        // Max capacity
        HBox capacityRow = new HBox(10);
        capacityRow.setAlignment(Pos.CENTER_LEFT);
        Label capacityLabel = new Label("Max Capacity:");
        maxCapacityLabel = new Label("0");
        maxCapacityLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        maxCapacityLabel.setStyle("-fx-text-fill: #4CAF50;");
        capacityRow.getChildren().addAll(capacityLabel, maxCapacityLabel);

        // Overall percentage
        HBox percentRow = new HBox(10);
        percentRow.setAlignment(Pos.CENTER_LEFT);
        Label percentLabel = new Label("Overall Occupancy:");
        overallPercentLabel = new Label("0.0%");
        overallPercentLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        overallPercentLabel.setStyle("-fx-text-fill: #FF5722;");
        percentRow.getChildren().addAll(percentLabel, overallPercentLabel);

        // Overall formula
        overallFormulaLabel = new Label("Formula: Overall = totalCurrentStudents / maxCapacity = 0 / 0 = 0.0%");
        overallFormulaLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        overallFormulaLabel.setStyle("-fx-text-fill: #777;");

        // Add all components to root
        root.getChildren().addAll(
            titleLabel,
            floorHeader,
            floorListView,
            floorFormulaLabel,
            overallHeader,
            totalRow,
            capacityRow,
            percentRow,
            overallFormulaLabel
        );

        Scene scene = new Scene(root, 450, 480);
        stage.setScene(scene);
    }

    /**
     * Shows the staff dashboard stage.
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
        // Update floor list
        updateFloorList(model.getFloors());

        // Update overall statistics
        int totalStudents = model.getTotalCurrentStudents();
        int maxCapacity = model.getMaxCapacity();
        double overallPercent = model.overallOccupancyPercent();

        totalStudentsLabel.setText(String.valueOf(totalStudents));
        maxCapacityLabel.setText(String.valueOf(maxCapacity));
        overallPercentLabel.setText(String.format("%.1f%%", overallPercent));

        // Update formula display with actual values
        overallFormulaLabel.setText(String.format(
            "Formula: Overall = totalCurrentStudents / maxCapacity = %d / %d = %.1f%%",
            totalStudents, maxCapacity, overallPercent
        ));
    }

    /**
     * Updates the floor list view with current floor data.
     * 
     * @param floors the list of floors to display
     */
    private void updateFloorList(List<OccupancyModel.Floor> floors) {
        floorListView.getItems().clear();

        for (OccupancyModel.Floor floor : floors) {
            HBox row = createFloorRow(floor);
            floorListView.getItems().add(row);
        }
    }

    /**
     * Creates a row for displaying a single floor's occupancy.
     * 
     * @param floor the floor to display
     * @return HBox containing the floor display elements
     */
    private HBox createFloorRow(OccupancyModel.Floor floor) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5));

        // Floor name
        Label nameLabel = new Label(floor.getName());
        nameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        nameLabel.setPrefWidth(80);

        // Progress bar showing occupancy
        ProgressBar progressBar = new ProgressBar(floor.getOccupancyFraction());
        progressBar.setPrefWidth(150);
        progressBar.setPrefHeight(20);
        HBox.setHgrow(progressBar, Priority.ALWAYS);

        // Occupancy text: occupied/total (percent)
        String occupancyText = String.format("%d / %d (%.1f%%)",
            floor.getOccupiedSeats(),
            floor.getTotalSeats(),
            floor.getOccupancyPercent()
        );
        Label occupancyLabel = new Label(occupancyText);
        occupancyLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        occupancyLabel.setPrefWidth(120);

        row.getChildren().addAll(nameLabel, progressBar, occupancyLabel);
        return row;
    }
}
