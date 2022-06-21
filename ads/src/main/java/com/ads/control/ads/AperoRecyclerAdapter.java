package com.ads.control.ads;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ads.control.R;

public class AperoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private  RecyclerView.Adapter adapterOriginal;
    private Activity activity;

    public AperoRecyclerAdapter(RecyclerView.Adapter adapterOriginal, Activity activity) {
        this.adapterOriginal = adapterOriginal;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (true){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_admob_ad, parent,false);
            return new AperoViewHolder(view);
        }else {
          return   adapterOriginal.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private   class AperoViewHolder extends RecyclerView.ViewHolder{
        public AperoViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }
}
