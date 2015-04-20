package com.jonatantierno.trellotimer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class TasksFragment extends Fragment implements TTCallback<List<Item>>,OnTTItemSelectedListener {
    ListType listType;

    RecyclerView listView;
    LinearLayoutManager listLayoutManager;
    TTAdapter listAdapter;
    TasksActivity activity;
    TextView instructionsTextView;

    final List<Item> list;
    private boolean firstTime = true;

    private static final String ARG_TAB_NUMBER = "tab_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TasksFragment newInstance(int sectionNumber) {

        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TasksFragment() {
        list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (TasksActivity) getActivity();

        View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);

        listType = ListType.ofIndex(getArguments().getInt(ARG_TAB_NUMBER));

        listView = (RecyclerView) rootView.findViewById(R.id.taskList);
        instructionsTextView = (TextView) rootView.findViewById(R.id.instructionsTextView);
        listLayoutManager = new LinearLayoutManager(this.getActivity());
        listView.setLayoutManager(listLayoutManager);

        listAdapter = new TTAdapter(list,this);
        listView.setAdapter(listAdapter);

        if (firstTime) {
            firstTime = false;
            requestTasks();
        }

        return rootView;
    }

    private void requestTasks() {
        ((TasksActivity) getActivity()).connections.getTasks(
                listType,
                ((TasksActivity) getActivity()).credentialFactory,
                this);
    }

    @Override
    public void success(List<Item> result) {
        listAdapter.addItems(result);
        listView.requestLayout();
    }

    @Override
    public void failure(Throwable cause) {

    }

    @Override
    public void onItemSelected(final int position, View selectedItem) {
        activity.connections.moveToList(
                list.get(position).id,
                ListType.nextListType(listType),
                activity.credentialFactory,
                new TTCallback<List<Item>>(){

                    @Override
                    public void success(List<Item> result) {
                        list.remove(position);
                        listAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void failure(Throwable cause) {
                        instructionsTextView.setText(R.string.trello_error);
                        Log.e("TAG",cause.toString() );
                    }
                });
    }
}
