# Student-Dash

A JavaFX application for UTA Library seat occupancy tracking with both Student and Staff dashboards.

## Features

- **Student Dashboard**: View floor-by-floor occupancy percentages, navigate to floor details, and access useful library links
- **Staff Dashboard**: Manage check-ins/check-outs with batch count support, view logs, and monitor real-time occupancy across all floors
- **Shared Occupancy Model**: Both dashboards share a singleton OccupancyModel with live JavaFX property bindings
- **Real-time Simulation**: An OccupancySimulator seeds initial placeholder values and periodically updates occupancy data, keeping levels around ~50%

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

- **Java 11 or later** with JavaFX support, either:
  - OpenJDK with separate [OpenJFX SDK](https://openjfx.io/) added to the module path, or
  - A JDK distribution that includes JavaFX (e.g., Liberica Full JDK, Azul Zulu with JavaFX)
- Alternatively, Java 8 with built-in JavaFX support

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

### Batch Check-In/Check-Out Methods

- `checkIn(floor)` - Increment occupied seats for a floor by 1
- `checkOut(floor)` - Decrement occupied seats for a floor by 1
- `checkInToFloor(floor, count)` - Increment occupied seats by N visitors (batch check-in)
- `checkOutFromFloor(floor, count)` - Decrement occupied seats by N visitors (batch check-out)
- `seedPlaceholderValues(...)` - Initialize with test data

All batch methods:
- Adjust occupied seats within bounds `[0, totalSeats]`
- Automatically recalculate floor occupancy percentages
- Automatically recalculate overall `currentStudents` and `overallOccupancyPercent`
- Use `Platform.runLater` for thread-safe JavaFX property updates
- Are synchronized for atomic updates

## Staff Batch Check-In/Check-Out

The Staff Dashboard includes a **batch count spinner** that allows staff to check in or check out multiple visitors at once:

1. Enter a student ID/email or name in the text field
2. Select a floor from the "Central Library Floors" dropdown
3. Adjust the **Count** spinner (1-50) to set how many to check in/out
4. Click **Check In** or **Check Out** to apply the batch operation

### Changing the Default Staff Check-In Count

To change the default batch count (currently 1), edit `StaffDashController.java`:

```java
/** Default batch count for check-in/check-out operations. Adjust as needed. */
private static final int DEFAULT_BATCH_COUNT = 1;  // Change this value
```

## Simulator

The `OccupancySimulator`:
- Seeds initial placeholder values on startup (approximately 50% occupancy per floor)
- Uses a **nudge-toward-target** strategy to keep overall occupancy around ~50% of maxCapacity
- Updates occupancy every 5 seconds
- Has a 70% probability of nudging toward the 50% target, with 30% random variation
- Uses `Platform.runLater()` for thread-safe JavaFX property updates
- Uses the batch `checkInToFloor`/`checkOutFromFloor` methods for atomic updates
- Automatically starts when the app launches
- Stops gracefully when the app closes

### Changing the Simulator Target Occupancy

To change the target occupancy percentage (currently 50%), edit `OccupancySimulator.java`:

```java
/** Target occupancy percentage for the simulator (0.0 to 1.0). */
private static final double TARGET_OCCUPANCY_PERCENT = 0.50;  // Change this value
```

## Architecture Notes

- All floor controllers bind their `occupancyLabel` to the shared `OccupancyModel`
- Staff dashboard displays overall and floor-specific occupancy
- Check-in/check-out actions update the model, which triggers UI updates in both dashboards
- URL button handlers are preserved from the original Student-Dash implementation
- JavaFX properties and bindings ensure both dashboards update live without polling
