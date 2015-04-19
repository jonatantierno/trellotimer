package com.jonatantierno.trellotimer;

import android.view.View;

/**
 * Interface for trello item (board or list) selection
 * Created by jonatan on 16/04/15.
 */
public interface OnTTItemSelectedListener {
    void onItemSelected(int position, View selectedItem);
}
