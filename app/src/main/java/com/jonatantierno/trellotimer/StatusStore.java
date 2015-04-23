package com.jonatantierno.trellotimer;


import android.content.Context;
import android.content.SharedPreferences;

import com.jonatantierno.trellotimer.model.Item;
import com.jonatantierno.trellotimer.model.ListType;

/**
 * This class handles the storage of persistant status of the app.
 * Created by jonatan on 15/04/15.
 */
//@Singleton
public class StatusStore {
    public static final String PREFERENCES = "TTPreferences";
    public static final String USER_LOGGED_IN = "USER_LOGGED_IN";
    public static final String BOARD_ID = "BOARD_ID";
    public static final String BOARD_NAME = "BOARD_NAME";
    public static final String LIST_ID = "LIST_ID_";
    public static final String LIST_NAME = "LIST_NAME";

    SharedPreferences prefs;
    /**
     * Returns true if user already logged in with her Trello Account
     * @return true if logged in already, false otherwise.
     */
    public boolean userLoggedIn() {

        return prefs.getBoolean(USER_LOGGED_IN,false);
    }

    /**
     * Registers that the user has finished the connection to trello.
     */
    public void saveLoggedIn() {
        prefs.edit().putBoolean(USER_LOGGED_IN, true).commit();
    }

    /**
     * Checks if the user finished the configuration process (connection to trello, selection of
     * board and lists.)
     * @return true if finished
     */
    public boolean userFinishedConfig() {
        if (!userConfiguredBoard()){
            return false;
        }
        for(int i = 0; i< ListType.values().length; i++){
            if (!userConfiguredList(ListType.values()[i])){
                return false;
            }
        }
        return true;
    }


    /**
     * Initializes the store.
     * @param context context
     */
    public void init(Context context) {
        prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * Saves the board selected.
     * @param board selected board
     */
    public void saveBoard(Item board) {
        prefs.edit().putString(BOARD_ID, board.id).commit();
        prefs.edit().putString(BOARD_NAME, board.name).commit();
    }

    /**
     * Gets the Id of the selected board
     * @return trello Id
     */
    public String getBoardId() {
        return prefs.getString(BOARD_ID,null);
    }

    /**
     * Gets the id of the selected list
     * @param listType type of list (TODO, DOING, DONE)
     * @return trello id
     */
    public String getListId(ListType listType) {
        return prefs.getString(LIST_ID+ listType.name(),null);
    }

    /**
     * Saves selected list
     * @param type type of list (TODO, DOING, DONE)
     * @param list list to save.
     */
    public void saveList(ListType type, Item list){
        prefs.edit().putString(LIST_ID +type.name(), list.id).commit();
        prefs.edit().putString(LIST_NAME + type.name(), list.name).commit();
    }

    private boolean userConfiguredList(ListType listType) {
        ListType type = listType;
        if (prefs.getString(LIST_NAME+type.name(),null) == null) {
            return false;
        }
        if (prefs.getString(LIST_ID+type.name(),null) == null) {
            return false;
        }
        return true;
    }

    private boolean userConfiguredBoard() {
        if (prefs.getString(BOARD_ID,null) == null) {
            return false;
        }
        if (prefs.getString(BOARD_NAME,null) == null) {
            return false;
        }
        return true;
    }

    /**
     * Deletes all data stored
     */
    public void reset() {
        prefs.edit().clear().commit();
    }

}
