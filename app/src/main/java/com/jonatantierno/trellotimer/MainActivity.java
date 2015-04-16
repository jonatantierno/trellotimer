package com.jonatantierno.trellotimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends ActionBarActivity {
    public static final String BASE_TRELLO_URL = "https://api.trello.com";

    CredentialFactory credentialFactory;
    StatusStore store;
    TTConnections connections;

    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        credentialFactory = ((TTApplication) getApplication()).credentialFactory;
        store = ((TTApplication) getApplication()).store;
        connections = ((TTApplication) getApplication()).connections;

        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textview);
        button = (Button)findViewById(R.id.button);

        credentialFactory.init(this);
        store.init(this);

        if (store.userLoggedIn()){
            goToConfig();
        }

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connections.getBoards(credentialFactory, new TTCallback<List<Item>>() {
                            @Override
                            public void success(final List<Item> boards) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(boards.size() + " boards found. First One is: " + boards.get(0).name);
                                    }
                                });
                                onLogInSuccess();
                            }

                            @Override
                            public void failure(final Throwable error) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(error.toString());
                                    }
                                });
                            }
                        });
                    }
                }).start();

            }
        });

    }

    void onLogInSuccess() {
        store.checkLoggedIn();
        goToConfig();
    }

    private void goToConfig() {
        startActivity(new Intent(this, SelectBoardActivity.class));
        finish();
    }
}
