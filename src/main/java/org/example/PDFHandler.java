package org.example;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Stack;

public class PDFHandler {

    private List<Interaction> undoList;
    private List<Interaction> redoList;
    private List<Integer> pagesList;
    private List<byte[]> imagesList;
    private static PDFModel model = null;
    private boolean isSaved = true;

    public PDFHandler() {
        model = PDFModel.getInstance();
        pagesList = model.getIntegerList();
        imagesList = model.getPagesAsImages();
        undoList = new Stack<Interaction>();
        redoList = new Stack<Interaction>();
    }

    public boolean isUndoAvailable() {
        return !undoList.isEmpty();
    }

    public boolean isRedoAvailable() {
        return !redoList.isEmpty();
    }

    public void save() {
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

    public Interaction redo() {

        Interaction command = null;
        if (isRedoAvailable()) {
            command = redoList.remove(redoList.size() - 1);
            undoList.add(command);
            isSaved = false;
        }

        return command;
    }

    public Image getPageImage(int pageIndex) {
        return new Image(new ByteArrayInputStream(imagesList.get(pageIndex)));
    }

    public void addPage(int pageIndex) {
        undoList.add(new Interaction(pageIndex));
        redoList.clear();
        isSaved = false;
    }

    public void removePage(int pageIndex, Pane removedPane) {
        undoList.add(new Interaction(pageIndex,removedPane));
        redoList.clear();
        isSaved = false;
    }

    public int getNumberOfPages() {
        return pagesList.size();
    }

    public boolean isSaved() {
        return isSaved;
    }
}
