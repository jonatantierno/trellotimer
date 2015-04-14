package com.jonatantierno.trellotimer;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jonatantierno.trellotimer.services.GetAllBoardsSrv;
import com.jonatantierno.trellotimer.services.GetBoardSrv;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {
    public static final String CLIENT_ID = "2cb6e34fcc644d0cc36f7b4751ed3a90";
    public static final String CLIENT_SECRET = "10436d346b0d223043dbca4baac8dd7a88bcbfd51194999ba80fd5d348c13f84";

    public static final String SAMPLE_BOARD_ID = "552c101e1158f2cb2ca1ab90";

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.messageTextView);
        RestAdapter boardRestAdapter = new RestAdapter.Builder().setEndpoint("https://api.trello.com").build();
        GetBoardSrv boardsSrv = boardRestAdapter.create(GetBoardSrv.class);

        boardsSrv.getBoard(CLIENT_ID, SAMPLE_BOARD_ID, new Callback<Board>(){

            @Override
            public void success(Board boards, Response response) {
                textView.setText("Boards Name is "+ boards.name);
            }

            @Override
            public void failure(RetrofitError error) {
                   textView.setText(error.toString());
            }
        });


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
