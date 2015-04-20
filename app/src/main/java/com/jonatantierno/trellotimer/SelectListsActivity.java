package com.jonatantierno.trellotimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * In this activity the user selects the three lists to use
 * Created by jonatan on 16/04/15.
 */
public class SelectListsActivity extends ConfigActivity<Item> {

    ListType selectedListType = ListType.TODO;
    int[] savedListsIndexes = new int[ListType.SIZE];

    TextView[] textViews = new TextView[ListType.SIZE];
    EditText[] editTexts = new EditText[ListType.SIZE];
    View[] layouts = new View[ListType.SIZE];

    public Button listFinishButton;

    public SelectListsActivity(){
        super(R.layout.activity_selectlist);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViews();

        for(int i = 0; i<savedListsIndexes.length; i++){
            savedListsIndexes[i] = -1;
        }

        setTextColor(ListType.TODO, R.color.text_current);

        connections.getLists(credentialFactory, this);
    }

    private void initializeViews() {
        layouts[ListType.TODO.ordinal()] = findViewById(R.id.list_todoView);
        layouts[ListType.DOING.ordinal()] = findViewById(R.id.list_doingView);
        layouts[ListType.DONE.ordinal()] = findViewById(R.id.list_doneView);

        layouts[ListType.TODO.ordinal()].setTag(ListType.TODO);
        layouts[ListType.DOING.ordinal()].setTag(ListType.DOING);
        layouts[ListType.DONE.ordinal()].setTag(ListType.DONE);

        for (int i = 0; i<layouts.length; i++){
            layouts[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectListType(v);
                }
            });
        }

        textViews[ListType.TODO.ordinal()] = (TextView) findViewById(R.id.list_todoTextView);
        textViews[ListType.DOING.ordinal()] = (TextView) findViewById(R.id.list_doingTextView);
        textViews[ListType.DONE.ordinal()] =(TextView) findViewById(R.id.list_doneTextView);

        editTexts[ListType.TODO.ordinal()] = (EditText) findViewById(R.id.list_todoEditText);
        editTexts[ListType.DOING.ordinal()] = (EditText) findViewById(R.id.list_doingEditText);
        editTexts[ListType.DONE.ordinal()] = (EditText) findViewById(R.id.list_doneEditText);

        editTexts[ListType.TODO.ordinal()].setTag(ListType.TODO);
        editTexts[ListType.DOING.ordinal()].setTag(ListType.DOING);
        editTexts[ListType.DONE.ordinal()].setTag(ListType.DONE);

        for (int i = 0; i<layouts.length; i++){
            editTexts[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        selectListType(v);
                    }
                }
            });
        }

        listFinishButton = (Button) findViewById(R.id.listFinishButton);

        listFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(SelectListsActivity.this, TasksActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setTextColor(ListType type, int color){
        textViews[type.ordinal()].setTextColor(getResources().getColor(color));
    }

    @Override
    public void onItemSelected(int position, View selectedItem) {
        assert(position >= 0 && position < list.size());

        if (!list.get(position).selected){
            setSelected(position);
        }
    }

    private void setSelected(int position) {
        final Item selectedList = this.list.get(position);

        unselectPreviousList();

        savedListsIndexes[this.selectedListType.ordinal()] = position;

        store.saveList(this.selectedListType, selectedList);
        editTexts[this.selectedListType.ordinal()].setText(selectedList.name);

        unselectListType(this.selectedListType);

        selectedList.selected = true;
        mAdapter.notifyDataSetChanged();

        nextListTypeToSelect();

        if (allListsReady()){
            listFinishButton.setVisibility(View.VISIBLE);
        }
    }

    private void unselectListType(ListType listType) {
        if (editTexts[listType.ordinal()].getText().toString().equals("")) {
            setTextColor(listType, R.color.text_pending);
        } else {
            setTextColor(listType, R.color.text_correct);
        }
    }

    private void nextListTypeToSelect() {
        setTextColor(ListType.nextListType(this.selectedListType), R.color.text_current);
        this.selectedListType = ListType.nextListType(this.selectedListType);
    }

    private void unselectPreviousList() {
        int previousIndex = savedListsIndexes[this.selectedListType.ordinal()];
        if (previousIndex != -1 && list.get(previousIndex).selected){
            list.get(previousIndex).selected = false;
        }
    }

    private boolean allListsReady() {
        for(int i=0;i< savedListsIndexes.length; i++){
            if (savedListsIndexes[i] == -1) {
                return false;
            }
        }
        return true;
    }

    private void selectListType(View v) {
        unselectListType(selectedListType);

        selectedListType = (ListType)v.getTag();
        setTextColor(selectedListType,R.color.text_current);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, SelectBoardActivity.class));
    }
}
