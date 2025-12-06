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
     */
    public void seedInitialValues() {
        // Overall max capacity
        int maxCapacity = 200;

        // Per-floor data: {totalSeats, occupiedSeats}
        Map<String, int[]> floorData = new HashMap<>();
        floorData.put("Basement", new int[]{30, 22});  // 73%
        floorData.put("Floor1", new int[]{35, 33});    // 94%
        floorData.put("Floor2", new int[]{32, 26});    // 81%
        floorData.put("Floor3", new int[]{28, 18});    // 64%
        floorData.put("Floor4", new int[]{25, 17});    // 68%
        floorData.put("Floor5", new int[]{25, 12});    // 48%
        floorData.put("Floor6", new int[]{25, 8});     // 32%

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
     * Randomly mutate occupied seats on each floor.
     */
    private void mutateOccupancy() {
        if (!running) {
            return;
        }

        for (String floor : OccupancyModel.FLOOR_NAMES) {
            int totalSeats = model.getTotalSeats(floor);
            int currentOccupied = model.getOccupiedSeats(floor);

            // Random change: -MAX_CHANGE to +MAX_CHANGE
            int change = random.nextInt(2 * MAX_CHANGE_PER_UPDATE + 1) - MAX_CHANGE_PER_UPDATE;
            int newOccupied = Math.max(0, Math.min(totalSeats, currentOccupied + change));

            final int finalOccupied = newOccupied;
            Platform.runLater(() -> model.setOccupiedSeats(floor, finalOccupied));
        }
    }

    public boolean isRunning() {
        return running;
    }
}
