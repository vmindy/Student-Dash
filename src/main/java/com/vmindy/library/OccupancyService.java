package com.vmindy.library;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Singleton service that manages library occupancy data and simulation.
 * 
 * This is a mock/simulation implementation that does not integrate with real sensors.
 * The simulation randomly adjusts floor occupancy values and occasionally modifies maxCapacity.
 * 
 * All listener notifications are dispatched on the JavaFX Application Thread using Platform.runLater.
 */
public class OccupancyService {

    private static OccupancyService instance;

    private final OccupancyModel model;
    private final List<OccupancyListener> listeners;
    private ScheduledExecutorService scheduler;
    private final Random random;
    private volatile boolean simulationRunning;

    /**
     * Private constructor for singleton pattern.
     * Initializes the model with placeholder values:
     * - 3 floors with totals 150/200/150 seats
     * - Initial occupied seats: 20/30/15
     * - Max capacity: 500
     */
    private OccupancyService() {
        this.model = new OccupancyModel();
        this.listeners = new ArrayList<>();
        this.random = new Random();
        this.simulationRunning = false;

        // Initialize with placeholder values
        initializePlaceholderData();
    }

    /**
     * Gets the singleton instance of OccupancyService.
     * 
     * @return the singleton instance
     */
    public static synchronized OccupancyService getInstance() {
        if (instance == null) {
            instance = new OccupancyService();
        }
        return instance;
    }

    /**
     * Initializes the model with placeholder data for the mock simulation.
     */
    private void initializePlaceholderData() {
        // Set max capacity
        model.setMaxCapacity(500);

        // Create 3 floors with specified totals and initial occupancy
        List<OccupancyModel.Floor> floors = new ArrayList<>();
        floors.add(new OccupancyModel.Floor("Floor 1", 150, 20));
        floors.add(new OccupancyModel.Floor("Floor 2", 200, 30));
        floors.add(new OccupancyModel.Floor("Floor 3", 150, 15));
        model.setFloors(floors);

        // Calculate total from floors
        model.recalcTotalFromFloors();
    }

    /**
     * Gets the current occupancy model.
     * 
     * @return the occupancy model (not a copy - be careful with modifications)
     */
    public OccupancyModel getModel() {
        return model;
    }

    /**
     * Adds a listener to receive occupancy updates.
     * 
     * @param listener the listener to add
     */
    public synchronized void addListener(OccupancyListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a listener from receiving occupancy updates.
     * 
     * @param listener the listener to remove
     */
    public synchronized void removeListener(OccupancyListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all registered listeners of an occupancy update.
     * Notifications are dispatched on the JavaFX Application Thread.
     */
    private void notifyListeners() {
        List<OccupancyListener> listenersCopy;
        synchronized (this) {
            listenersCopy = new ArrayList<>(listeners);
        }
        
        // Ensure UI updates happen on JavaFX Application Thread
        Platform.runLater(() -> {
            for (OccupancyListener listener : listenersCopy) {
                try {
                    listener.onOccupancyUpdated(model);
                } catch (Exception e) {
                    System.err.println("Error notifying listener: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Starts the occupancy simulation.
     * The simulation runs every 2 seconds and randomly adjusts floor occupancy.
     * Occasionally (10% chance), it also adjusts the max capacity slightly.
     */
    public synchronized void startSimulation() {
        if (simulationRunning) {
            return; // Already running
        }

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "OccupancySimulation");
            t.setDaemon(true);
            return t;
        });

        simulationRunning = true;

        scheduler.scheduleAtFixedRate(() -> {
            try {
                simulateOccupancyChange();
            } catch (Exception e) {
                System.err.println("Simulation error: " + e.getMessage());
            }
        }, 0, 2, TimeUnit.SECONDS);

        System.out.println("Occupancy simulation started.");
    }

    /**
     * Stops the occupancy simulation.
     */
    public synchronized void stopSimulation() {
        simulationRunning = false;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Occupancy simulation stopped.");
    }

    /**
     * Simulates a change in occupancy.
     * Randomly adjusts occupied seats on each floor within valid bounds.
     * Has a 10% chance of adjusting max capacity slightly.
     */
    private void simulateOccupancyChange() {
        List<OccupancyModel.Floor> floors = model.getFloors();

        for (OccupancyModel.Floor floor : floors) {
            // Random change: -3 to +3 students
            int change = random.nextInt(7) - 3;
            int newOccupied = floor.getOccupiedSeats() + change;

            // Ensure occupied seats stay within valid bounds [0, totalSeats]
            newOccupied = Math.max(0, Math.min(newOccupied, floor.getTotalSeats()));
            floor.setOccupiedSeats(newOccupied);
        }

        // Occasionally adjust max capacity (10% chance)
        if (random.nextInt(10) == 0) {
            int capacityChange = random.nextInt(21) - 10; // -10 to +10
            int newCapacity = model.getMaxCapacity() + capacityChange;
            // Ensure capacity doesn't go below total floor seats
            int totalFloorSeats = floors.stream().mapToInt(OccupancyModel.Floor::getTotalSeats).sum();
            newCapacity = Math.max(totalFloorSeats, newCapacity);
            model.setMaxCapacity(newCapacity);
        }

        // Recalculate total and notify listeners
        model.recalcTotalFromFloors();
        notifyListeners();
    }

    /**
     * Manually checks in a student to a specific floor.
     * 
     * @param floorIndex the index of the floor (0-based)
     * @return true if check-in was successful, false if floor is full or index is invalid
     */
    public boolean checkInToFloor(int floorIndex) {
        List<OccupancyModel.Floor> floors = model.getFloors();
        
        if (floorIndex < 0 || floorIndex >= floors.size()) {
            return false;
        }

        OccupancyModel.Floor floor = floors.get(floorIndex);
        if (floor.getOccupiedSeats() >= floor.getTotalSeats()) {
            return false; // Floor is full
        }

        floor.setOccupiedSeats(floor.getOccupiedSeats() + 1);
        model.recalcTotalFromFloors();
        notifyListeners();
        return true;
    }

    /**
     * Manually checks out a student from a specific floor.
     * 
     * @param floorIndex the index of the floor (0-based)
     * @return true if check-out was successful, false if floor is empty or index is invalid
     */
    public boolean checkOutFromFloor(int floorIndex) {
        List<OccupancyModel.Floor> floors = model.getFloors();
        
        if (floorIndex < 0 || floorIndex >= floors.size()) {
            return false;
        }

        OccupancyModel.Floor floor = floors.get(floorIndex);
        if (floor.getOccupiedSeats() <= 0) {
            return false; // Floor is empty
        }

        floor.setOccupiedSeats(floor.getOccupiedSeats() - 1);
        model.recalcTotalFromFloors();
        notifyListeners();
        return true;
    }

    /**
     * Shuts down the service completely.
     * Stops the simulation and clears all listeners.
     */
    public synchronized void shutdown() {
        stopSimulation();
        listeners.clear();
        System.out.println("OccupancyService shut down.");
    }
}
