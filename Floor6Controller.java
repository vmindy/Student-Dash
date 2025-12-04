package com.example.studentmobileapp.pages.floor6;

import com.example.studentmobileapp.MainAppApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Floor6Controller {
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
        occupancyLabel.setText("33%");
    }
    @FXML
    private void openFloorPlan6(ActionEvent event) {
        openUrl("https://uta.stackmap.com/explore/8");
    }

    @FXML
    private void openSpecialCollections(ActionEvent event) {
        openUrl("https://libraries.uta.edu/special-collections");
    }

    @FXML
    private void openParlorAtrium(ActionEvent event) {
        // TODO: replace this placeholder with the exact Parlor & Atrium URL if you have one.
        openUrl("https://libraries.uta.edu"); // placeholder
    }

    @FXML
    private void openAdminContact(ActionEvent event) {
        // You provided this Admissions contact URL — I'm using it for the Libraries Admin button.
        openUrl("https://www.uta.edu/admissions/contact-us");
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