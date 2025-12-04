# Student-Dash
Student Dash

## Library Occupancy Dashboard (JavaFX)

A JavaFX-based mock implementation of library occupancy dashboards for monitoring student presence across different floors of a library.

### Features

- **Staff Dashboard**: Detailed view showing per-floor occupancy with progress bars, occupied/total seats, and percentages
- **Student Dashboard**: Simplified view showing overall students inside and library occupancy percentage
- **Real-time Simulation**: Mock data that updates every 2 seconds to simulate occupancy changes
- **Live Updates**: Both dashboards update automatically as occupancy data changes

### Occupancy Formulas

The dashboards use the following formulas to calculate occupancy:

1. **Floor Occupancy**: `occupiedSeats / totalSeats`
   - Example: If Floor 1 has 20 occupied seats out of 150 total, occupancy = 20/150 = 13.3%

2. **Overall Occupancy**: `totalCurrentStudents / maxCapacity`
   - Example: If 65 students are inside with a max capacity of 500, occupancy = 65/500 = 13.0%

### Running the Application

#### Prerequisites

- Java 11 or later with JavaFX support
- JavaFX SDK (if not bundled with your JDK)

#### Option 1: Using Module Path (Recommended for Java 11+)

```bash
# Set the path to your JavaFX SDK
export PATH_TO_FX=/path/to/javafx-sdk/lib

# Compile
javac --module-path $PATH_TO_FX --add-modules javafx.controls \
    -d out src/main/java/com/vmindy/library/*.java

# Run
java --module-path $PATH_TO_FX --add-modules javafx.controls \
    -cp out com.vmindy.library.MainFX
```

#### Option 2: Using Classpath (Legacy)

```bash
# Set classpath to include JavaFX JARs
export CLASSPATH=/path/to/javafx-sdk/lib/*

# Compile
javac -cp $CLASSPATH -d out src/main/java/com/vmindy/library/*.java

# Run
java -cp "out:$CLASSPATH" com.vmindy.library.MainFX
```

#### Option 3: Using an IDE

1. Open the project in your IDE (IntelliJ IDEA, Eclipse, etc.)
2. Configure JavaFX SDK in your project settings
3. Run `MainFX.java` as a Java Application

### Project Structure

```
src/main/java/com/vmindy/library/
├── OccupancyModel.java      # Data model with Floor class and occupancy calculations
├── OccupancyListener.java   # Interface for receiving occupancy updates
├── OccupancyService.java    # Singleton service managing data and simulation
├── StaffDashboardFX.java    # Staff dashboard UI with per-floor details
├── StudentDashboardFX.java  # Student dashboard UI with overall occupancy
└── MainFX.java              # Main application entry point
```

### Simulation Details

This is a **mock/simulation** implementation that does not integrate with real sensors. The simulation:

- Starts with 3 floors: Floor 1 (150 seats), Floor 2 (200 seats), Floor 3 (150 seats)
- Initial occupancy: 20, 30, and 15 students respectively
- Max capacity: 500 students
- Updates every 2 seconds with random occupancy changes (-3 to +3 per floor)
- Occasionally adjusts max capacity (10% chance per update)

### API Reference

The `OccupancyService` singleton provides:

- `getInstance()`: Get the service instance
- `addListener(OccupancyListener)`: Register for updates
- `removeListener(OccupancyListener)`: Unregister from updates
- `startSimulation()`: Start the mock simulation
- `stopSimulation()`: Stop the simulation
- `checkInToFloor(int floorIndex)`: Manually add a student to a floor
- `checkOutFromFloor(int floorIndex)`: Manually remove a student from a floor
- `shutdown()`: Clean up resources
