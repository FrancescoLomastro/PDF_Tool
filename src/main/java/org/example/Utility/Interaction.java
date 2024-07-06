package org.example.Utility;

import javafx.scene.layout.Pane;

/**
 * Class for the Interaction object
 * An interaction models the action of adding or removing a page
 */
public class Interaction {
    private Action action;
    private int pageIndex;
    private Pane removedPane;

    /**
     * Constructor for a Remove action
     * @param pageIndex The index of the page
     * @param removedPane The pane that was removed
     */
    public Interaction( int pageIndex, Pane removedPane) {
        this.action = Action.REMOVE;
        this.pageIndex = pageIndex;
        this.removedPane = removedPane;
    }

    /**
     * Constructor for an Add action
     * @param pageIndex The index of the page
     */
    public Interaction(int pageIndex) {
        this.action = Action.ADD;
        this.pageIndex = pageIndex;
        this.removedPane = null;
    }

    /**
     * @return The action of the interaction
     */
    public Action getAction() {
        return action;
    }

    /**
     * @return The index of the page
     */
    public int getPageIndex() {
        return pageIndex;
    }

    /**
     * @return The pane that was removed
     */
    public Pane getRemovedPane() {
        return removedPane;
    }
}
