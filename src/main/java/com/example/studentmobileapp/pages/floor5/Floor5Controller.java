package com.example.studentmobileapp.pages.floor5;

import com.example.studentmobileapp.MainAppApplication;
import com.example.studentmobileapp.model.OccupancyModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Floor5Controller {

    @FXML
    private Label occupancyLabel;

    private static final String FLOOR_NAME = "floor5";

    @FXML
    public void initialize() {
        // Bind occupancy label to the model
        OccupancyModel model = OccupancyModel.getInstance();
        OccupancyModel.FloorData floorData = model.getFloorData(FLOOR_NAME);

        if (floorData != null) {
            // Update label whenever occupancy percent changes
            floorData.occupancyPercentProperty().addListener((obs, oldVal, newVal) -> {
                occupancyLabel.setText(String.format("%.0f%%", newVal.doubleValue()));
            });
            // Set initial value
            occupancyLabel.setText(String.format("%.0f%%", floorData.getOccupancyPercent()));
        }
    }

    @FXML
    private void goToHome(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/main/stumain-page1.fxml");
    }

    @FXML
    private void goToSettings(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/settings/settings-page.fxml");
    }

    @FXML
    private void goToFavorites(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/favorites/middle-page.fxml");
    }

    @FXML
    private void openFloorPlan5(ActionEvent event) {
        openUrl("https://uta.stackmap.com/explore/6");
    }

    @FXML
    private void openDFRL(ActionEvent event) {
        openUrl("https://libraries.uta.edu/research/DFRL");
    }

    @FXML
    private void openGroupStudy5(ActionEvent event) {
        openUrl("https://uta.libcal.com/spaces?lid=10450&gid=29456&c=0");
    }

    private void openUrl(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return;
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
