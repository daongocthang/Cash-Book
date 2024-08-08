package com.standalone.cashbook.models;

import com.google.firebase.Timestamp;
import com.standalone.core.utils.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PayableModel extends BaseModel {

    String title;
    long amount;
    long updatedAt;
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

    public Date getUpdatedAt() {
        return DateTimeUtil.getDateTime(updatedAt);
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
        map.put("updatedAt", new Date().getTime());
        map.put("paid", paid);
        return map;
    }
}
