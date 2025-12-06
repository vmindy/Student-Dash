package com.example.studentmobileapp.model;

import javafx.application.Platform;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Utility that seeds placeholder occupancy data and periodically simulates
 * check-in/check-out activity by mutating floor occupied seats.
 * 
 * Runs on a background scheduled executor and updates JavaFX properties
 * on the JavaFX Application Thread using Platform.runLater().
 */
public class OccupancySimulator {

    private static OccupancySimulator instance;

    private final OccupancyModel model;
    private ScheduledExecutorService scheduler;
    private final Random random = new Random();
    private volatile boolean running = false;

    // Configuration
    private static final int DEFAULT_MAX_CAPACITY = 200;
    private static final int SIMULATION_INTERVAL_SECONDS = 3;

    // Initial seed values per floor (totalSeats, initialOccupied)
    private static final int[][] FLOOR_SEEDS = {
        {25, 19},  // basement: 75% -> 76%
        {30, 28},  // floor1: 93%
        {35, 28},  // floor2: 81%
        {30, 20},  // floor3: 65%
        {25, 17},  // floor4: 68%
        {30, 14},  // floor5: 48%
        {25, 8}    // floor6: 33%
    };

    private OccupancySimulator() {
        this.model = OccupancyModel.getInstance();
    }

    /**
     * Returns the singleton instance.
     */
    public static synchronized OccupancySimulator getInstance() {
        if (instance == null) {
            instance = new OccupancySimulator();
        }
        return instance;
    }

    /**
     * Seeds the model with placeholder initial values.
     */
    public void seedInitialData() {
        Platform.runLater(() -> {
            model.setMaxCapacity(DEFAULT_MAX_CAPACITY);
            String[] floorNames = model.getFloorNames();
            for (int i = 0; i < floorNames.length && i < FLOOR_SEEDS.length; i++) {
                model.seedFloor(floorNames[i], FLOOR_SEEDS[i][0], FLOOR_SEEDS[i][1]);
            }
        });
    }

    /**
     * Starts the simulation timer that periodically updates occupancy.
     */
    public void start() {
        if (running) {
            return;
        }
        running = true;

        // Seed initial data
        seedInitialData();

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "OccupancySimulator");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(this::simulateActivity,
            SIMULATION_INTERVAL_SECONDS,
            SIMULATION_INTERVAL_SECONDS,
            TimeUnit.SECONDS);
    }

    /**
     * Stops the simulation timer.
     */
    public void stop() {
        running = false;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Returns whether the simulator is currently running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Simulates random check-in/check-out activity on floors.
     * This runs on the scheduler thread and updates UI via Platform.runLater.
     */
    private void simulateActivity() {
        if (!running) {
            return;
        }

        Platform.runLater(() -> {
            String[] floorNames = model.getFloorNames();

            // Pick a random floor
            String floor = floorNames[random.nextInt(floorNames.length)];
            OccupancyModel.FloorData fd = model.getFloorData(floor);

            if (fd == null) {
                return;
            }

            // Decide whether to check in or check out (weighted by current occupancy)
            double occupancyRatio = (double) fd.getOccupiedSeats() / fd.getTotalSeats();

            // Higher occupancy -> more likely to check out
            // Lower occupancy -> more likely to check in
            boolean checkIn;
            if (occupancyRatio > 0.9) {
                checkIn = random.nextDouble() < 0.2; // 20% chance to check in
            } else if (occupancyRatio < 0.3) {
                checkIn = random.nextDouble() < 0.8; // 80% chance to check in
            } else {
                checkIn = random.nextBoolean(); // 50/50
            }

            if (checkIn) {
                model.checkIn(floor);
            } else {
                model.checkOut(floor);
            }
        });
    }

    /**
     * Manually trigger a check-in on a specific floor.
     */
    public void manualCheckIn(String floorName) {
        Platform.runLater(() -> model.checkIn(floorName));
    }

    /**
     * Manually trigger a check-out on a specific floor.
     */
    public void manualCheckOut(String floorName) {
        Platform.runLater(() -> model.checkOut(floorName));
    }

    /**
     * Get the configured max capacity for reference.
     */
    public int getDefaultMaxCapacity() {
        return DEFAULT_MAX_CAPACITY;
    }

    /**
     * Get the seed values for reference (for documentation).
     */
    public int[][] getFloorSeeds() {
        return FLOOR_SEEDS.clone();
    }
}
