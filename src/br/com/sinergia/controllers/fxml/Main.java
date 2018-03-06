package br.com.sinergia.controllers.fxml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/br/com/sinergia/views/Login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Sistema Sinergia");
        stage.getIcons().add(new Image("/br/com/sinergia/properties/images/Icone_Sistema.png"));
        stage.setResizable(false);
        stage.show();
        stage.setY(stage.getY() * 3f / 2f); //Centraliza a tela
    }
}
