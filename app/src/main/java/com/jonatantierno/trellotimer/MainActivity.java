package com.jonatantierno.trellotimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
        setContentView(R.layout.activity_main);

        credentialFactory = ((TTApplication) getApplication()).credentialFactory;
        store = ((TTApplication) getApplication()).store;
        connections = ((TTApplication) getApplication()).connections;

        textView = (TextView)findViewById(R.id.textview);
        button = (Button)findViewById(R.id.button);

        credentialFactory.init(this);
        store.init(this);

        if (store.userFinishedConfig()){
            goTo(TasksActivity.class);
            return;
        }
        if (store.userLoggedIn()){
            goTo(SelectBoardActivity.class);
            return;
        }

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                button.setVisibility(View.GONE);
                textView.setText(R.string.connecting);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connections.getBoards(credentialFactory, new TTCallback<List<Item>>() {
                            @Override
                            public void success(final List<Item> boards) {
                                onLogInSuccess();
                            }

                            @Override
                            public void failure(final Throwable error) {
                                onLoginFailure(error);
                            }
                        });
                    }
                }).start();

            }
        });

    }

    private void onLoginFailure(final Throwable error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(R.string.trello_error);
                button.setVisibility(View.VISIBLE);
            }
        });
    }

    void onLogInSuccess() {
        store.checkLoggedIn();
        goTo(SelectBoardActivity.class);
    }

    private void goTo(Class activity) {
        startActivity(new Intent(this, activity));
        finish();
    }
}
