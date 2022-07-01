package com.example.andmoduleads.applovin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.andmoduleads.R;

public class ListSimpleAdapter extends RecyclerView.Adapter<ListSimpleAdapter.ViewHolder> {

    int itemCount = 30;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate( R.layout.item_simple_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       TextView title =  holder.itemView.findViewById(R.id.txtTile);
       title.setText("Item Position "+position);
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public void removeItem(int pos){
        itemCount--;
    }

    public void addItem(int pos){
        itemCount++;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
