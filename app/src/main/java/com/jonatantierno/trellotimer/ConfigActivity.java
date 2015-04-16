package com.jonatantierno.trellotimer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonatan on 16/04/15.
 */
public abstract class ConfigActivity<T extends Item> extends ActionBarActivity implements TTCallback<List<T>>, OnTTItemSelectedListener{
    public final int layoutId;

    CredentialFactory credentialFactory;
    StatusStore store;
    TTConnections connections;
    ProgressBar progressBar;
    TextView messageTextView;

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    TTAdapter mAdapter;
    protected final List<T> list;


    ConfigActivity(int layoutId){
        this.layoutId = layoutId;
        list = new ArrayList<T>();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);

        credentialFactory = ((TTApplication) getApplication()).credentialFactory;
        store = ((TTApplication) getApplication()).store;
        connections = ((TTApplication) getApplication()).connections;

        progressBar = (ProgressBar) findViewById(R.id.boardProgressBar);
        messageTextView = (TextView) findViewById(R.id.boardTextView);
        mRecyclerView = (RecyclerView) findViewById(R.id.boardList);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new TTAdapter(list,this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void success(List<T> result) {
        mAdapter.addItems(result);
        mRecyclerView.requestLayout();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void failure(Throwable error) {
        messageTextView.setText(R.string.connection_error);
        progressBar.setVisibility(View.GONE);
    }
}
