package com.jonatantierno.trellotimer;

import android.app.Application;

import com.jonatantierno.trellotimer.db.TaskStore;

/**
 * Created by jonatan on 15/04/15.
 */
public class TTApplication extends Application {

    CredentialFactory credentialFactory = new CredentialFactory();
    StatusStore store = new StatusStore();
    TTConnections connections = new TTConnections(store);
    TaskStore taskStore = new TaskStore(this);

    @Override
    public void onCreate() {
        super.onCreate();


    }
}
