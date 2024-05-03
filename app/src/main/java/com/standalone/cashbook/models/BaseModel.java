package com.standalone.cashbook.models;

import java.io.Serializable;
import java.util.Map;

public abstract class BaseModel implements Serializable {
    String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public abstract Map<String, Object> toMap();
}
