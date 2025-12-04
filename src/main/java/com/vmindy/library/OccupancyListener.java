package com.vmindy.library;

/**
 * Listener interface for receiving occupancy updates.
 * Implementations should handle UI updates or other actions when occupancy data changes.
 */
public interface OccupancyListener {
    
    /**
     * Called when occupancy data has been updated.
     * This method is invoked on the JavaFX Application Thread via Platform.runLater.
     * 
     * @param model the updated occupancy model containing current occupancy data
     */
    void onOccupancyUpdated(OccupancyModel model);
}
