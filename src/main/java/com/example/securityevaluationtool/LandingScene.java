package com.example.securityevaluationtool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LandingScene extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LandingScene.class.getResource("landing-scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        LandingSceneController landingSceneController = new LandingSceneController();
        stage.setTitle(landingSceneController.SCENE_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}