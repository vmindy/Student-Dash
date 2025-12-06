# Student-Dash

A JavaFX application for UTA Library seat occupancy tracking with both Student and Staff dashboards.

## Features

- **Student Dashboard**: View floor-by-floor occupancy percentages, navigate to floor details, and access useful library links
- **Staff Dashboard**: Manage check-ins/check-outs, view logs, and monitor real-time occupancy across all floors
- **Shared Occupancy Model**: Both dashboards share a singleton OccupancyModel with live JavaFX property bindings
- **Real-time Simulation**: An OccupancySimulator seeds initial placeholder values and periodically updates occupancy data

## Project Structure

```
├── MainAppApplication.java     # Main JavaFX Application entry point
├── OccupancyModel.java         # Singleton model with JavaFX observable properties
├── OccupancySimulator.java     # Simulates occupancy changes for demo purposes
├── StaffDashController.java    # Staff dashboard controller
├── staff-dash.fxml             # Staff dashboard FXML layout
├── StuMainController.java      # Student main page controller
├── *Controller.java            # Floor-specific controllers (Basement, Floor1-6)
├── *.fxml                      # Floor-specific FXML layouts
└── README.md                   # This file
```

## Requirements

- Java 11 or later
- JavaFX SDK (included in Java 11+ or via OpenJFX)

## How to Run

### Option 1: Using an IDE (IntelliJ IDEA, Eclipse, VS Code)

1. Import the project into your IDE
2. Ensure JavaFX is configured in your project settings
3. Run `MainAppApplication.main()` as the entry point

### Option 2: Command Line with JavaFX Module Path

```bash
# Set JAVAFX_HOME to your JavaFX SDK location
export JAVAFX_HOME=/path/to/javafx-sdk

# Compile
javac --module-path $JAVAFX_HOME/lib --add-modules javafx.controls,javafx.fxml -d out *.java

# Run
java --module-path $JAVAFX_HOME/lib --add-modules javafx.controls,javafx.fxml -cp out com.example.studentmobileapp.MainAppApplication
```

### Option 3: Maven/Gradle (if you add build files)

Add JavaFX dependencies to your build configuration and run:
```bash
mvn javafx:run
# or
gradle run
```

## Occupancy Model Details

The `OccupancyModel` singleton provides:
- `IntegerProperty maxCapacity` - Maximum building capacity (default: 200)
- `IntegerProperty currentStudents` - Sum of all occupied seats across floors
- `DoubleProperty overallOccupancyPercent` - Calculated as `(currentStudents / maxCapacity) * 100`
- Per-floor properties:
  - `IntegerProperty totalSeats` - Total seats on each floor
  - `IntegerProperty occupiedSeats` - Currently occupied seats
  - `DoubleProperty occupancyPercent` - Calculated as `(occupiedSeats / totalSeats) * 100`

Methods:
- `checkIn(floor)` - Increment occupied seats for a floor
- `checkOut(floor)` - Decrement occupied seats for a floor
- `seedPlaceholderValues(...)` - Initialize with test data

## Simulator

The `OccupancySimulator`:
- Seeds initial placeholder values on startup
- Updates occupancy every 5 seconds with random changes
- Uses `Platform.runLater()` for thread-safe JavaFX property updates
- Automatically starts when the app launches
- Stops gracefully when the app closes

## Architecture Notes

- All floor controllers bind their `occupancyLabel` to the shared `OccupancyModel`
- Staff dashboard displays overall and floor-specific occupancy
- Check-in/check-out actions update the model, which triggers UI updates in both dashboards
- URL button handlers are preserved from the original Student-Dash implementation
