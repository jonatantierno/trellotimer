package com.jonatantierno.trellotimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * In this activity the user selects the three lists to use
 * Created by jonatan on 16/04/15.
 */
public class SelectListsActivity extends ConfigActivity<Item>{

    public SelectListsActivity(){
        super(R.layout.activity_selectboard);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connections.getLists(credentialFactory, this);
    }

    @Override
    public void onItemSelected(int position) {
        assert(position >= 0 && position < list.size());


    }
}
