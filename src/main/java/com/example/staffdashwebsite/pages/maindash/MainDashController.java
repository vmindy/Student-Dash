package com.example.staffdashwebsite.pages.maindash;

import com.example.studentmobileapp.model.OccupancyModel;
import com.example.studentmobileapp.model.OccupancySimulator;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Staff dashboard controller that displays library occupancy data
 * bound to the shared OccupancyModel singleton.
 */
public class MainDashController {

    @FXML private TableView<LogEntry> logsTable;
    @FXML private TextField manualEntryField;
    @FXML private Button checkInButton, checkOutButton;
    @FXML private ComboBox<String> centralFloorsCombo;
    @FXML private ComboBox<String> otherLibrariesCombo;
    @FXML private Hyperlink viewAllLink;

    @FXML private Button manualCheckButton, logsButton, logoutButton;
    
    // Labels for occupancy display (bound to model)
    @FXML private Label totalOccupancyLabel;
    @FXML private Label floorOccupancyLabel;
    @FXML private Label currentStudentsLabel;
    @FXML private Label maxCapacityLabel;

    private final ObservableList<LogEntry> logs = FXCollections.observableArrayList();
    private final FilteredList<LogEntry> filteredLogs = new FilteredList<>(logs, p -> true);

    private static final Path CHECKIN_FILE = Paths.get("checkin.txt");
    private static final Path CHECKOUT_FILE = Paths.get("checkout.txt");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private OccupancyModel occupancyModel;
    private OccupancySimulator simulator;

    @FXML
    public void initialize() {
        // Get shared occupancy model
        occupancyModel = OccupancyModel.getInstance();
        simulator = OccupancySimulator.getInstance();

        // Setup floor selection combo boxes
        if (centralFloorsCombo != null) {
            centralFloorsCombo.getItems().addAll(
                    "Basement", "1st Floor", "2nd Floor", "3rd Floor", "4th Floor", "5th Floor", "6th Floor"
            );
            centralFloorsCombo.setOnAction(e -> updateFloorOccupancyDisplay());
        }
        if (otherLibrariesCombo != null) {
            otherLibrariesCombo.getItems().addAll(
                    "West Campus Library", "Science and Engineering Library"
            );
        }

        // Setup table
        setupTable();
        loadExistingLogs();

        // Button actions
        if (checkInButton != null) checkInButton.setOnAction(e -> handleCheckIn());
        if (checkOutButton != null) checkOutButton.setOnAction(e -> handleCheckOut());

        if (manualCheckButton != null) manualCheckButton.setOnAction(this::handleManualCheckIn);
        if (logsButton != null) logsButton.setOnAction(this::handleLogs);
        if (logoutButton != null) logoutButton.setOnAction(this::handleLogout);

        // Hyperlinks
        if (viewAllLink != null) viewAllLink.setOnAction(e -> showInfo("View All", "Open full logs view (not implemented)."));

        // Bind occupancy labels to model
        bindOccupancyLabels();
    }

    /**
     * Bind UI labels to the shared occupancy model properties.
     */
    private void bindOccupancyLabels() {
        // Bind total occupancy percentage
        if (totalOccupancyLabel != null) {
            occupancyModel.overallOccupancyPercentProperty().addListener((obs, oldVal, newVal) -> {
                totalOccupancyLabel.setText(String.format("%.0f%%", newVal.doubleValue()));
            });
            totalOccupancyLabel.setText(String.format("%.0f%%", occupancyModel.getOverallOccupancyPercent()));
        }

        // Bind current students count
        if (currentStudentsLabel != null) {
            occupancyModel.currentStudentsProperty().addListener((obs, oldVal, newVal) -> {
                currentStudentsLabel.setText(String.valueOf(newVal.intValue()));
            });
            currentStudentsLabel.setText(String.valueOf(occupancyModel.getCurrentStudents()));
        }

        // Bind max capacity
        if (maxCapacityLabel != null) {
            occupancyModel.maxCapacityProperty().addListener((obs, oldVal, newVal) -> {
                maxCapacityLabel.setText(String.valueOf(newVal.intValue()));
            });
            maxCapacityLabel.setText(String.valueOf(occupancyModel.getMaxCapacity()));
        }

        // Initial floor occupancy display
        updateFloorOccupancyDisplay();
    }

    /**
     * Update the floor occupancy label based on selected floor in combo box.
     */
    private void updateFloorOccupancyDisplay() {
        if (floorOccupancyLabel == null || centralFloorsCombo == null) {
            return;
        }

        String selectedFloor = centralFloorsCombo.getValue();
        if (selectedFloor == null || selectedFloor.isEmpty()) {
            // Show average or first floor
            floorOccupancyLabel.setText("--");
            return;
        }

        // Map combo selection to model floor name
        String floorKey = mapComboToFloorKey(selectedFloor);
        OccupancyModel.FloorData floorData = occupancyModel.getFloorData(floorKey);

        if (floorData != null) {
            floorOccupancyLabel.setText(String.format("%.0f%%", floorData.getOccupancyPercent()));
            // Add listener for live updates
            floorData.occupancyPercentProperty().addListener((obs, oldVal, newVal) -> {
                if (selectedFloor.equals(centralFloorsCombo.getValue())) {
                    floorOccupancyLabel.setText(String.format("%.0f%%", newVal.doubleValue()));
                }
            });
        }
    }

    private String mapComboToFloorKey(String comboValue) {
        if (comboValue == null) return "floor1";
        switch (comboValue) {
            case "Basement": return "basement";
            case "1st Floor": return "floor1";
            case "2nd Floor": return "floor2";
            case "3rd Floor": return "floor3";
            case "4th Floor": return "floor4";
            case "5th Floor": return "floor5";
            case "6th Floor": return "floor6";
            default: return "floor1";
        }
    }

    private void setupTable() {
        if (logsTable == null) return;

        logsTable.getColumns().clear();

        TableColumn<LogEntry, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(cell -> cell.getValue().actionProperty());
        actionCol.setPrefWidth(60);

        TableColumn<LogEntry, String> idCol = new TableColumn<>("Student ID / Guest Email");
        idCol.setCellValueFactory(cell -> cell.getValue().idOrEmailProperty());
        idCol.setPrefWidth(220);

        TableColumn<LogEntry, String> nameCol = new TableColumn<>("First and Last Name");
        nameCol.setCellValueFactory(cell -> cell.getValue().nameProperty());
        nameCol.setPrefWidth(220);

        TableColumn<LogEntry, String> timeCol = new TableColumn<>("Timestamp");
        timeCol.setCellValueFactory(cell -> cell.getValue().timestampProperty());
        timeCol.setPrefWidth(180);

        logsTable.getColumns().addAll(actionCol, idCol, nameCol, timeCol);
        logsTable.setItems(filteredLogs);
    }

    @FXML
    private void handleManualCheckIn(ActionEvent event) {
        if (manualEntryField != null) manualEntryField.requestFocus();
    }

    @FXML
    private void handleLogs(ActionEvent event) {
        showInfo("Logs", "Open full logs view (not implemented).");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Close the staff dashboard window
        if (logoutButton != null && logoutButton.getScene() != null) {
            logoutButton.getScene().getWindow().hide();
        }
    }

    /**
     * Gets the floor key based on the currently selected floor in the combo box,
     * defaulting to floor1 if nothing is selected.
     */
    private String getSelectedFloorKey() {
        String selectedFloor = centralFloorsCombo != null ? centralFloorsCombo.getValue() : null;
        return mapComboToFloorKey(selectedFloor != null ? selectedFloor : "1st Floor");
    }

    @FXML
    private void handleCheckIn() {
        final String raw = manualEntryField == null ? "" : manualEntryField.getText();
        if (raw == null || raw.trim().isEmpty()) {
            showWarning("Input required", "Please enter a student ID/email or name before checking in.");
            return;
        }
        final String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        final String[] parsed = parseInput(raw);
        final String idOrEmail = parsed[0];
        final String name = parsed[1];
        final String line = String.join("|", timestamp, idOrEmail, name, "IN");

        // Update occupancy model - check in to selected floor or default floor1
        simulator.manualCheckIn(getSelectedFloorKey());

        appendLineToFileAsync(CHECKIN_FILE, line, () -> {
            LogEntry e = new LogEntry("IN", idOrEmail, name, timestamp);
            logs.add(0, e);
            if (manualEntryField != null) manualEntryField.clear();
        });
    }

    @FXML
    private void handleCheckOut() {
        final String raw = manualEntryField == null ? "" : manualEntryField.getText();
        if (raw == null || raw.trim().isEmpty()) {
            showWarning("Input required", "Please enter a student ID/email or name before checking out.");
            return;
        }
        final String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        final String[] parsed = parseInput(raw);
        final String idOrEmail = parsed[0];
        final String name = parsed[1];
        final String line = String.join("|", timestamp, idOrEmail, name, "OUT");

        // Update occupancy model - check out from selected floor or default floor1
        simulator.manualCheckOut(getSelectedFloorKey());

        appendLineToFileAsync(CHECKOUT_FILE, line, () -> {
            LogEntry e = new LogEntry("OUT", idOrEmail, name, timestamp);
            logs.add(0, e);
            if (manualEntryField != null) manualEntryField.clear();
        });
    }

    private void appendLineToFileAsync(Path file, String line, Runnable onComplete) {
        CompletableFuture.runAsync(() -> {
            try {
                Path parent = file.getParent();
                if (parent != null && !Files.exists(parent)) {
                    Files.createDirectories(parent);
                }
                Files.write(file, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                Platform.runLater(onComplete);
            } catch (IOException ex) {
                ex.printStackTrace();
                Platform.runLater(() -> showError("File write error", "Unable to write to file: " + file + "\n" + ex.getMessage()));
            }
        });
    }

    private void loadExistingLogs() {
        CompletableFuture.runAsync(() -> {
            try {
                if (Files.exists(CHECKIN_FILE)) {
                    List<String> lines = Files.readAllLines(CHECKIN_FILE, StandardCharsets.UTF_8);
                    for (String l : lines) {
                        LogEntry entry = parseLineToLogEntry(l);
                        if (entry != null) Platform.runLater(() -> logs.add(entry));
                    }
                }
                if (Files.exists(CHECKOUT_FILE)) {
                    List<String> lines = Files.readAllLines(CHECKOUT_FILE, StandardCharsets.UTF_8);
                    for (String l : lines) {
                        LogEntry entry = parseLineToLogEntry(l);
                        if (entry != null) Platform.runLater(() -> logs.add(entry));
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                Platform.runLater(() -> showError("File read error", "Unable to read log files: " + ex.getMessage()));
            }
        });
    }

    private LogEntry parseLineToLogEntry(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4) return null;
        String timestamp = parts[0];
        String idOrEmail = parts[1];
        String name = parts[2];
        String action = parts[3];
        return new LogEntry(action, idOrEmail, name, timestamp);
    }

    private String[] parseInput(String raw) {
        String t = raw.trim();
        if (t.contains(",")) {
            String[] parts = t.split(",", 2);
            return new String[]{parts[0].trim(), parts[1].trim()};
        }
        if (t.contains("@")) {
            return new String[]{t, ""};
        }
        if (t.contains(" ")) {
            return new String[]{"", t};
        }
        return new String[]{t, ""};
    }

    // UI helpers
    private void showInfo(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        a.setHeaderText(title);
        a.show();
    }

    private void showWarning(String title, String message) {
        Alert a = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        a.setHeaderText(title);
        a.show();
    }

    private void showError(String title, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        a.setHeaderText(title);
        a.show();
    }

    // Table row model
    public static class LogEntry {
        private final SimpleStringProperty action;
        private final SimpleStringProperty idOrEmail;
        private final SimpleStringProperty name;
        private final SimpleStringProperty timestamp;

        public LogEntry(String action, String idOrEmail, String name, String timestamp) {
            this.action = new SimpleStringProperty(action == null ? "" : action);
            this.idOrEmail = new SimpleStringProperty(idOrEmail == null ? "" : idOrEmail);
            this.name = new SimpleStringProperty(name == null ? "" : name);
            this.timestamp = new SimpleStringProperty(timestamp == null ? "" : timestamp);
        }

        public String getAction() { return action.get(); }
        public SimpleStringProperty actionProperty() { return action; }

        public String getIdOrEmail() { return idOrEmail.get(); }
        public SimpleStringProperty idOrEmailProperty() { return idOrEmail; }

        public String getName() { return name.get(); }
        public SimpleStringProperty nameProperty() { return name; }

        public String getTimestamp() { return timestamp.get(); }
        public SimpleStringProperty timestampProperty() { return timestamp; }
    }
}
