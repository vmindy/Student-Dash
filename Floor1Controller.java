package com.example.studentmobileapp.pages.floor1;

import com.example.studentmobileapp.MainAppApplication;
import com.example.studentmobileapp.model.OccupancyModel;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Floor1Controller {

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
    private Label occupancyLabel;

    @FXML
    public void initialize() {
        // Bind to OccupancyModel
        OccupancyModel model = OccupancyModel.getInstance();
        occupancyLabel.textProperty().bind(
            Bindings.createStringBinding(
                () -> String.format("%.0f%%", model.getOccupancyPercent("Floor1")),
                model.occupancyPercentProperty("Floor1")
            )
        );
    }
    @FXML
    private void openFloorPlan1(ActionEvent event) {
        openUrl("https://uta.stackmap.com/explore/7");
    }

    @FXML
    private void openFabLab(ActionEvent event) {
        openUrl("https://libraries.uta.edu/services/fablab");
    }

    @FXML
    private void openEinstein(ActionEvent event) {
        openUrl("https://www.einsteinbros.com/menu/");
    }
    private void openUrl(String url) {
        // Try Desktop.browse first (works on most desktop platforms)
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return;
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

}}