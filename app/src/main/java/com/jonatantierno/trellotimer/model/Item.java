package com.jonatantierno.trellotimer.model;

/**
 * This immutable class represents a Trello item (board, list, card).
 * Created by jonatan on 14/04/15.
 */
public class Item {
    public final String id;
    public final String name;
    public final String info;

    public boolean selected;

    public Item(String id, String name) {
        this(id,name,null);
    }

    Item(String id, String name, String info){
        this.id = id;
        this.name = name;
        this.info = info;

        this.selected = false;
    }
}
