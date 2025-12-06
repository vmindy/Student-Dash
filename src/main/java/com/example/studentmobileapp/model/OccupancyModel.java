package com.example.studentmobileapp.model;

import javafx.beans.property.*;
import javafx.application.Platform;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton model that tracks library occupancy for all floors.
 * Exposes JavaFX Properties for UI binding, and automatically recalculates
 * derived values (floor occupancy %, overall occupancy %) when underlying values change.
 */
public class OccupancyModel {

    private static OccupancyModel instance;

    // Overall capacity
    private final IntegerProperty maxCapacity = new SimpleIntegerProperty(200);
    private final IntegerProperty currentStudents = new SimpleIntegerProperty(0);
    private final DoubleProperty overallOccupancyPercent = new SimpleDoubleProperty(0.0);

    // Floor names - indices match the floor names
    private static final String[] FLOOR_NAMES = {
        "basement", "floor1", "floor2", "floor3", "floor4", "floor5", "floor6"
    };

    // Per-floor data: maps floor name to FloorData
    private final Map<String, FloorData> floorDataMap = new HashMap<>();

    /**
     * Per-floor data container with observable properties.
     */
    public static class FloorData {
        private final IntegerProperty totalSeats;
        private final IntegerProperty occupiedSeats;
        private final DoubleProperty occupancyPercent;

        public FloorData(int totalSeats) {
            this.totalSeats = new SimpleIntegerProperty(totalSeats);
            this.occupiedSeats = new SimpleIntegerProperty(0);
            this.occupancyPercent = new SimpleDoubleProperty(0.0);
        }

        public IntegerProperty totalSeatsProperty() { return totalSeats; }
        public int getTotalSeats() { return totalSeats.get(); }
        public void setTotalSeats(int value) { totalSeats.set(value); }

        public IntegerProperty occupiedSeatsProperty() { return occupiedSeats; }
        public int getOccupiedSeats() { return occupiedSeats.get(); }
        public void setOccupiedSeats(int value) { occupiedSeats.set(value); }

        public DoubleProperty occupancyPercentProperty() { return occupancyPercent; }
        public double getOccupancyPercent() { return occupancyPercent.get(); }
    }

    private OccupancyModel() {
        // Initialize floor data with default total seats
        for (String floorName : FLOOR_NAMES) {
            FloorData fd = new FloorData(30); // Default 30 seats per floor
            floorDataMap.put(floorName, fd);

            // Add listener to recalculate when occupied seats change
            fd.occupiedSeatsProperty().addListener((obs, oldVal, newVal) -> {
                recalculateFloorPercent(floorName);
                recalculateCurrentStudents();
            });

            // Add listener to recalculate when total seats change
            fd.totalSeatsProperty().addListener((obs, oldVal, newVal) -> {
                recalculateFloorPercent(floorName);
            });
        }

        // Add listeners to recalculate overall percent when currentStudents or maxCapacity changes
        currentStudents.addListener((obs, oldVal, newVal) -> recalculateOverallPercent());
        maxCapacity.addListener((obs, oldVal, newVal) -> recalculateOverallPercent());
    }

    /**
     * Returns the singleton instance.
     */
    public static synchronized OccupancyModel getInstance() {
        if (instance == null) {
            instance = new OccupancyModel();
        }
        return instance;
    }

    // ============ Overall properties ============

    public IntegerProperty maxCapacityProperty() { return maxCapacity; }
    public int getMaxCapacity() { return maxCapacity.get(); }
    public void setMaxCapacity(int value) { maxCapacity.set(value); }

    public IntegerProperty currentStudentsProperty() { return currentStudents; }
    public int getCurrentStudents() { return currentStudents.get(); }

    public DoubleProperty overallOccupancyPercentProperty() { return overallOccupancyPercent; }
    public double getOverallOccupancyPercent() { return overallOccupancyPercent.get(); }

    // ============ Floor-specific methods ============

    /**
     * Get FloorData for the specified floor.
     * Floor names are case-insensitive.
     * 
     * @param floorName the name of the floor (e.g., "basement", "floor1", etc.)
     * @return the FloorData for the floor, or null if the floor name is invalid
     */
    public FloorData getFloorData(String floorName) {
        if (floorName == null) {
            return null;
        }
        return floorDataMap.get(floorName.toLowerCase());
    }

    /**
     * Get floor names array.
     */
    public String[] getFloorNames() {
        return FLOOR_NAMES.clone();
    }

    /**
     * Check in a student on a specific floor (increments occupied seats).
     * Does nothing if the floor name is invalid or if the floor is at capacity.
     * 
     * @param floorName the name of the floor (case-insensitive)
     */
    public void checkIn(String floorName) {
        if (floorName == null) {
            return;
        }
        FloorData fd = floorDataMap.get(floorName.toLowerCase());
        if (fd != null && fd.getOccupiedSeats() < fd.getTotalSeats()) {
            fd.setOccupiedSeats(fd.getOccupiedSeats() + 1);
        }
    }

    /**
     * Check out a student from a specific floor (decrements occupied seats).
     * Does nothing if the floor name is invalid or if no students are present.
     * 
     * @param floorName the name of the floor (case-insensitive)
     */
    public void checkOut(String floorName) {
        if (floorName == null) {
            return;
        }
        FloorData fd = floorDataMap.get(floorName.toLowerCase());
        if (fd != null && fd.getOccupiedSeats() > 0) {
            fd.setOccupiedSeats(fd.getOccupiedSeats() - 1);
        }
    }

    /**
     * Seed placeholder values for a floor.
     * Does nothing if the floor name is invalid.
     * 
     * @param floorName the name of the floor (case-insensitive)
     * @param totalSeats the total number of seats on the floor
     * @param occupiedSeats the initial number of occupied seats
     */
    public void seedFloor(String floorName, int totalSeats, int occupiedSeats) {
        if (floorName == null) {
            return;
        }
        FloorData fd = floorDataMap.get(floorName.toLowerCase());
        if (fd != null) {
            fd.setTotalSeats(totalSeats);
            fd.setOccupiedSeats(Math.min(occupiedSeats, totalSeats));
        }
    }

    // ============ Recalculation methods ============

    private void recalculateFloorPercent(String floorName) {
        FloorData fd = floorDataMap.get(floorName.toLowerCase());
        if (fd != null) {
            int total = fd.getTotalSeats();
            int occupied = fd.getOccupiedSeats();
            double percent = (total > 0) ? ((double) occupied / total) * 100.0 : 0.0;
            fd.occupancyPercent.set(percent);
        }
    }

    private void recalculateCurrentStudents() {
        int total = 0;
        for (FloorData fd : floorDataMap.values()) {
            total += fd.getOccupiedSeats();
        }
        currentStudents.set(total);
    }

    private void recalculateOverallPercent() {
        int max = maxCapacity.get();
        int current = currentStudents.get();
        double percent = (max > 0) ? ((double) current / max) * 100.0 : 0.0;
        overallOccupancyPercent.set(percent);
    }

    /**
     * Reset all values to zero (for testing).
     */
    public void reset() {
        for (FloorData fd : floorDataMap.values()) {
            fd.setOccupiedSeats(0);
        }
    }
}
