package org.example.GUI;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.*;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PDFSceneController implements Initializable {
    private static final int PANE_SIZE = 450;
    private static final Paint blackColor = Color.BLACK;
    private static final Paint whiteColor = Color.WHITE;
    private static final Paint errorColor = Color.web("#cf1f24");
    private static final Paint textColor = Color.web("#849297");
    @FXML
    private Button messageButton;


    @FXML
    private VBox pageVBox;
    private PDFHandler pdfHandler;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            pdfHandler = new PDFHandler();
        } catch (UnknownException e) {
            showError(e.getMessage());
            //throw new RuntimeException(e);
        }
        showPages();
        modify();
        setupListeners();
        messageButton.setOnAction(event -> {
            messageStackPane.setVisible(false);
        });
    }



    public void handleUndo(ActionEvent actionEvent) {
        Interaction command = pdfHandler.undo();
        if(command != null) {
            if (command.getAction() == Action.ADD) {
                pageVBox.getChildren().remove(command.getPageIndex());
            } else if (command.getAction() == Action.REMOVE) {
                pageVBox.getChildren().add(command.getPageIndex(),command.getRemovedPane());
            }
        }
        modify();
    }

    public void handleRedo(ActionEvent actionEvent) {
        Interaction command = pdfHandler.redo();
        if(command != null) {
            if (command.getAction() == Action.ADD) {
                Pane newPane = command.getRemovedPane();
                if (command.getRemovedPane() == null)
                    newPane = getWhitePane();
                pageVBox.getChildren().add(command.getPageIndex(),newPane);
            } else if (command.getAction() == Action.REMOVE) {
                pageVBox.getChildren().remove(command.getPageIndex());
            }
        }
        modify();
    }

    public void handleHome(ActionEvent actionEvent) {
        try {
            pdfHandler.close();
            PDFView.getInstance().changeScene("/fxml/fileSelector.fxml");
        } catch (CloseDocException e) {
            showError(e.getMessage());
        }

    }

    public void handleSave(ActionEvent actionEvent) {
        try {
            pdfHandler.save();
            modify();
        } catch (SaveDocException e) {
            showError("Error saving document, assure that the document is not open in another program");
            //throw new RuntimeException(e);
        }
    }
    public void handleInfo(ActionEvent actionEvent) {
        String string =
                "This tool allows you to add and remove page from PDF files:\n" +
                "- Left click on a page to add a white page after it\n" +
                "- Right click on a page to remove it\n" +
                "- To navigate between pages use the bottom controls or the scrollbar\n" +
                "- To save the document click on File -> Save, or Ctrl+s. When the document is saved you will see the Label in the right top corner become Green\n" +
                "- Undo (Ctrl+z) and Redo (Ctrl+y) are available in the Edit menu \n";
        showInfo(string);
    }



    private void handleLeftClick(Pane clickedPane) {
        int index = pageVBox.getChildren().indexOf(clickedPane);
        addBlackPage(index);

    }

    private void addBlackPage(int index) {
        Pane newPane = getWhitePane();
        if (index < pageVBox.getChildren().size() - 1) {
            pageVBox.getChildren().add(index + 1, newPane);
            pdfHandler.addPage(index + 1);
        } else {
            pageVBox.getChildren().add(newPane);
            pdfHandler.addPage(pageVBox.getChildren().size() - 1);
        }
        modify();
    }

    private Pane getWhitePane() {
        Pane newPane = new Pane();
        newPane.setMinSize(PANE_SIZE,PANE_SIZE);
        newPane.setMaxSize(PANE_SIZE,PANE_SIZE);
        newPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        Rectangle overlay = new Rectangle(newPane.getMinWidth(), newPane.getMinHeight(), blackColor);
        overlay.setOpacity(0.0);
        newPane.getChildren().add(overlay);

        newPane.setOnMouseEntered(event -> {
            overlay.setOpacity(0.3);
            newPane.setCursor(Cursor.HAND);
        });
        newPane.setOnMouseExited(event -> {
            overlay.setOpacity(0.0);
            newPane.setCursor(Cursor.DEFAULT);
        });
        newPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                handleLeftClick((Pane) event.getSource());
            } else if (event.getButton() == MouseButton.SECONDARY) {
                handleRightClick((Pane) event.getSource());
            }
        });
        return newPane;
    }

    private void handleRightClick(Pane clickedPane) {
        if(pageVBox.getChildren().size() > 1) {
            int index = pageVBox.getChildren().indexOf(clickedPane);
            pageVBox.getChildren().remove(clickedPane);
            pageVBox.requestLayout();
            pdfHandler.removePage(index, clickedPane);
            modify();
        }
    }

    private void showPages() {
        int len = pdfHandler.getNumberOfPages();

        for (int i = 0; i < len; i++) {
            showPage(i);
        }

    }

    private void showPage(int i) {
        Pane pane = new Pane();
        ImageView imageView = new ImageView(pdfHandler.getPageImage(i));
        double ratio = imageView.getImage().getHeight()/imageView.getImage().getWidth();

        imageView.setPreserveRatio(true);
        pane.setMinSize(PANE_SIZE,PANE_SIZE*ratio);
        pane.setMaxSize(PANE_SIZE,PANE_SIZE*ratio);
        imageView.fitWidthProperty().bind(pane.widthProperty());
        pane.getChildren().add(imageView);

        Rectangle overlay = new Rectangle(pane.getMinWidth(), pane.getMinHeight(), blackColor);
        overlay.setOpacity(0.0);
        pane.getChildren().add(overlay);

        pane.setOnMouseEntered(event -> {
            overlay.setOpacity(0.3);
            pane.setCursor(Cursor.HAND);
        });
        pane.setOnMouseExited(event -> {
            overlay.setOpacity(0.0);
            pane.setCursor(Cursor.DEFAULT);
        });
        pane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                handleLeftClick((Pane) event.getSource());
            } else if (event.getButton() == MouseButton.SECONDARY) {
                handleRightClick((Pane) event.getSource());
            }
        });


        pageVBox.getChildren().add(pane);
    }



    @FXML
    private MenuItem undoItem;

    @FXML
    private MenuItem redoItem;

    @FXML
    private MenuItem saveItem;

    @FXML
    private Label saveLabel;
    @FXML
    private Label maxPageLabel;
    @FXML
    private TextField pageLabel;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button nextButton;
    @FXML
    private Button backButton;

    private void modify() {
        updateSaveLabel();
        updateMenuItems();
        updatePageNumbers();
        enableButtons();
    }

    private void updatePageNumbers() {
        maxPageLabel.setText("/"+pageVBox.getChildren().size());
        pageLabel.setText(Math.min(pageVBox.getChildren().size(),Integer.parseInt(pageLabel.getText()))+"");
    }

    private void updateSaveLabel() {
        if(pdfHandler.isSaved()){
            saveLabel.setText("Saved");
            saveLabel.getStyleClass().clear();
            saveLabel.getStyleClass().add("saved");
        }else {
            saveLabel.setText("Not Saved");
            saveLabel.getStyleClass().clear();
            saveLabel.getStyleClass().add("unsaved");
        }
    }

    private void updateMenuItems() {
        redoItem.setDisable(!pdfHandler.isRedoAvailable());
        undoItem.setDisable(!pdfHandler.isUndoAvailable());
        saveItem.setDisable(pdfHandler.isSaved());
    }

    private void setupListeners() {
        pageLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                String page = newValue.replaceAll("[^\\d]", "");
                pageLabel.setText(page);
            }
        });
        pageLabel.setOnAction(event -> {
            String page = pageLabel.getText();
            int pageInt = Integer.parseInt(page);
            if(page.isEmpty() || pageInt < 1 || pageInt > pageVBox.getChildren().size()) {
                page = "1";
                pageInt = Integer.parseInt(page);
                pageLabel.setText(page);
            }

            scrollToPage(pageInt);

        });
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            updateScroll();
            enableButtons();
        });
        nextButton.setOnAction(event -> {
            int pageInt = Integer.parseInt(pageLabel.getText());
            if(pageInt < pageVBox.getChildren().size()) {
                scrollToPage(pageInt+1);
            }
        });
        backButton.setOnAction(event -> {
            int pageInt = Integer.parseInt(pageLabel.getText());
            if(pageInt > 1) {
                scrollToPage(pageInt-1);
            }
        });
        initialLabelValue();
    }

    private void enableButtons() {
        nextButton.setDisable(Integer.parseInt(pageLabel.getText()) == pageVBox.getChildren().size());
        backButton.setDisable(Integer.parseInt(pageLabel.getText()) == 1);
    }


    private void updateScroll() {
        double vValue = scrollPane.getVvalue();
        double space = pageVBox.getHeight() - scrollPane.getHeight();
        double vValueReal = vValue*space +1 ; // +1 to avoid rounding errors
        ObservableList<Node> children =  pageVBox.getChildren();

        for (int i = 0; i < children.size(); i++) {
            Node midNode = children.get(i);
            double pageBegin = midNode.getBoundsInParent().getMinY();
            double pageEnd = midNode.getBoundsInParent().getMaxY();

            if (vValueReal >= pageBegin && vValueReal < pageEnd) {
                pageLabel.setText(String.valueOf(i + 1));
                break;
            }
        }

    }

    private void initialLabelValue() {
        if(!pageVBox.getChildren().isEmpty()) {
            pageLabel.setText("1");
        }else {
            pageLabel.setText("0");
        }
    }

    private void scrollToPage(int pageInt) {
        double pageBegin= pageVBox.getChildren().get(pageInt-1).getBoundsInParent().getMinY();
        double space = pageVBox.getHeight() - scrollPane.getHeight();
        scrollPane.setVvalue(pageBegin/space);
    }

    @FXML
    private StackPane messageStackPane;
    @FXML
    private ImageView messageImage;
    @FXML
    private Label messageTitle;
    @FXML
    private Label messageBody;


    private void showError(String message) {
        messageImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/error_icon.png"))));
        messageTitle.setText("Error");
        messageBody.setText(message);
        messageBody.setTextFill(errorColor);
        messageButton.setText("Close");
        messageButton.requestFocus();
        messageStackPane.setVisible(true);
    }


    private void showInfo(String message) {
        messageImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/info_icon.png"))));
        messageTitle.setText("How it works");
        messageBody.setText(message);
        messageBody.setTextFill(textColor);
        messageButton.setText("Close");
        messageButton.requestFocus();
        messageStackPane.setVisible(true);
    }



}