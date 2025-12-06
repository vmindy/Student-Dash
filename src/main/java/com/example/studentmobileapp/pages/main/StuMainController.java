package com.example.studentmobileapp.pages.main;

import com.example.studentmobileapp.MainAppApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
public class StuMainController {
    @FXML
    private void goToSettings(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/settings/settings-page.fxml");
    }

    @FXML
    private void goToFavorites(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/favorites/middle-page.fxml");
    }

    @FXML
    private void goToBasement(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/basement/basement-floor.fxml");
    }

    @FXML
    private void goToFloor1(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/floor1/floor-1.fxml");
    }

    @FXML
    private void goToFloor2(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/floor2/floor-2.fxml");
    }

    @FXML
    private void goToFloor3(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/floor3/floor-3.fxml");
    }

    @FXML
    private void goToFloor4(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/floor4/seat-occupancy.fxml");
    }

    @FXML
    private void goToFloor5(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/floor5/floor-5.fxml");
    }

    @FXML
    private void goToFloor6(ActionEvent event) {
        MainAppApplication.setRoot("/com/example/studentmobileapp/pages/floor6/floor-6.fxml");
    }
}