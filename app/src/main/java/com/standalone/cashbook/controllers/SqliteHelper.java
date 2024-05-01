package com.standalone.cashbook.controllers;

import android.content.Context;

import com.standalone.cashbook.models.PayableModel;
import com.standalone.core.requests.dbase.SqLiteBase;

public class SqliteHelper extends SqLiteBase<PayableModel> {

    public SqliteHelper(Context context) {
        super(context, "tbl_expenses");
    }

    @Override
    public void insert(PayableModel model) {
        try {
            getDatabase().insert(getTableName(), null, parseContentValues(model));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(PayableModel model) {
        try {
            getDatabase().update(getTableName(), parseContentValues(model), "id=?", new String[]{String.valueOf(model.getId())});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(int id) {
        getDatabase().delete(getTableName(), "id=?", new String[]{String.valueOf(id)});
    }
}
