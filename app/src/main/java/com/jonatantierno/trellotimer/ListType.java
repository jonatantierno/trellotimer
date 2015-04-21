package com.jonatantierno.trellotimer;

/**
 * Created by jonatan on 19/04/15.
 */
public enum ListType {
    TODO, DOING, DONE;

    private int[] stringId = new int[]{R.string.todo, R.string.doing, R.string.done};

    public static final int SIZE = values().length;

    public static ListType nextListType(ListType type) {
        int nextIndex = (type.ordinal() +1) % SIZE;

        return ListType.ofIndex(nextIndex);
    }

    public static ListType prevListType(ListType type) {
        int nextIndex = type.ordinal() -1;
        if(nextIndex < 0){
            nextIndex = SIZE -1;
        }
        return ListType.ofIndex(nextIndex);
    }

    public static ListType ofIndex(int index){
        return ListType.values()[index];
    }

    public int getStringId(){
        return stringId[ordinal()];
    }
}
