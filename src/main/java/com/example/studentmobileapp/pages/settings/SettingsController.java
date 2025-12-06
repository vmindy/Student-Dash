package com.example.studentmobileapp.pages.settings;

import com.example.studentmobileapp.MainAppApplication;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;

public class SettingsController {
    @FXML
    private void goToFavorites(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/favorites/middle-page.fxml");
    }
    @FXML
    public void goToHome(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/main/stumain-page1.fxml");
    }
}