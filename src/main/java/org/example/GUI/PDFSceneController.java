package org.example.GUI;

import javafx.collections.ObservableList;
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
import javafx.scene.paint.Paint;
import org.example.Exceptions.CloseDocException;
import org.example.Exceptions.SaveDocException;
import org.example.Exceptions.UnknownException;
import org.example.Model.PDFHandler;
import org.example.Utility.Action;
import org.example.Utility.Interaction;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for the PDF scene
 */
public class PDFSceneController implements Initializable {
    private static final int PANE_SIZE = 450;
    private static final Paint blackColor = Color.BLACK;
    private static final Paint errorColor = Color.web("#cf1f24");
    private static final Paint textColor = Color.web("#849297");
    private PDFHandler pdfHandler;

    @FXML
    private Label fileNameLabel;
    @FXML
    private Button messageButton;
    @FXML
    private VBox pageVBox;
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
    @FXML
    private StackPane messageStackPane;
    @FXML
    private ImageView messageImage;
    @FXML
    private Label messageTitle;
    @FXML
    private Label messageBody;

    /**
     * Initializes the controller
     * It creates a PDFHandler object and shows the pages of the PDF
     * @param url
     * @param resourceBundle
     */
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
        messageButton.setOnAction(event -> messageStackPane.setVisible(false));
        fileNameLabel.setText(pdfHandler.getFileName());
    }

    /**
     * Handles the action of the undo button
     * It removes the last action done
     */
    public void handleUndo() {
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

    /**
     * Handles the action of the redo button
     * It reverts the last undone action
     */
    public void handleRedo() {
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

    /**
     * Handles the action of the home button
     * It closes the current document and goes back to the file selector scene
     */
    public void handleHome() {
        try {
            pdfHandler.close();
            PDFView.getInstance().changeScene("/fxml/fileSelector.fxml");
        } catch (CloseDocException e) {
            showError(e.getMessage());
        }

    }

    /**
     * Handles the action of the save button
     * It saves the current document
     */
    public void handleSave() {
        try {
            pdfHandler.save();
            modify();
        } catch (SaveDocException e) {
            showError("Error saving document, assure that the document is not open in another program");
            //throw new RuntimeException(e);
        }
    }

    /**
     * Handles the action of the info button
     * It shows a message with information about the tool
     */
    public void handleInfo() {
        String string =
                """
                        This tool allows you to add and remove page from PDF files:
                        - Left click on a page to add a white page after it
                        - Right click on a page to remove it
                        - To navigate between pages use the bottom controls or the scrollbar
                        - To save the document click on File -> Save, or Ctrl+s. When the document is saved you will see the Label in the right top corner become Green
                        - Undo (Ctrl+z) and Redo (Ctrl+y) are available in the Edit menu\s
                        """;
        showInfo(string);
    }

    /**
     * Handles the left click on a page
     * It adds a white page after the clicked page
     * @param clickedPane the clicked page
     */
    private void handleLeftClick(Pane clickedPane) {
        int index = pageVBox.getChildren().indexOf(clickedPane);
        addBlackPage(index);

    }

    /**
     * Adds a black page after the page with the given index
     * @param index the index of the previous page
     */
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

    /**
     * Creates a white pane
     * @return the white pane
     */
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

    /**
     * Handles the right click on a page
     * It removes the clicked page
     * @param clickedPane the clicked page
     */
    private void handleRightClick(Pane clickedPane) {
        if(pageVBox.getChildren().size() > 1) {
            int index = pageVBox.getChildren().indexOf(clickedPane);
            pageVBox.getChildren().remove(clickedPane);
            pageVBox.requestLayout();
            pdfHandler.removePage(index, clickedPane);
            modify();
        }
    }

    /**
     * Shows all the pages of the PDF
     */
    private void showPages() {
        int len = pdfHandler.getNumberOfPages();

        for (int i = 0; i < len; i++) {
            showPage(i);
        }

    }

    /**
     * Shows a page of the PDF
     * @param i the index of the page
     */
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

    /**
     * Modifies the scene after an action
     * It updates the save label, the menu items, the page numbers and enables the buttons
     */
    private void modify() {
        updateSaveLabel();
        updateMenuItems();
        updatePageNumbers();
        enableButtons();
    }

    /**
     * Updates the page numbers
     * It updates the current page number and the maximum page number
     */
    private void updatePageNumbers() {
        maxPageLabel.setText("/"+pageVBox.getChildren().size());
        pageLabel.setText(Math.min(pageVBox.getChildren().size(),Integer.parseInt(pageLabel.getText()))+"");
    }

    /**
     * Updates the save label
     * It changes the text and the color of the save label
     */
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

    /**
     * Updates the menu items
     * It enables or disables the undo, redo and save menu items
     */
    private void updateMenuItems() {
        redoItem.setDisable(!pdfHandler.isRedoAvailable());
        undoItem.setDisable(!pdfHandler.isUndoAvailable());
        saveItem.setDisable(pdfHandler.isSaved());
    }

    /**
     * Creates listeners for the page navigation
     * It listens for changes in the page label, the scroll bar and the next and back buttons
     */
    private void setupListeners() {
        pageLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                String page = newValue.replaceAll("\\D", "");
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

    /**
     * Enables or disables the next and back buttons
     */
    private void enableButtons() {
        nextButton.setDisable(Integer.parseInt(pageLabel.getText()) == pageVBox.getChildren().size());
        backButton.setDisable(Integer.parseInt(pageLabel.getText()) == 1);
    }

    /**
     * Updates the page number when scrolling
     */
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

    /**
     * Sets the initial value of the page label
     */
    private void initialLabelValue() {
        if(!pageVBox.getChildren().isEmpty()) {
            pageLabel.setText("1");
        }else {
            pageLabel.setText("0");
        }
    }

    /**
     * Scrolls to a page
     * @param pageInt the page number (User index)
     */
    private void scrollToPage(int pageInt) {
        double pageBegin= pageVBox.getChildren().get(pageInt-1).getBoundsInParent().getMinY();
        double space = pageVBox.getHeight() - scrollPane.getHeight();
        scrollPane.setVvalue(pageBegin/space);
    }

    /**
     * Shows an error message
     * @param message the error message
     */
    private void showError(String message) {
        messageImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/error_icon.png"))));
        messageTitle.setText("Error");
        messageBody.setText(message);
        messageBody.setTextFill(errorColor);
        messageButton.setText("Close");
        messageButton.requestFocus();
        messageStackPane.setVisible(true);
    }

    /**
     * Shows an information message
     * @param message the information message
     */
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