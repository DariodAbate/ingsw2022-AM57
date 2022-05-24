package it.polimi.ingsw.network.client.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

public class GUI extends Application implements  PropertyChangeListener {

    Label label = new Label("My Label");

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hello.fxml"));
        Scene scene = new Scene(loader.load(), 800, 500);
        primaryStage.setTitle("Hello");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
