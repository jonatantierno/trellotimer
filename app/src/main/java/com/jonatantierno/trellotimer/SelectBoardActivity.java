package com.jonatantierno.trellotimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * In this activity the user selects the board where the tasks are.
 */
public class SelectBoardActivity extends ConfigActivity<Item>  {

    public SelectBoardActivity(){
        super(R.layout.activity_selectboard);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            connections.getBoards(credentialFactory, this);
    }

    @Override
    public void onItemSelected(int position) {
        assert(position >= 0 && position < list.size());

        store.saveBoard(list.get(position));

        startActivity(new Intent(this,SelectListsActivity.class));
    }
}
