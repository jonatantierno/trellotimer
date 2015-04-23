package com.jonatantierno.trellotimer.listview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jonatantierno.trellotimer.R;
import com.jonatantierno.trellotimer.model.Item;

import java.util.List;

/**
 * Adapter for Lists (Recycler Views)
 * Created by jonatan on 15/04/15.
 */
public class TTAdapter<T extends Item> extends RecyclerView.Adapter<TTAdapter.ViewHolder> {
    public static final TTAdapter NULL = new TTAdapter(null,null);

    private final List<T> list;
    private final OnTTItemSelectedListener itemSelectedListener;

    public void addItems(List<T> boards) {
        list.addAll(boards);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nameTextView;
        public TextView infoTextView;
        public View layout;

        private final OnTTItemSelectedListener itemSelectedListener;

        public ViewHolder(ViewGroup v, OnTTItemSelectedListener itemSelectedListener) {
            super(v);
            this.itemSelectedListener = itemSelectedListener;
            this.nameTextView = (TextView)v.findViewById(R.id.itemName);
            this.infoTextView = (TextView)v.findViewById(R.id.taskInfo);

            this.layout = v;
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.moveLeftButton:
                    this.itemSelectedListener.onItemLeft(getPosition(),v);
                    break;
                case R.id.moveRightButton:
                    this.itemSelectedListener.onItemRight(getPosition(), v);
                    break;
                default:
                    this.itemSelectedListener.onItemSelected(getPosition(), v);
            }
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
                .inflate(R.layout.item_list_view, parent, false);

        ViewHolder vh = new ViewHolder((ViewGroup) v, itemSelectedListener);

        v.setOnClickListener(vh);

        prepareButton(vh, v.findViewById(R.id.moveLeftButton));
        prepareButton(vh, v.findViewById(R.id.moveRightButton));

        return vh;
    }

    private void prepareButton(ViewHolder vh, View button) {
        button.setOnClickListener(vh);
        button.setVisibility(View.GONE);

        if (button.getId() == R.id.moveRightButton
                && itemSelectedListener.showRightButton()) {
            button.setVisibility(View.VISIBLE);
        }
        if (button.getId() == R.id.moveLeftButton
                && itemSelectedListener.showLeftButton()) {
            button.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final T currentItem = list.get(position);
        holder.nameTextView.setText(currentItem.name);

        showInfo(holder, currentItem);
        showColor(holder, currentItem);
    }

    private void showColor(ViewHolder holder, T currentItem) {
        int backgroundColor = holder.layout.getContext().getResources().getColor(R.color.background_notSelected);
        if (currentItem.selected) {
            backgroundColor = holder.layout.getContext().getResources().getColor(R.color.background_selected);
        }

        holder.layout.setBackgroundColor(backgroundColor);
    }

    private void showInfo(ViewHolder holder, T currentItem) {
        if (currentItem.info != null){
            holder.infoTextView.setVisibility(View.VISIBLE);
            holder.infoTextView.setText(currentItem.info);
        } else {
            holder.infoTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
