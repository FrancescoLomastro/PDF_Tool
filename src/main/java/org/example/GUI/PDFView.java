package org.example.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.Exceptions.CloseDocException;
import org.example.Model.PDFModel;


/**
 * The main class of the application.
 * Is used to handle the GUI scenes.
 * PDFView is a singleton class.
 */
public class PDFView extends Application {
    private Stage stage;
    private static PDFView instance = null;
    public PDFView() {}
    /**
     * This method is used to get the instance of the PDFView.
     * @return The instance of the PDFView.
     */
    public static PDFView getInstance() {
        if (instance == null) {
            instance = new PDFView();
        }
        return instance;
    }

    /**
     * This method is used to start the GUI.
     * @param args The arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method is used to load the file selector scene.
     * File selector scene is the first scene that is loaded and its used to select the PDF file.
     * @param stage
     */
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

    /**
     * This method is used to change the displayed scene.
     * @param fxmlPath The path of the fxml file of the new scene.
     */
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

    /**
     * This method is used to show the stage.
     */
    public void show() {
        stage.show();
    }

    /**
     * This method is used to remove the close action of the stage.
     */
    public void removeCloseAction() {
        stage.setOnCloseRequest(e -> {});
    }
}