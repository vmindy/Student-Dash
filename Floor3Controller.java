package com.example.studentmobileapp.pages.floor3;

import com.example.studentmobileapp.MainAppApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Floor3Controller {
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
        occupancyLabel.setText("65%");
    }

    @FXML
    private void openFloorPlan3(ActionEvent event) {
        openUrl("https://uta.stackmap.com/explore/10");
    }

    @FXML
    private void openSafeSpace(ActionEvent event) {
        openUrl("https://libraries.uta.edu/news/safe-space");
    }

    @FXML
    private void openResearchMavs(ActionEvent event) {
        openUrl("https://libraries.uta.edu/research/researchmavs");
    }

    @FXML
    private void openStudyRooms(ActionEvent event) {
        openUrl("https://uta.libcal.com/spaces?lid=10450&gid=25835&c=");
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