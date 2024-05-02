package com.standalone.cashbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.standalone.cashbook.R;
import com.standalone.cashbook.activities.EditorActivity;
import com.standalone.cashbook.models.PayableModel;
import com.standalone.core.adapters.BaseAdapter;

import java.util.Locale;

public class PayableAdapter extends BaseAdapter<PayableModel, PayableAdapter.ViewHolder> {

    final Context context;

    public PayableAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = instantiateItemView(R.layout.list_item_payable, parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PayableModel model = getItem(position);
        holder.textviewTitle.setText(model.getTitle());
        holder.textViewAmount.setText(String.format(Locale.US, "%,d", model.getAmount()));
        holder.textViewDate.setText(model.getDate());
        if (model.getPaid() > 0)
            holder.viewIndicator.setBackgroundResource(com.standalone.core.R.color.success_dark);
    }

    public PayableModel removeItem(int position) {
        PayableModel model = itemList.get(position);
        itemList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void editItem(int position) {
        Intent intent = new Intent(context, EditorActivity.class);
        PayableModel model = getItem(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("payment", model);
        intent.putExtra("bundle", bundle);
        context.startActivity(intent);
    }

    public int getTotalAmount() {
        int total = 0;
        for (PayableModel item : itemList) {
            if (item.getPaid() > 0) continue;
            ;
            total += item.getAmount();
        }
        return total;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textviewTitle;
        TextView textViewAmount;
        TextView textViewDate;
        View viewIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textviewTitle = itemView.findViewById(R.id.tittleTV);
            textViewAmount = itemView.findViewById(R.id.amountTV);
            textViewDate = itemView.findViewById(R.id.dateTV);
            viewIndicator = itemView.findViewById(R.id.indicator);
        }
    }
}
