package com.example.studentmobileapp.pages.floor4;

import com.example.studentmobileapp.MainAppApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SeatOccupancyController {
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
        occupancyLabel.setText("68%");
    }

    @FXML
    private void openFloorPlan4(ActionEvent event) {
        openUrl("https://uta.stackmap.com/explore/5");
    }

    @FXML
    private void openGroupStudy4(ActionEvent event) {
        openUrl("https://uta.libcal.com/spaces?lid=10450&gid=29453&c=");
    }

    @FXML
    private void openWritingCenter(ActionEvent event) {
        openUrl("https://uta.libcal.com/spaces?lid=10450&gid=29453&c=");
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
}}