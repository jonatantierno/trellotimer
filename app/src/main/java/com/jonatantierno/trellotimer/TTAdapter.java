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
public class TTAdapter<T extends Item> extends RecyclerView.Adapter<TTAdapter.ViewHolder> {
    private final List<T> list;
    private final OnTTItemSelectedListener itemSelectedListener;

    public void addItems(List<T> boards) {
        list.addAll(boards);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        public TextView mTextView;
        private final OnTTItemSelectedListener itemSelectedListener;

        public ViewHolder(ViewGroup v, OnTTItemSelectedListener itemSelectedListener) {
            super(v);
            this.itemSelectedListener = itemSelectedListener;
            this.mTextView = (TextView)v.findViewById(R.id.boardName);
        }

        @Override
        public void onClick(View v) {
            this.itemSelectedListener.onItemSelected(getPosition(), v);
        }
    }

    public TTAdapter(List<T> list, OnTTItemSelectedListener itemSelectedListener){
        this.list = list;
        this.itemSelectedListener = itemSelectedListener;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public TTAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_list_view, parent, false);
        // set the view's size, margins, paddings and layout parameters


        ViewHolder vh = new ViewHolder((ViewGroup) v, itemSelectedListener);
        v.setOnClickListener(vh);

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
