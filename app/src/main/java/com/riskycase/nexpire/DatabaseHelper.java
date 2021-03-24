package com.riskycase.nexpire;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.riskycase.nexpire.ui.MyUpcomingRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "itemsDatabase";
    private static final String TABLE_ITEMS = "items";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EXPIRY = "expiry";
    private static final String KEY_REMINDER = "reminder";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String CREATE_QUERY = "CREATE TABLE " + TABLE_ITEMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_EXPIRY + " INTEGER,"
                + KEY_REMINDER + " INTEGER"
                +")";
        db.execSQL(CREATE_QUERY);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_ITEMS);
        onCreate(db);
    }

    public void addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, item.getID());
        values.put(KEY_NAME, item.getName());
        values.put(KEY_EXPIRY, item.getExpiry());
        values.put(KEY_REMINDER, item.getReminder());

        db.insert(TABLE_ITEMS, null, values);

        db.close();

    }

    public Item getItem(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ITEMS,
                new String[] {KEY_ID, KEY_NAME, KEY_EXPIRY, KEY_REMINDER},
                KEY_ID + "=?", new String[] {String.valueOf(id)},
                null,
                null,
                null,
                null);
        if(cursor != null)
            cursor.moveToFirst();
        db.close();
        return new Item(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getLong(2),
                cursor.getLong(3)
        );
    }

    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<Item>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ITEMS,
                new String[] {KEY_ID, KEY_NAME, KEY_EXPIRY, KEY_REMINDER},
                null,
                null,
                null,
                null,
                KEY_EXPIRY + " ASC");

        if(cursor.moveToFirst()) {
            do
              itemList.add(new Item()
                      .setID(cursor.getLong(0))
                      .setName(cursor.getString(1))
                      .setExpiry(cursor.getLong(2))
                      .setReminder(cursor.getLong(3))
              );
            while (cursor.moveToNext());
        }

        db.close();

        return  itemList;
    }

    public int updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, item.getName());
        values.put(KEY_EXPIRY, item.getExpiry());
        values.put(KEY_REMINDER, item.getReminder());

        int result = db.update(TABLE_ITEMS, values, KEY_ID+"=?", new String[]{String.valueOf(item.getID())});
        db.close();
        return result;
    }

    public void deleteItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ITEMS, KEY_ID+"=?", new String[]{String.valueOf(id)});
        db.close();

    }

}
