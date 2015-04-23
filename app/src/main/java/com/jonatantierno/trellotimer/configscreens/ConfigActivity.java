package com.jonatantierno.trellotimer.configscreens;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jonatantierno.trellotimer.CredentialFactory;
import com.jonatantierno.trellotimer.listview.OnTTItemSelectedListener;
import com.jonatantierno.trellotimer.R;
import com.jonatantierno.trellotimer.StatusStore;
import com.jonatantierno.trellotimer.listview.TTAdapter;
import com.jonatantierno.trellotimer.TTApplication;
import com.jonatantierno.trellotimer.trellorequests.TTCallback;
import com.jonatantierno.trellotimer.trellorequests.TTConnections;
import com.jonatantierno.trellotimer.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonatan on 16/04/15.
 */
public abstract class ConfigActivity<T extends Item> extends ActionBarActivity implements TTCallback<List<T>>, OnTTItemSelectedListener {
    public final int layoutId;

    CredentialFactory credentialFactory;
    StatusStore store;
    TTConnections connections;
    ProgressBar progressBar;
    TextView messageTextView;

    RecyclerView listView;
    LinearLayoutManager listLayoutManager;
    TTAdapter listAdapter;
    protected final List<T> list;

    ConfigActivity(int layoutId){
        this.layoutId = layoutId;
        list = new ArrayList<T>();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);

        initGlobalObjects();

        initUxElements();
    }

    private void prepareList() {
        listLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(listLayoutManager);

        listAdapter = new TTAdapter(list,this);
        listView.setAdapter(listAdapter);
    }

    private void initUxElements() {
        progressBar = (ProgressBar) findViewById(R.id.boardProgressBar);
        messageTextView = (TextView) findViewById(R.id.boardTextView);
        listView = (RecyclerView) findViewById(R.id.boardList);

        prepareList();
    }

    private void initGlobalObjects() {
        credentialFactory = ((TTApplication) getApplication()).credentialFactory;
        store = ((TTApplication) getApplication()).store;
        connections = ((TTApplication) getApplication()).connections;
    }

    @Override
    public void success(List<T> result) {
        listAdapter.addItems(result);
        listAdapter.notifyItemRangeInserted(0, result.size());
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void failure(Throwable error) {
        messageTextView.setText(R.string.connection_error);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onItemLeft(int position, View selectedItem) {
        // Nothing to do
    }

    @Override
    public void onItemRight(int position, View selectedItem) {
        // Nothing to do
    }
    @Override
    public boolean showRightButton() {
        return false;
    }

    @Override
    public boolean showLeftButton() {
        return false;
    }

}
