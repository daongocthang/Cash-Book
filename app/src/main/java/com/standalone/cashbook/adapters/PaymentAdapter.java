package com.standalone.cashbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.standalone.cashbook.R;
import com.standalone.cashbook.activities.AdditionActivity;
import com.standalone.cashbook.models.PayableModel;
import com.standalone.core.adapters.BaseAdapter;

import java.util.Locale;

public class PaymentAdapter extends BaseAdapter<PayableModel, PaymentAdapter.ViewHolder> {

    final Context context;

    public PaymentAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = instantiateItemView(R.layout.list_item_payment, parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PayableModel model = getItem(position);
        holder.titleTV.setText(model.getTitle());
        holder.amountTV.setText(String.format(Locale.US, "%,d", model.getAmount()));
        holder.dateTV.setText(model.getDate());
    }

    public PayableModel removeItem(int position) {
        PayableModel model = itemList.get(position);
        itemList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void editItem(int position) {
        Intent intent = new Intent(context, AdditionActivity.class);
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
        TextView titleTV;
        TextView amountTV;
        TextView dateTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.tittleTV);
            amountTV = itemView.findViewById(R.id.amountTV);
            dateTV = itemView.findViewById(R.id.dateTV);
        }
    }
}
