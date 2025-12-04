package com.vmindy.library;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model representing library occupancy with floor-level details.
 * 
 * Formulas used:
 * - Floor occupancy = occupiedSeats / totalSeats
 * - Overall occupancy = totalCurrentStudents / maxCapacity
 */
public class OccupancyModel {

    /**
     * Nested class representing a single floor in the library.
     */
    public static class Floor {
        private String name;
        private int totalSeats;
        private int occupiedSeats;

        public Floor(String name, int totalSeats, int occupiedSeats) {
            this.name = name;
            this.totalSeats = totalSeats;
            this.occupiedSeats = occupiedSeats;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTotalSeats() {
            return totalSeats;
        }

        public void setTotalSeats(int totalSeats) {
            this.totalSeats = totalSeats;
        }

        public int getOccupiedSeats() {
            return occupiedSeats;
        }

        public void setOccupiedSeats(int occupiedSeats) {
            this.occupiedSeats = occupiedSeats;
        }

        /**
         * Calculates the occupancy fraction for this floor.
         * Formula: occupiedSeats / totalSeats
         * 
         * @return occupancy as a fraction (0.0 to 1.0), or 0.0 if totalSeats is 0
         */
        public double getOccupancyFraction() {
            if (totalSeats <= 0) {
                return 0.0;
            }
            return (double) occupiedSeats / totalSeats;
        }

        /**
         * Calculates the occupancy percentage for this floor.
         * Formula: (occupiedSeats / totalSeats) * 100
         * 
         * @return occupancy as a percentage (0.0 to 100.0)
         */
        public double getOccupancyPercent() {
            return getOccupancyFraction() * 100.0;
        }
    }

    private int maxCapacity;
    private int totalCurrentStudents;
    private List<Floor> floors;

    public OccupancyModel() {
        this.floors = new ArrayList<>();
        this.maxCapacity = 0;
        this.totalCurrentStudents = 0;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getTotalCurrentStudents() {
        return totalCurrentStudents;
    }

    public void setTotalCurrentStudents(int totalCurrentStudents) {
        this.totalCurrentStudents = totalCurrentStudents;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public void setFloors(List<Floor> floors) {
        this.floors = floors;
    }

    /**
     * Recalculates totalCurrentStudents by summing occupied seats across all floors.
     * Call this after modifying floor occupancy values.
     */
    public void recalcTotalFromFloors() {
        int total = 0;
        for (Floor floor : floors) {
            total += floor.getOccupiedSeats();
        }
        this.totalCurrentStudents = total;
    }

    /**
     * Calculates the overall occupancy fraction for the library.
     * Formula: totalCurrentStudents / maxCapacity
     * 
     * @return occupancy as a fraction (0.0 to 1.0), or 0.0 if maxCapacity is 0
     */
    public double overallOccupancyFraction() {
        if (maxCapacity <= 0) {
            return 0.0;
        }
        return (double) totalCurrentStudents / maxCapacity;
    }

    /**
     * Calculates the overall occupancy percentage for the library.
     * Formula: (totalCurrentStudents / maxCapacity) * 100
     * 
     * @return occupancy as a percentage (0.0 to 100.0)
     */
    public double overallOccupancyPercent() {
        return overallOccupancyFraction() * 100.0;
    }
}
