package org.example.GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.example.PDFModel;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupDragAndDrop();
        setupPopUpButton();
        PDFView.getInstance().removeCloseAction();
    }

    private void setupPopUpButton() {
        popUpButton.setOnAction(event -> {
            errorPopUp.setVisible(false);
            errorLabel.setText("");
        });
    }

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


    private void handleInvalidFile(String message) {
        errorLabel.setText(message);
        errorPopUp.setVisible(true);
    }


    private void changeToPDFScene() {
        bufferingPopUp.setVisible(true);
        dragDropArea.setDisable(true);
        PDFView.getInstance().changeScene("/fxml/pdfScene.fxml");
    }


}