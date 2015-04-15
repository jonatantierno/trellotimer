package com.jonatantierno.trellotimer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for List of boards in Config Activity
 * Created by jonatan on 15/04/15.
 */
public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {
    private final List<Board> list;

    public void addItems(List<Board> boards) {
        list.addAll(boards);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(ViewGroup v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.boardName);
        }
    }

    public BoardAdapter(List<Board> list){
        this.list = list;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public BoardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_list_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder((ViewGroup) v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(list.get(position).name);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }
}
