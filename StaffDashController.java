package com.example.studentmobileapp.pages.staff;

import com.example.studentmobileapp.MainAppApplication;
import com.example.studentmobileapp.model.OccupancyModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
 * Staff dashboard controller for library occupancy management.
 * Binds UI elements to the shared OccupancyModel.
 */
public class StaffDashController {

    @FXML private TableView<LogEntry> logsTable;
    @FXML private TextField manualEntryField;
    @FXML private Button checkInButton, checkOutButton;
    @FXML private ComboBox<String> centralFloorsCombo;
    @FXML private ComboBox<String> otherLibrariesCombo;
    @FXML private Hyperlink viewAllLink;

    @FXML private Button manualCheckButton, logsButton, logoutButton;

    // Occupancy display labels
    @FXML private Label totalOccupancyLabel;
    @FXML private Label floorOccupancyLabel;
    @FXML private Label currentStudentsLabel;

    private final ObservableList<LogEntry> logs = FXCollections.observableArrayList();
    private final FilteredList<LogEntry> filteredLogs = new FilteredList<>(logs, p -> true);

    private static final Path CHECKIN_FILE = Paths.get("checkin.txt");
    private static final Path CHECKOUT_FILE = Paths.get("checkout.txt");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String DEFAULT_FLOOR = OccupancyModel.FLOOR_NAMES[1]; // Floor1

    private final OccupancyModel occupancyModel = OccupancyModel.getInstance();

    @FXML
    public void initialize() {
        // Setup floor combo boxes
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

        setupTable();
        loadExistingLogs();
        bindOccupancyLabels();

        // Button actions
        if (checkInButton != null) checkInButton.setOnAction(e -> handleCheckIn());
        if (checkOutButton != null) checkOutButton.setOnAction(e -> handleCheckOut());

        if (manualCheckButton != null) manualCheckButton.setOnAction(this::handleManualCheckIn);
        if (logsButton != null) logsButton.setOnAction(this::handleLogs);
        if (logoutButton != null) logoutButton.setOnAction(this::handleLogout);

        // Hyperlinks
        if (viewAllLink != null) viewAllLink.setOnAction(e -> showInfo("View All", "Open full logs view (not implemented)."));
    }

    private void bindOccupancyLabels() {
        // Bind total occupancy label to overall percent
        if (totalOccupancyLabel != null) {
            totalOccupancyLabel.textProperty().bind(
                Bindings.createStringBinding(
                    () -> String.format("%.0f%%", occupancyModel.getOverallOccupancyPercent()),
                    occupancyModel.overallOccupancyPercentProperty()
                )
            );
        }

        // Bind current students label
        if (currentStudentsLabel != null) {
            currentStudentsLabel.textProperty().bind(
                Bindings.createStringBinding(
                    () -> String.format("%d / %d students",
                        occupancyModel.getCurrentStudents(),
                        occupancyModel.getMaxCapacity()),
                    occupancyModel.currentStudentsProperty(),
                    occupancyModel.maxCapacityProperty()
                )
            );
        }

        // Initial floor display
        updateFloorOccupancyDisplay();
    }

    private void updateFloorOccupancyDisplay() {
        if (floorOccupancyLabel == null || centralFloorsCombo == null) return;

        String selected = centralFloorsCombo.getValue();
        String floorKey = mapComboToFloorKey(selected);

        if (floorKey != null) {
            double percent = occupancyModel.getOccupancyPercent(floorKey);
            floorOccupancyLabel.setText(String.format("%.0f%%", percent));
        } else {
            // Show average or default
            double avg = calculateAverageFloorOccupancy();
            floorOccupancyLabel.setText(String.format("%.0f%%", avg));
        }
    }

    private String mapComboToFloorKey(String comboValue) {
        if (comboValue == null) return null;
        switch (comboValue) {
            case "Basement": return "Basement";
            case "1st Floor": return "Floor1";
            case "2nd Floor": return "Floor2";
            case "3rd Floor": return "Floor3";
            case "4th Floor": return "Floor4";
            case "5th Floor": return "Floor5";
            case "6th Floor": return "Floor6";
            default: return null;
        }
    }

    private double calculateAverageFloorOccupancy() {
        double sum = 0;
        int count = 0;
        for (String floor : OccupancyModel.FLOOR_NAMES) {
            sum += occupancyModel.getOccupancyPercent(floor);
            count++;
        }
        return count > 0 ? sum / count : 0;
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

    @FXML
    private void handleCheckIn() {
        final String raw = manualEntryField == null ? "" : manualEntryField.getText();
        if (raw == null || raw.trim().isEmpty()) {
            showWarning("Input required", "Please enter a student ID/email or name before checking in.");
            return;
        }

        // Get selected floor for occupancy model update, or use default
        String floorKey = getSelectedFloorKey();

        final String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        final String[] parsed = parseInput(raw);
        final String idOrEmail = parsed[0];
        final String name = parsed[1];
        final String line = String.join("|", timestamp, idOrEmail, name, "IN");
        final String finalFloorKey = floorKey;

        appendLineToFileAsync(CHECKIN_FILE, line, () -> {
            LogEntry e = new LogEntry("IN", idOrEmail, name, timestamp);
            logs.add(0, e);
            if (manualEntryField != null) manualEntryField.clear();

            // Update occupancy model (UI auto-updates via bindings)
            occupancyModel.checkIn(finalFloorKey);
        });
    }

    @FXML
    private void handleCheckOut() {
        final String raw = manualEntryField == null ? "" : manualEntryField.getText();
        if (raw == null || raw.trim().isEmpty()) {
            showWarning("Input required", "Please enter a student ID/email or name before checking out.");
            return;
        }

        // Get selected floor for occupancy model update, or use default
        String floorKey = getSelectedFloorKey();

        final String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        final String[] parsed = parseInput(raw);
        final String idOrEmail = parsed[0];
        final String name = parsed[1];
        final String line = String.join("|", timestamp, idOrEmail, name, "OUT");
        final String finalFloorKey = floorKey;

        appendLineToFileAsync(CHECKOUT_FILE, line, () -> {
            LogEntry e = new LogEntry("OUT", idOrEmail, name, timestamp);
            logs.add(0, e);
            if (manualEntryField != null) manualEntryField.clear();

            // Update occupancy model (UI auto-updates via bindings)
            occupancyModel.checkOut(finalFloorKey);
        });
    }

    /**
     * Gets the selected floor key from the combo box, or returns the default floor.
     */
    private String getSelectedFloorKey() {
        String floorKey = mapComboToFloorKey(centralFloorsCombo != null ? centralFloorsCombo.getValue() : null);
        return floorKey != null ? floorKey : DEFAULT_FLOOR;
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
