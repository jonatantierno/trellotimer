package com.jonatantierno.trellotimer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.api.client.auth.oauth2.Credential;
import com.jonatantierno.trellotimer.services.GetAllBoardsSrv;

import java.io.IOException;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {
    public static final String BASE_TRELLO_URL = "https://api.trello.com";

    CredentialFactory credentialFactory = new CredentialFactory();

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textview);
        Button b = (Button)findViewById(R.id.button);

        credentialFactory.build(this);

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getBoards(credentialFactory.getCredential(),new Callback<List<Board>>() {
                                @Override
                                public void success(final List<Board> boards, Response response) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView.setText(boards.size() + " boards found. First One is: " + boards.get(0).name);
                                        }
                                    });
                                }

                                @Override
                                public void failure(final RetrofitError error) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView.setText(error.toString());
                                        }
                                    });
                                }
                            });
                        } catch (IOException e) {
                            textView.setText(e.toString());
                        }
                    }
                }).start();

            }
        });

    }


    private void getBoards(Credential credential, Callback<List<Board>> callback){
        RestAdapter boardRestAdapter = new RestAdapter.Builder().setEndpoint(BASE_TRELLO_URL).build();
        GetAllBoardsSrv getAllBoardsSrv = boardRestAdapter.create(GetAllBoardsSrv.class);

        getAllBoardsSrv.listBoards(CredentialFactory.CLIENT_ID, credential.getAccessToken(), callback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
