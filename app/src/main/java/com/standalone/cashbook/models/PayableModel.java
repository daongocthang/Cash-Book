package com.standalone.cashbook.models;

import com.standalone.core.requests.dbase.Column;

import java.io.Serializable;

public class PayableModel implements Serializable {
    @Column(primary = true)
    int id;
    @Column
    String title;
    @Column
    int amount;
    @Column
    String date;
    @Column
    int paid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPaid() {
        return paid;
    }

    public void setPaid(int paid) {
        this.paid = paid;
    }
}
