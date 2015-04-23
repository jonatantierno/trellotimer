package com.jonatantierno.trellotimer;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.jonatantierno.trellotimer.configscreens.SelectBoardActivity;
import com.jonatantierno.trellotimer.db.TaskStore;
import com.jonatantierno.trellotimer.trellorequests.TTConnections;

/**
 * Application with global objects.
 * Created by jonatan on 15/04/15.
 */
public class TTApplication extends Application {

    // This should not be public, but are in order to be injectable.
    // Could be solved with a dependency injection engine (Roboguice, Dagger)
    public CredentialFactory credentialFactory = new CredentialFactory();
    public StatusStore store = new StatusStore();
    public TTConnections connections = new TTConnections(store);
    public TaskStore taskStore = new TaskStore(this);

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
