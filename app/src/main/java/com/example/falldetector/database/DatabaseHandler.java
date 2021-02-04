package com.example.falldetector.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "fallDetector";
    private static final String TABLE_RIDES = "rides";
    private static final String TABLE_CONTACTS = "contacts";
    private static final String KEY_ID = "id";
    private static final String KEY_HOW_LONG_WAS_RIDE = "how_long_was_ride";
    private static final String KEY_HOW_WAS_RIDE = "how_was_ride";
    private static final String KEY_HOW_WAS_WEATHER = "how_was_weather";
    private static final String KEY_NAME = "name";
    private static final String KEY_PH_NO = "phone_number";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RIDES_TABLE = "CREATE TABLE " + TABLE_RIDES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_HOW_LONG_WAS_RIDE + " TEXT,"
                + KEY_HOW_WAS_RIDE + " TEXT,"
                + KEY_HOW_WAS_WEATHER + " TEXT"
                + ")";

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT" + ")";

        db.execSQL(CREATE_RIDES_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create the tables again
        onCreate(db);
    }

    public void addRide(Ride ride) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_HOW_LONG_WAS_RIDE, ride.getHowLongWasRide());
        values.put(KEY_HOW_WAS_RIDE, ride.getHowWasRide());
        values.put(KEY_HOW_WAS_WEATHER, ride.getHowWasWeather());

        //Insert row into database
        db.insert(TABLE_RIDES, null, values);

        db.close();
    }

    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);

        db.close();
    }

    public Ride getRide(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RIDES,
                new String[] {KEY_ID, KEY_HOW_LONG_WAS_RIDE, KEY_HOW_WAS_RIDE, KEY_HOW_WAS_WEATHER},
                KEY_ID + "=?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Ride ride = new Ride(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3)
        );

        return ride;
    }

    public Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                        KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return contact
        return contact;
    }

    public List<Ride> getAllRides() {
        List<Ride> rideList = new ArrayList<Ride>();

        //Select all query
        String selectQuery = "SELECT * FROM " + TABLE_RIDES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Ride ride = new Ride();
                ride.setId(Integer.parseInt(cursor.getString(0)));
                ride.setHowLongWasRide(cursor.getString(1));
                ride.setHowWasRide(cursor.getString(2));
                ride.setHowWasWeather(cursor.getString(3));

                rideList.add(ride);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rideList;
    }

    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();

        // Select all query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));

                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactList;
    }

    public void deleteRide(Ride ride) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                TABLE_RIDES,
                KEY_ID + " = ?",
                new String[] { String.valueOf(ride.getId()) }
                );

        db.close();
    }

    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
        db.close();
    }

    public int getRidesCount() {
        String countQuery = "SELECT * FROM " + TABLE_RIDES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
