package com.riskycase.nexpire;

public class Item {
    long _id;
    String _name;
    long _expiry;
    long _reminder;
    public Item() {}

    public Item(long id, String name, long expiry, long reminder) {
        this._id = id;
        this._name = name;
        this._expiry = expiry;
        this._reminder = reminder;
    }
    public long getID() {
        return this._id;
    }

    public Item setID(long id) {
        this._id = id;
        return this;
    }
    public String getName() {
        return this._name;
    }

    public Item setName(String name) {
        this._name = name;
        return this;
    }

    public long getExpiry() {
        return this._expiry;
    }

    public Item setExpiry(long expiry) {
        this._expiry = expiry;
        return this;
    }

    public long getReminder() {
        return this._reminder;
    }

    public Item setReminder(long reminder) {
        this._reminder = reminder;
        return this;
    }
}
