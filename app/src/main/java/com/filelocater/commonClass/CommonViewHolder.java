package com.filelocater.commonClass;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ADMIN on 24-04-2018.
 */

public abstract class CommonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public CommonViewHolder(View itemView) {
        super(itemView);
    }

    public CommonViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onItemSelected(getAdapterPosition());
    }

    public abstract void onItemSelected(int position);

    public View getView(){
        return itemView;
    }
}