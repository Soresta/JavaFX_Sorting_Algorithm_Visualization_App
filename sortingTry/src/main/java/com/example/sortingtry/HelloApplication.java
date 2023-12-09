package com.example.sortingtry;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        InputStream iconStream = HelloApplication.class.getResourceAsStream("sort.png");

        Image icon1 = new Image(iconStream);
        stage.getIcons().add(icon1);
        stage.setScene(scene);
        stage.setTitle("Sorting Algorithms Vizulizer");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}