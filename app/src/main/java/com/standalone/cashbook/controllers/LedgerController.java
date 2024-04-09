package com.standalone.cashbook.controllers;

import android.content.Context;

import com.standalone.cashbook.models.Ledger;
import com.standalone.core.requests.dbase.SqLiteBase;

public class LedgerController extends SqLiteBase<Ledger> {


    public LedgerController(Context context) {
        super(context, "tbl_ledger");
    }


    @Override
    public void insert(Ledger ledger) {
        try {
            getDatabase().insert(getTableName(), null, parseContentValues(ledger));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Ledger ledger) {
        try {
            getDatabase().update(getTableName(), parseContentValues(ledger), "id=?", new String[]{String.valueOf(ledger.getId())});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(int id) {
        getDatabase().delete(getTableName(), "id=?", new String[]{String.valueOf(id)});
    }
}
