package com.example.studentmobileapp.pages.favorites;

import com.example.studentmobileapp.MainAppApplication;
import com.example.studentmobileapp.pages.main.StuMainController;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;

import java.net.URL;
import java.util.ResourceBundle;

public class MiddleController implements Initializable {

    @FXML
    private Button favoritesButton, homeButton, profileButton;

    @FXML
    private Hyperlink mapLink;

    private static final String UTA_MAPS_URL = "https://www.uta.edu/maps";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (mapLink != null) {
            mapLink.setOnAction(evt -> onMapClicked());
        }

        if (homeButton != null) homeButton.setStyle(homeButton.getStyle() + " -fx-font-size:22px;");
        if (favoritesButton != null) favoritesButton.setStyle(favoritesButton.getStyle() + " -fx-font-size:22px;");
        if (profileButton != null) profileButton.setStyle(profileButton.getStyle() + " -fx-font-size:22px;");
    }

    @FXML
    private void onMapClicked() {
        HostServices hs = MainAppApplication.getHostServicesInstance();
        if (hs != null) hs.showDocument(UTA_MAPS_URL);
    }

    @FXML
    private void goToHome(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/main/stumain-page1.fxml");
    }

    @FXML
    private void goToSettings(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/settings/settings-page.fxml");
    }
}