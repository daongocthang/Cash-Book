package com.standalone.cashbook.models;

import com.standalone.core.requests.dbase.Column;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PayableModel extends BaseModel {

    String title;
    long amount;
    String date;
    boolean paid;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("amount", amount);
        map.put("date", date);
        map.put("paid", paid);
        return map;
    }
}
