package com.standalone.cashbook.controllers;

import android.content.Context;

import com.standalone.cashbook.models.Category;
import com.standalone.core.requests.dbase.SqLiteBase;

public class CategoryController extends SqLiteBase<Category> {
    public CategoryController(Context context) {
        super(context, "tbl_category");
    }

    @Override
    public void insert(Category category) {
        try {
            getDatabase().insert(getTableName(), null, parseContentValues(category));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Category category) {
        try {
            getDatabase().update(getTableName(), parseContentValues(category), "id=?", new String[]{String.valueOf(category.getId())});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(int id) {
        getDatabase().delete(getTableName(), "id=?", new String[]{String.valueOf(id)});
    }
}
