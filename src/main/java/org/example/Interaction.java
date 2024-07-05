package org.example;

import javafx.scene.layout.Pane;

public class Interaction {
    private Action action;
    private int pageIndex;
    private Pane removedPane;

    public Interaction( int pageIndex, Pane removedPane) {
        this.action = Action.REMOVE;
        this.pageIndex = pageIndex;
        this.removedPane = removedPane;
    }
    public Interaction(int pageIndex) {
        this.action = Action.ADD;
        this.pageIndex = pageIndex;
        this.removedPane = null;
    }

    public Action getAction() {
        return action;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public Pane getRemovedPane() {
        return removedPane;
    }
}
