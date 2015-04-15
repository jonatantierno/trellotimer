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

    SharedPreferences prefs;
    /**
     * Returns true if user already logged in with her Trello Account
     * @return true if logged in already, false otherwise.
     */
    public boolean userLoggedIn() {

        return prefs.getBoolean(USER_LOGGED_IN,false);
    }

    public void checkLoggedIn() {
        prefs.edit().putBoolean(USER_LOGGED_IN,true).commit();
    }

    public void init(Context context) {
        prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }
}
