package com.jonatantierno.trellotimer;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * In this activity the user selects the three lists to use
 * Created by jonatan on 16/04/15.
 */
public class SelectListsActivity extends ConfigActivity<Item>{
    ViewGroup todoGroupView;
    EditText todoEditTextView;

    TextView todoTextView;
    TextView doingTextView;
    TextView doneTextView;

    public SelectListsActivity(){
        super(R.layout.activity_selectlist);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        todoTextView = (TextView) findViewById(R.id.list_todoTextView);
        doingTextView = (TextView) findViewById(R.id.list_doingTextView);
        doneTextView = (TextView) findViewById(R.id.list_doneTextView);

        todoTextView.setTextColor(getResources().getColor(R.color.text_current));

        connections.getLists(credentialFactory, this);
    }

    @Override
    public void onItemSelected(int position) {
        assert(position >= 0 && position < list.size());

        store.saveTodoList(list.get(position));
        todoTextView.setTextColor(getResources().getColor(R.color.text_correct));
        doingTextView.setTextColor(getResources().getColor(R.color.text_current));

    }
}
