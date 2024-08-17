package com.standalone.cashbook.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.standalone.cashbook.models.BaseModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class FireStoreHelper<T extends BaseModel> {
    final String TAG = this.getClass().getSimpleName();
    FirebaseFirestore db;
    FirebaseAuth auth;
    final String path;

    public FireStoreHelper(String path) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        this.path = path;
    }

    public Task<Void> create(T t) {
        return reference().document(t.getKey()).set(t.toMap());
    }

    public Task<Void> update(String key, T t) {
        return reference().document(key).update(t.toMap());
    }

    public Task<Void> remove(String key) {
        return reference().document(key).delete();
    }

    public void fetch(Class<T> classType, OnFetchCompleteListener<T> onFetchCompleteListener) {
        if (auth.getUid() == null) {
            Log.e(TAG, "Authentication failed");
            return;
        }
        reference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<T> data = new ArrayList<>();

                for (DocumentSnapshot ds : task.getResult()) {
                    T t = make(ds, classType);
                    if (t == null) continue;

                    t.setKey(ds.getId());
                    data.add(t);
                }

                onFetchCompleteListener.onFetchComplete(data);
            }
        });
    }

    public CollectionReference reference() {
        return db.collection("users").document(Objects.requireNonNull(auth.getUid())).collection(path);
    }

    private T make(DocumentSnapshot snapshot, Class<T> classType) {
        Map<String, Object> map = snapshot.getData();
        if (map == null) return null;

        try {
            T t = classType.newInstance();
            for (Field field : t.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(t, map.get(field.getName()));
            }

            return t;

        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public interface OnFetchCompleteListener<T> {
        void onFetchComplete(ArrayList<T> data);
    }
}
