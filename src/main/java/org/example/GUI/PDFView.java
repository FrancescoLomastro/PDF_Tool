package org.example.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.CloseDocException;
import org.example.PDFModel;

import java.io.IOException;

public class PDFView extends Application {
    private Stage stage;
    private static PDFView instance = null;
    public PDFView() {}

    public static PDFView getInstance() {
        if (instance == null) {
            instance = new PDFView();
        }
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage)  {
        Image icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
        stage.getIcons().add(icon);
        stage.setTitle("PDFEditor");
        stage.setOnCloseRequest(e -> {
            try {
                PDFModel.getInstance().closeDocument();
            } catch (CloseDocException ex) {
                throw new RuntimeException(ex);
            }
        });
        instance = this;
        instance.stage = stage;
        changeScene("/fxml/fileSelector.fxml");

    }
    public void changeScene(String fxmlPath) {
        Task<Parent> loadSceneTask = new Task<Parent>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader newSceneLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                return newSceneLoader.load();
            }
        };

        loadSceneTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                Parent root = loadSceneTask.getValue();
                stage.setScene(new Scene(root));
                show();
            });
        });

        loadSceneTask.setOnFailed(event -> {
            throw new RuntimeException(loadSceneTask.getException());
        });

        new Thread(loadSceneTask).start();
    }
    public void show() {
        stage.show();
    }


    public void removeCloseAction() {
        stage.setOnCloseRequest(e -> {});
    }
}