package com.jonatantierno.trellotimer;

import android.content.Intent;
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
    TTAdapter listAdapter = TTAdapter.NULL;
    TasksActivity activity;
    TextView infoTextView;

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

        listType = ListType.ofIndex(getArguments().getInt(ARG_TAB_NUMBER));

        View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);

        listView = (RecyclerView) rootView.findViewById(R.id.taskList);
        infoTextView = (TextView) rootView.findViewById(R.id.taskInfoTextView);
        infoTextView.setText(getResources().getStringArray(R.array.task_info_string)[listType.ordinal()]);

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
        activity.startLoading();

        ((TasksActivity) getActivity()).connections.getTasks(
                listType,
                ((TasksActivity) getActivity()).credentialFactory,
                this);
    }

    @Override
    public void success(List<Item> result) {
        listAdapter.addItems(result);
        listAdapter.notifyItemRangeInserted(0, result.size());
        activity.stopLoading();
    }

    @Override
    public void failure(Throwable cause) {
        activity.stopLoading();
        infoTextView.setText(R.string.trello_error);
    }

    @Override
    public void onItemSelected(final int position, View selectedItem) {
        if (listType == ListType.DOING){
            saveCurrentTask(position);
            goToTimerActivity(position);
        }
    }

    private void saveCurrentTask(int position) {
        ((TTApplication)activity.getApplication()).taskStore.putTask(new Task(list.get(position)));
    }

    private void goToTimerActivity(int position) {
        final Intent intent = new Intent(activity, TimerActivity.class);
        intent.putExtra(TasksActivity.EXTRA_CARD_ID, list.get(position).id);
        activity.startActivity(intent);
    }

    @Override
    public void onItemLeft(final int position, final View selectedItem) {
        if (activity.isLoading()){
            return;
        }

        activity.startLoading();

        activity.connections.moveToList(
                list.get(position).id,
                ListType.prevListType(listType),
                activity.credentialFactory,
                new TTCallback<List<Item>>() {

                    @Override
                    public void success(List<Item> result) {
                        activity.onTaskMoved(position, listType, ListType.prevListType(listType));
                        activity.stopLoading();
                    }

                    @Override
                    public void failure(Throwable cause) {
                        infoTextView.setText(R.string.trello_error);
                        Log.e("TAG", cause.toString());
                        activity.stopLoading();
                    }
                });
    }

    @Override
    public void onItemRight(final int position, View selectedItem) {
        if (activity.isLoading()){
            return;
        }

        activity.startLoading();

        activity.connections.moveToList(
                list.get(position).id,
                ListType.nextListType(listType),
                activity.credentialFactory,
                new TTCallback<List<Item>>() {

                    @Override
                    public void success(List<Item> result) {
                        activity.onTaskMoved(position, listType, ListType.nextListType(listType));

                    }

                    @Override
                    public void failure(Throwable cause) {
                        infoTextView.setText(R.string.trello_error);
                        Log.e("TAG", cause.toString());
                        activity.stopLoading();
                    }
                });
    }

    @Override
    public boolean showRightButton() {
        return listType == ListType.TODO || listType == ListType.DOING;
    }

    @Override
    public boolean showLeftButton() {
        return listType == ListType.DOING || listType == ListType.DONE;
    }

    void addToList(Item movedTask) {
        list.add(movedTask);
        listAdapter.notifyItemInserted(list.size() - 1);
    }
}
