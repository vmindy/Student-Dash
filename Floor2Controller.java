package com.example.studentmobileapp.pages.floor2;

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

public class Floor2Controller {
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
                () -> String.format("%.0f%%", model.getOccupancyPercent("Floor2")),
                model.occupancyPercentProperty("Floor2")
            )
        );
    }

    @FXML
    private void openFloorPlan2(ActionEvent event) {
        openUrl("https://uta.stackmap.com/explore/9");
    }

    @FXML
    private void openTutoring(ActionEvent event) {
        openUrl("https://www.uta.edu/student-success/course-assistance/tutoring/drop-in");
    }

    @FXML
    private void openPrinting(ActionEvent event) {
        openUrl("https://libraries.uta.edu/services/printing");
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