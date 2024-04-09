package com.standalone.cashbook.models;

import com.standalone.core.requests.dbase.Column;

public class Category {
    @Column(primary = true)
    int id;

    @Column
    String name;

    @Column
    int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
