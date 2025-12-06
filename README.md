# Student-Dash

A JavaFX application for UTA Library occupancy tracking, featuring both Student and Staff dashboards with live, shared occupancy data.

## Features

- **Student Dashboard**: Mobile-style interface showing floor-by-floor occupancy percentages with navigation to floor details and external library resources
- **Staff Dashboard**: Desktop-style administrative interface with manual check-in/check-out, occupancy metrics, and activity logs
- **Shared Occupancy Model**: Both dashboards display the same live occupancy data using JavaFX observable properties
- **Occupancy Simulator**: Background simulation of student check-ins/check-outs to demonstrate live data updates

## Project Structure

```
src/main/java/
├── com/example/studentmobileapp/
│   ├── MainAppApplication.java          # Main entry point
│   ├── model/
│   │   ├── OccupancyModel.java          # Shared occupancy data model (singleton)
│   │   └── OccupancySimulator.java      # Background activity simulator
│   └── pages/                           # Student UI controllers
│       ├── main/StuMainController.java
│       ├── basement/BasementController.java
│       ├── floor1/Floor1Controller.java
│       ├── floor2/Floor2Controller.java
│       ├── floor3/Floor3Controller.java
│       ├── floor4/SeatOccupancyController.java
│       ├── floor5/Floor5Controller.java
│       ├── floor6/Floor6Controller.java
│       ├── favorites/MiddleController.java
│       └── settings/SettingsController.java
└── com/example/staffdashwebsite/
    └── pages/maindash/MainDashController.java  # Staff dashboard controller

src/main/resources/
├── com/example/studentmobileapp/pages/  # Student FXML files
└── com/example/staffdashwebsite/pages/  # Staff FXML files
```

## Running the Application

### Prerequisites
- Java 11+ with JavaFX support
- Maven or Gradle (optional, for build automation)

### Running with Java directly
```bash
# Compile and run (ensure JavaFX is in the module path)
javac --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -d out src/main/java/**/*.java
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp out com.example.studentmobileapp.MainAppApplication
```

### Running with Maven (if configured)
```bash
mvn javafx:run
```

## How It Works

1. **On startup**, the application:
   - Starts the `OccupancySimulator` which seeds initial placeholder data
   - Opens the Student dashboard as the primary window
   - Opens the Staff dashboard in a separate window

2. **The OccupancyModel** provides:
   - `maxCapacity` (default: 200 students)
   - `currentStudents` (sum of all floor occupied seats)
   - Per-floor `totalSeats`, `occupiedSeats`, and `occupancyPercent`
   - `overallOccupancyPercent` = (currentStudents / maxCapacity) * 100

3. **The Simulator**:
   - Seeds initial occupancy values per floor
   - Every 3 seconds, randomly checks in or checks out a student on a random floor
   - Updates happen on the JavaFX thread via `Platform.runLater()`

## Adjusting Simulator Values

To modify the initial seed values or simulation behavior, edit `OccupancySimulator.java`:

```java
// Initial seed values per floor: {totalSeats, initialOccupied}
private static final int[][] FLOOR_SEEDS = {
    {25, 19},  // basement
    {30, 28},  // floor1
    {35, 28},  // floor2
    {30, 20},  // floor3
    {25, 17},  // floor4
    {30, 14},  // floor5
    {25, 8}    // floor6
};

private static final int DEFAULT_MAX_CAPACITY = 200;
private static final int SIMULATION_INTERVAL_SECONDS = 3;
```

## Notes

- All student floor URL button handlers are preserved and unchanged
- The student UI layout and design remain exactly as originally designed
- Both dashboards update live without polling (using JavaFX property bindings)
- The Staff dashboard's check-in/check-out buttons update the shared model
