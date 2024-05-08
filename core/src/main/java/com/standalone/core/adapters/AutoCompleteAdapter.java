package com.standalone.core.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.standalone.core.R;

public class AutoCompleteAdapter extends BaseAdapter<String, AutoCompleteAdapter.ViewHolder> {
    private final OnItemClickListener listener;

    public AutoCompleteAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(instantiateItemView(R.layout.simple_list_item, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener, this);
    }

    public void addItem(String str) {
        itemList.add(str);
        notifyItemChanged(itemList.size()-1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item);
        }

        public void bind(String text, OnItemClickListener listener, AutoCompleteAdapter parent) {
            textView.setText(text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(text);
                    // parent.clear();
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(String text);
    }
}
