package com.jonatantierno.trellotimer;

import android.view.View;

/**
 * Interface for trello item (board, list or task) selection.
 * Created by jonatan on 16/04/15.
 */
public interface OnTTItemSelectedListener {
    /**
     * Call when an item is selected.
     * @param position position selected
     * @param selectedItem view selected
     */
    void onItemSelected(int position, View selectedItem);
    /**
     * Call when an item is sent left (left Button)
     * @param position position selected
     * @param selectedItem view selected
     */
    void onItemLeft(int position, View selectedItem);
    /**
     * Call when an item is sent right (right button)
     * @param position position selected
     * @param selectedItem view selected
     */
    void onItemRight(int position, View selectedItem);

    /**
     * Tells if listener is interested in the right button.
     * @return true if right button must be shown.
     */
    boolean showRightButton();
    /**
     * Tells if listener is interested in the leftbutton.
     * @return true if left button must be shown.
     */
    boolean showLeftButton();

}
