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
        public View layout;
        private final OnTTItemSelectedListener itemSelectedListener;

        public ViewHolder(ViewGroup v, OnTTItemSelectedListener itemSelectedListener) {
            super(v);
            this.itemSelectedListener = itemSelectedListener;
            this.mTextView = (TextView)v.findViewById(R.id.boardName);
            this.layout = v;
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

    @Override
    public TTAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_list_view, parent, false);

        ViewHolder vh = new ViewHolder((ViewGroup) v, itemSelectedListener);
        v.setOnClickListener(vh);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final T currentList = list.get(position);
        holder.mTextView.setText(currentList.name);

        int backgroundColor = holder.layout.getContext().getResources().getColor(R.color.background_notSelected);
        if (currentList.selected) {
            backgroundColor = holder.layout.getContext().getResources().getColor(R.color.background_selected);
        }

        holder.layout.setBackgroundColor(backgroundColor);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
