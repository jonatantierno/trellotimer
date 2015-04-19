package com.jonatantierno.trellotimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * In this activity the user selects the three lists to use
 * Created by jonatan on 16/04/15.
 */
public class SelectListsActivity extends ConfigActivity<Item>{
    ViewGroup todoGroupView;

    ListType selectedList = ListType.TODO;
    boolean[] savedLists = new boolean[ListType.values().length];

    TextView[] textViews = new TextView[ListType.values().length];
    EditText[] editTexts = new EditText[ListType.values().length];
    public Button listFinishButton;

    public SelectListsActivity(){
        super(R.layout.activity_selectlist);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textViews[ListType.TODO.ordinal()] = (TextView) findViewById(R.id.list_todoTextView);
        textViews[ListType.DOING.ordinal()] = (TextView) findViewById(R.id.list_doingTextView);
        textViews[ListType.DONE.ordinal()] =(TextView) findViewById(R.id.list_doneTextView);

        editTexts[ListType.TODO.ordinal()] = (EditText) findViewById(R.id.list_todoEditText);
        editTexts[ListType.DOING.ordinal()] = (EditText) findViewById(R.id.list_doingEditText);
        editTexts[ListType.DONE.ordinal()] = (EditText) findViewById(R.id.list_doneEditText);

        listFinishButton = (Button) findViewById(R.id.listFinishButton);

        listFinishButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectListsActivity.this, TasksActivity.class));
                finish();
            }
        });

        setTextColor(ListType.TODO, R.color.text_current);

        connections.getLists(credentialFactory, this);
    }

    private void setTextColor(ListType type, int color){
        textViews[type.ordinal()].setTextColor(getResources().getColor(color));
    }

    @Override
    public void onItemSelected(int position, View selectedItem) {
        assert(position >= 0 && position < list.size());
        final Item list = this.list.get(position);

        setTextColor(selectedList,R.color.text_correct);
        setTextColor(ListType.nextListType(selectedList), R.color.text_current);
        savedLists[selectedList.ordinal()] = true;
        store.saveList(selectedList, list);
        editTexts[selectedList.ordinal()].setText(list.name);

        selectedItem.setBackgroundColor(R.color.background_selectedList);

        selectedList = ListType.nextListType(selectedList);

        if (allListsReady()){
            listFinishButton.setVisibility(View.VISIBLE);

        }
    }

    private boolean allListsReady() {
        for(int i=0;i<savedLists.length; i++){
            if (!savedLists[i]) {
                return false;
            }
        }
        return true;
    }
}
