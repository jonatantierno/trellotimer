package com.jonatantierno.trellotimer;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ConfigActivity extends ActionBarActivity implements TTCallback<List<Board>> {

    CredentialFactory credentialFactory;
    StatusStore store;
    TTConnections connections;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private BoardAdapter mAdapter;
    private List<Board> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        credentialFactory = ((TTApplication) getApplication()).credentialFactory;
        store = ((TTApplication) getApplication()).store;
        connections = ((TTApplication) getApplication()).connections;

        mRecyclerView = (RecyclerView) findViewById(R.id.boardList);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        list = new ArrayList<Board>();
        mAdapter = new BoardAdapter(list);
        mRecyclerView.setAdapter(mAdapter);

        connections.getBoards(credentialFactory, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_config, menu);
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

    @Override
    public void success(List<Board> boards) {
        mAdapter.addItems(boards);
        mRecyclerView.requestLayout();
    }

    @Override
    public void failure(Throwable error) {

    }
}
