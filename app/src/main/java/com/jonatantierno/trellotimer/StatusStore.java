package com.jonatantierno.trellotimer;


import android.content.Context;
import android.content.SharedPreferences;

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

    public void checkLoggedIn() {
        prefs.edit().putBoolean(USER_LOGGED_IN, true).commit();
    }

    public void init(Context context) {
        prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }
    public void saveBoard(Item board) {
        prefs.edit().putString(BOARD_ID, board.id).commit();
        prefs.edit().putString(BOARD_NAME, board.name).commit();
    }

    public String getBoardId() {
        return prefs.getString(BOARD_ID,null);
    }
    public String getListId(ListType listType) {
        return prefs.getString(LIST_ID+ listType.name(),null);
    }

    public void saveList(ListType type, Item list){
        prefs.edit().putString(LIST_ID +type.name(), list.id).commit();
        prefs.edit().putString(LIST_NAME + type.name(), list.name).commit();
    }


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

    public void reset() {
        prefs.edit().clear().commit();
    }

}
