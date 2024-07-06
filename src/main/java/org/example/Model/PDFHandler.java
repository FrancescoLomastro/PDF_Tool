package org.example.Model;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import org.example.Utility.Action;
import org.example.Exceptions.CloseDocException;
import org.example.Exceptions.SaveDocException;
import org.example.Exceptions.UnknownException;
import org.example.Utility.Interaction;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Stack;

/**
 * This class is responsible to act as an intermediary between the PDFModel and the PDFSceneController.
 * PDFSceneController has the illusion that it is directly interacting with the PDFModel, but in reality it is interacting with the PDFHandler.
 * This class will perform the action required by the PDFSceneController only at save time.
 * This class will also maintain the undo and redo list.
 */
public class PDFHandler {
    private List<Interaction> undoList;
    private List<Interaction> redoList;
    private List<Integer> pagesList;
    private List<byte[]> imagesList;
    private static PDFModel model = null;
    private boolean isSaved = true;

    /**
     * Constructor to initialize the PDFHandler.
     * @throws UnknownException If an unknown error occurs.
     */
    public PDFHandler() throws UnknownException {
        model = PDFModel.getInstance();
        pagesList = model.getIntegerList();
        imagesList = model.getPagesAsImages();
        undoList = new Stack<Interaction>();
        redoList = new Stack<Interaction>();
    }

    /**
     * @return True if undo is available, false otherwise.
     */
    public boolean isUndoAvailable() {
        return !undoList.isEmpty();
    }

    /**
     * @return True if redo is available, false otherwise.
     */
    public boolean isRedoAvailable() {
        return !redoList.isEmpty();
    }

    /**
     * Saves the document.
     * @throws SaveDocException If an error occurs while saving the document.
     */
    public void save() throws SaveDocException {
        int len = undoList.size();
        for (int i=0; i<len; i++) {
            Interaction interaction = undoList.get(i);
            if (interaction.getAction() == Action.ADD) {
                model.addPage(interaction.getPageIndex());
            } else if (interaction.getAction() == Action.REMOVE) {
                model.removePage(interaction.getPageIndex());
            }
        }
        undoList.clear();
        redoList.clear();
        model.save();
        isSaved = true;
    }

    /**
     * Undoes the last action.
     * @return The action that was undone.
     */
    public Interaction undo() {
        //print the whole list of undoList

        Interaction command = null;
        if (isUndoAvailable()) {
            command = undoList.remove(undoList.size() - 1);
            redoList.add(command);
        }
        if(undoList.isEmpty())
            isSaved = true;
        else
            isSaved = false;
        return command;
    }

    /**
     * Redoes the last action.
     * @return The action that was redone.
     */
    public Interaction redo() {

        Interaction command = null;
        if (isRedoAvailable()) {
            command = redoList.remove(redoList.size() - 1);
            undoList.add(command);
            isSaved = false;
        }

        return command;
    }

    /**
     * @param pageIndex The index of the page.
     * @return The image of the page.
     */
    public Image getPageImage(int pageIndex) {
        return new Image(new ByteArrayInputStream(imagesList.get(pageIndex)));
    }

    /**
     * Adds a page to the document.
     * Redo list is cleared.
     * @param pageIndex The index at which the page should be added.
     */
    public void addPage(int pageIndex) {
        undoList.add(new Interaction(pageIndex));
        redoList.clear();
        isSaved = false;
    }

    /**
     * Removes a page from the document.
     * Redo list is cleared.
     * @param pageIndex The index of the page.
     * @param removedPane The pane that was removed.
     */
    public void removePage(int pageIndex, Pane removedPane) {
        undoList.add(new Interaction(pageIndex,removedPane));
        redoList.clear();
        isSaved = false;
    }

    /**
     * @return The number of pages in the document.
     */
    public int getNumberOfPages() {
        return pagesList.size();
    }

    /**
     * @return True if the document is saved, false otherwise.
     */
    public boolean isSaved() {
        return isSaved;
    }

    /**
     * Closes the document.
     * @throws CloseDocException If an error occurs while closing the document.
     */
    public void close() throws CloseDocException {
        model.closeDocument();
    }

    /**
     * @return The name of the file.
     */
    public String getFileName() {
        return model.getFileName();
    }
}
