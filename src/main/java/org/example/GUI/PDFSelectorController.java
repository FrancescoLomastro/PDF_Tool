package org.example.GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.example.Model.PDFModel;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the PDFSelector scene
 */
public class PDFSelectorController implements Initializable {

    @FXML
    private StackPane dragDropArea;

    @FXML
    private StackPane errorPopUp;

    @FXML
    private StackPane bufferingPopUp;

    @FXML
    private Button popUpButton;

    @FXML
    private Label errorLabel;


    /**
     * Initializes the controller
     * Sets up the drag and drop area and the pop up button
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupDragAndDrop();
        setupPopUpButton();
        PDFView.getInstance().removeCloseAction();
    }

    /**
     * Sets up the pop up button
     * When clicked, the error pop up is hidden
     */
    private void setupPopUpButton() {
        popUpButton.setOnAction(event -> {
            errorPopUp.setVisible(false);
            errorLabel.setText("");
        });
    }

    /**
     * Sets up the drag and drop area
     * When a file is dropped, the PDFModel is updated with the file path
     * If the file is a PDF, the scene is changed to the PDFScene
     * If the file is not a PDF, an error message is displayed
     */
    private void setupDragAndDrop() {
        dragDropArea.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        dragDropArea.setOnDragEntered(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
                dragDropArea.getStyleClass().clear();
                dragDropArea.getStyleClass().add("stackPane_Entered");
            }
        });
        dragDropArea.setOnDragExited(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
                dragDropArea.getStyleClass().clear();
                dragDropArea.getStyleClass().add("stackPane_Exited");
            }
        });
        dragDropArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                File file = db.getFiles().getFirst();
                String path = file.getAbsolutePath();
                if(path.toLowerCase().endsWith(".pdf"))
                {
                    try {
                        PDFModel.getInstance().setDocument(path);
                    } catch (IOException e) {
                        handleInvalidFile(e.getMessage());
                    };
                    success = true;
                }
            }
            event.setDropCompleted(success);
            dragDropArea.getStyleClass().clear();
            dragDropArea.getStyleClass().add("stackPane_Exited");
            if(success) {
                changeToPDFScene();
            }
        });

        dragDropArea.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                String path = selectedFile.getAbsolutePath();
                try {
                    PDFModel.getInstance().setDocument(path);
                } catch (IOException e) {
                    handleInvalidFile(e.getMessage());
                }
                changeToPDFScene();
            }
        });
    }

    /**
     * Displays an error message in the error pop up
     * @param message the error message to display
     */
    private void handleInvalidFile(String message) {
        errorLabel.setText(message);
        errorPopUp.setVisible(true);
    }

    /**
     * Changes the scene to the PDFScene
     */
    private void changeToPDFScene() {
        bufferingPopUp.setVisible(true);
        dragDropArea.setDisable(true);
        PDFView.getInstance().changeScene("/fxml/pdfScene.fxml");
    }


}