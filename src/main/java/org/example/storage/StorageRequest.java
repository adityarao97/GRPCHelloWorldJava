package org.example.storage;

public class StorageRequest {
    private String key;
    private String value;
    public StorageRequest(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }    

    public boolean isValid() {
        return this.key != null && this.value != null;
    }
}
