package com.example.studentmobileapp.model;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Singleton model for library occupancy data.
 * Provides JavaFX observable properties for live UI bindings.
 */
public class OccupancyModel {

    private static OccupancyModel instance;

    // Overall building capacity
    private final IntegerProperty maxCapacity = new SimpleIntegerProperty(0);
    private final IntegerProperty currentStudents = new SimpleIntegerProperty(0);
    private final DoubleProperty overallOccupancyPercent = new SimpleDoubleProperty(0.0);

    // Per-floor data: floor name -> properties
    private final Map<String, IntegerProperty> totalSeatsMap = new HashMap<>();
    private final Map<String, IntegerProperty> occupiedSeatsMap = new HashMap<>();
    private final Map<String, DoubleProperty> occupancyPercentMap = new HashMap<>();

    // Floor names
    public static final String[] FLOOR_NAMES = {
        "Basement", "Floor1", "Floor2", "Floor3", "Floor4", "Floor5", "Floor6"
    };

    private OccupancyModel() {
        // Initialize per-floor properties
        for (String floor : FLOOR_NAMES) {
            totalSeatsMap.put(floor, new SimpleIntegerProperty(0));
            occupiedSeatsMap.put(floor, new SimpleIntegerProperty(0));
            occupancyPercentMap.put(floor, new SimpleDoubleProperty(0.0));

            // Add listener to recalculate floor percent when occupied or total changes
            IntegerProperty totalSeats = totalSeatsMap.get(floor);
            IntegerProperty occupiedSeats = occupiedSeatsMap.get(floor);
            DoubleProperty floorPercent = occupancyPercentMap.get(floor);

            totalSeats.addListener((obs, oldVal, newVal) -> recalculateFloorPercent(floor));
            occupiedSeats.addListener((obs, oldVal, newVal) -> {
                recalculateFloorPercent(floor);
                recalculateCurrentStudents();
            });
        }

        // Recalculate overall percent when currentStudents or maxCapacity changes
        currentStudents.addListener((obs, oldVal, newVal) -> recalculateOverallPercent());
        maxCapacity.addListener((obs, oldVal, newVal) -> recalculateOverallPercent());
    }

    public static synchronized OccupancyModel getInstance() {
        if (instance == null) {
            instance = new OccupancyModel();
        }
        return instance;
    }

    // --- Overall properties ---

    public IntegerProperty maxCapacityProperty() {
        return maxCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity.get();
    }

    public void setMaxCapacity(int value) {
        maxCapacity.set(value);
    }

    public IntegerProperty currentStudentsProperty() {
        return currentStudents;
    }

    public int getCurrentStudents() {
        return currentStudents.get();
    }

    public DoubleProperty overallOccupancyPercentProperty() {
        return overallOccupancyPercent;
    }

    public double getOverallOccupancyPercent() {
        return overallOccupancyPercent.get();
    }

    // --- Per-floor properties ---

    public IntegerProperty totalSeatsProperty(String floor) {
        return totalSeatsMap.get(floor);
    }

    public int getTotalSeats(String floor) {
        IntegerProperty prop = totalSeatsMap.get(floor);
        return prop != null ? prop.get() : 0;
    }

    public void setTotalSeats(String floor, int value) {
        IntegerProperty prop = totalSeatsMap.get(floor);
        if (prop != null) {
            prop.set(value);
        }
    }

    public IntegerProperty occupiedSeatsProperty(String floor) {
        return occupiedSeatsMap.get(floor);
    }

    public int getOccupiedSeats(String floor) {
        IntegerProperty prop = occupiedSeatsMap.get(floor);
        return prop != null ? prop.get() : 0;
    }

    public void setOccupiedSeats(String floor, int value) {
        IntegerProperty prop = occupiedSeatsMap.get(floor);
        if (prop != null) {
            prop.set(value);
        }
    }

    public DoubleProperty occupancyPercentProperty(String floor) {
        return occupancyPercentMap.get(floor);
    }

    public double getOccupancyPercent(String floor) {
        DoubleProperty prop = occupancyPercentMap.get(floor);
        return prop != null ? prop.get() : 0.0;
    }

    public Set<String> getFloorNames() {
        return totalSeatsMap.keySet();
    }

    // --- Check-in/Check-out ---

    /**
     * Check in a student on the specified floor.
     * Increments occupied seats for that floor.
     * Synchronized to prevent race conditions.
     */
    public synchronized void checkIn(String floor) {
        IntegerProperty prop = occupiedSeatsMap.get(floor);
        if (prop != null) {
            int totalSeats = getTotalSeats(floor);
            if (prop.get() < totalSeats) {
                prop.set(prop.get() + 1);
            }
        }
    }

    /**
     * Check out a student from the specified floor.
     * Decrements occupied seats for that floor.
     * Synchronized to prevent race conditions.
     */
    public synchronized void checkOut(String floor) {
        IntegerProperty prop = occupiedSeatsMap.get(floor);
        if (prop != null && prop.get() > 0) {
            prop.set(prop.get() - 1);
        }
    }

    // --- Recalculation methods ---

    private void recalculateFloorPercent(String floor) {
        int total = getTotalSeats(floor);
        int occupied = getOccupiedSeats(floor);
        double percent = total > 0 ? ((double) occupied / total) * 100.0 : 0.0;
        DoubleProperty prop = occupancyPercentMap.get(floor);
        if (prop != null) {
            prop.set(percent);
        }
    }

    private void recalculateCurrentStudents() {
        int sum = 0;
        for (String floor : FLOOR_NAMES) {
            sum += getOccupiedSeats(floor);
        }
        currentStudents.set(sum);
    }

    private void recalculateOverallPercent() {
        int max = maxCapacity.get();
        int current = currentStudents.get();
        double percent = max > 0 ? ((double) current / max) * 100.0 : 0.0;
        overallOccupancyPercent.set(percent);
    }

    /**
     * Seed placeholder values for testing.
     */
    public void seedPlaceholderValues(int maxCap, Map<String, int[]> floorData) {
        setMaxCapacity(maxCap);
        for (Map.Entry<String, int[]> entry : floorData.entrySet()) {
            String floor = entry.getKey();
            int[] data = entry.getValue();
            if (data.length >= 2) {
                setTotalSeats(floor, data[0]);
                setOccupiedSeats(floor, data[1]);
            }
        }
    }
}
