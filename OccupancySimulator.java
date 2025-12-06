package com.example.studentmobileapp.model;

import javafx.application.Platform;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simulator that seeds placeholder occupancy values and periodically
 * mutates occupied seats to simulate real-time changes.
 */
public class OccupancySimulator {

    private static OccupancySimulator instance;

    private final OccupancyModel model;
    private ScheduledExecutorService executor;
    private final Random random = new Random();
    private volatile boolean running = false;

    // Simulation parameters
    private static final int UPDATE_INTERVAL_SECONDS = 5;
    private static final int MAX_CHANGE_PER_UPDATE = 3;
    
    /** 
     * Target occupancy percentage for the simulator (0.0 to 1.0).
     * The simulator will nudge floor occupancies toward this target.
     */
    private static final double TARGET_OCCUPANCY_PERCENT = 0.50;

    private OccupancySimulator() {
        this.model = OccupancyModel.getInstance();
    }

    public static synchronized OccupancySimulator getInstance() {
        if (instance == null) {
            instance = new OccupancySimulator();
        }
        return instance;
    }

    /**
     * Seed initial placeholder values.
     * Initial occupancy is set to approximately 50% on each floor.
     */
    public void seedInitialValues() {
        // Overall max capacity
        int maxCapacity = 200;

        // Per-floor data: {totalSeats, occupiedSeats} - starting around 50% occupancy
        Map<String, int[]> floorData = new HashMap<>();
        floorData.put("Basement", new int[]{30, 15});  // 50%
        floorData.put("Floor1", new int[]{35, 18});    // 51%
        floorData.put("Floor2", new int[]{32, 16});    // 50%
        floorData.put("Floor3", new int[]{28, 14});    // 50%
        floorData.put("Floor4", new int[]{25, 12});    // 48%
        floorData.put("Floor5", new int[]{25, 13});    // 52%
        floorData.put("Floor6", new int[]{25, 12});    // 48%

        Platform.runLater(() -> model.seedPlaceholderValues(maxCapacity, floorData));
    }

    /**
     * Start the simulator. Seeds initial values and begins periodic updates.
     */
    public void start() {
        if (running) {
            return;
        }
        running = true;

        // Seed initial values
        seedInitialValues();

        // Schedule periodic updates
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "OccupancySimulator");
            t.setDaemon(true);
            return t;
        });

        executor.scheduleAtFixedRate(this::mutateOccupancy,
                UPDATE_INTERVAL_SECONDS, UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Stop the simulator.
     */
    public void stop() {
        running = false;
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Nudge occupied seats on each floor toward the target occupancy (~50%).
     * Uses the batch checkInToFloor/checkOutFromFloor methods for atomic updates.
     * The algorithm applies a bias toward the target while still allowing random variation.
     */
    private void mutateOccupancy() {
        if (!running) {
            return;
        }

        for (String floor : OccupancyModel.FLOOR_NAMES) {
            int totalSeats = model.getTotalSeats(floor);
            if (totalSeats <= 0) continue;
            
            int currentOccupied = model.getOccupiedSeats(floor);
            int targetOccupied = (int) Math.round(totalSeats * TARGET_OCCUPANCY_PERCENT);
            
            // Calculate direction bias: nudge toward target
            int diff = targetOccupied - currentOccupied;
            int direction = 0;
            if (diff > 0) {
                direction = 1;  // Need to increase
            } else if (diff < 0) {
                direction = -1; // Need to decrease
            }
            
            // Random change with bias toward target: 70% chance to move toward target
            int change;
            if (direction != 0 && random.nextDouble() < 0.7) {
                // Move toward target
                change = direction * (1 + random.nextInt(MAX_CHANGE_PER_UPDATE));
            } else {
                // Random change: -MAX_CHANGE to +MAX_CHANGE
                change = random.nextInt(2 * MAX_CHANGE_PER_UPDATE + 1) - MAX_CHANGE_PER_UPDATE;
            }
            
            // Apply change using batch methods (they handle bounds and FX thread)
            if (change > 0) {
                model.checkInToFloor(floor, change);
            } else if (change < 0) {
                model.checkOutFromFloor(floor, -change);
            }
        }
    }

    public boolean isRunning() {
        return running;
    }
}
