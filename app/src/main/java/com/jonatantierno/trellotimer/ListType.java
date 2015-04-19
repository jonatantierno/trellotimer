package com.jonatantierno.trellotimer;

/**
 * Created by jonatan on 19/04/15.
 */
public enum ListType {
    TODO, DOING, DONE;

    public static ListType nextListType(ListType type) {
        switch(type){
            case TODO:
                return DOING;
            case DOING:
                return DONE;
            default:
            case DONE:
                return TODO;
        }
    }
}
