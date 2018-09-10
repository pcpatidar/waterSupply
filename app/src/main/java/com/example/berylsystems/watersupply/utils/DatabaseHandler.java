package com.example.berylsystems.watersupply.utils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.berylsystems.watersupply.bean.OrderBean;
import com.example.berylsystems.watersupply.bean.UserBean;

import java.util.ArrayList;

/**
 * Created by abc on 4/11/2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    //Table Name
    public static final String TABLE_NAME = "mytable";
    //Column Name
    private static final String ID = "id";
    public static final String DATE = "date";
    private String BOOKING_DATE;


    public DatabaseHandler(Context context) {
        super(context, "my_db", null, 1);
        createTable(this.getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createTable(SQLiteDatabase db) {
        if (db == null) {
            db = this.getWritableDatabase();
        }
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DATE + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    public void deleteTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        createTable(db);
    }

    public long insertData(OrderBean model) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DATE, model.getBookingDate());
        return db.insert(TABLE_NAME, null, values);
    }

    public long updateData(OrderBean model, String id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DATE, model.getBookingDate());

        long result = db.update(TABLE_NAME, values, ID + " = '" + id + "' ", null);
        return result;
    }

    public long deleteVoucher(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, ID + " = '" + id + "'", null);
        db.close();
        return result;
    }

    public ArrayList<OrderBean> getAllDataList(String startdate, String enddate, String company_id) {
        // Select All Query
        ArrayList<OrderBean> dataList = new ArrayList<OrderBean>();
//        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        String selectQuery2 =
                "SELECT * FROM " + TABLE_NAME +
                        " WHERE " + DATE + " BETWEEN '" + startdate + "' AND '" + enddate + "' ORDER BY Date ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery2, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                OrderBean orderBean = new OrderBean();
                orderBean.setBookingDate(cursor.getString(cursor.getColumnIndex(ID)));

                dataList.add(orderBean);
            } while (cursor.moveToNext());
        }
        return dataList;
    }


    public OrderBean getLast(String enddate, String company_id) {
        String query =
                "SELECT * FROM " + TABLE_NAME +
                        " WHERE " + DATE + "='" + enddate + "' ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor2 = db.rawQuery(query, null);
        OrderBean model = null;
        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            model = new OrderBean();
            model.setBookingDate(cursor2.getString(cursor2.getColumnIndex(DATE)));

        }
        return model;
    }

    public long deletePayment(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, ID + " = '" + id + "'", null);
        db.close();
        return result;
    }

    public OrderBean getRecord(String id) {
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = " + " '" + id + "' ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        OrderBean model = new OrderBean();
        model.setBookingDate(cursor.getString(cursor.getColumnIndex(DATE)));
        return model;
    }

}
