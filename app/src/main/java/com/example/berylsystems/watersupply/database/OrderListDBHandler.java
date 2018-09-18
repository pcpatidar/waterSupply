package com.example.berylsystems.watersupply.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.berylsystems.watersupply.bean.OrderBean;
import java.util.ArrayList;

public class OrderListDBHandler extends SQLiteOpenHelper {
    //Datebase name
    public static String DATABASE_NAME = "database";
    int DATABASE_VERSION = 1;
    //Table Name
    public static final String TABLE_NAME = "clint_data";
    //Column Name
    private static final String AUTO_INCREMENT_ID = "id";
    public static final String ORDER_ID = "orderId";
    public static final String USER_TYPE = "userType";
    public static final String NAME = "name";
    public static final String BOOKING_DATE = "bookingDate";
    public static final String DELIVER_DATE = "deliveryDate";
    public static final String AMOUNT = "amount";
    public static final String COMMENT = "comment";
    public static final String CASH_ON_DELIVERY = "cashOnDelivery";
    public static final String ADDRESS = "address";
    public static final String STATUS = "status";
    public static final String MOBILE = "mobile";
    public static final String SHOP_NAME = "shopName";



    public OrderListDBHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
       // OrderListDBHandler db = new OrderListDBHandler(context);
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
                + AUTO_INCREMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ORDER_ID + " TEXT,"
                + USER_TYPE + " TEXT,"
                + NAME + " TEXT,"
                + BOOKING_DATE + " TEXT,"
                + DELIVER_DATE + " TEXT,"
                + AMOUNT + " TEXT,"
                + COMMENT + " TEXT,"
                + CASH_ON_DELIVERY + " TEXT,"
                + ADDRESS + " TEXT,"
                + STATUS + " TEXT,"
                + MOBILE + " TEXT,"
                + SHOP_NAME + " TEXT" + ")";
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
        values.put(ORDER_ID, model.getOrderId());
        values.put(USER_TYPE, model.getUser().getUserType());
        values.put(NAME, model.getUser().getName());
        values.put(BOOKING_DATE, model.getBookingDate());
        values.put(DELIVER_DATE, model.getDeliveryDate());
        values.put(AMOUNT, model.getAmount());
        values.put(COMMENT, model.getComment());
        if (model.isCashOnDelivery()){
            values.put(CASH_ON_DELIVERY, "true");
        }else {
            values.put(CASH_ON_DELIVERY, "false");
        }
        values.put(ADDRESS, model.getAddress());
        if (model.isStatus()){
            values.put(STATUS,"true");
        }else {
            values.put(STATUS,"false");
        }
        values.put(MOBILE, model.getUser().getMobile());
        values.put(SHOP_NAME, model.getUser().getShopName());
        long result = db.insert(TABLE_NAME, null, values);
        return result;
    }


    public long updateData(OrderBean model, String id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
       // values.put(C_NAME, model.getC_name());

        long result = db.update(TABLE_NAME, values, AUTO_INCREMENT_ID + " = '" + id + "' ", null);
        return result;
    }

    public long deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, AUTO_INCREMENT_ID + " = '" + id + "'", null);
        db.close();
        return result;
    }


    public ArrayList<OrderBean> getAllDataList() {
        // Select All Query
        ArrayList<OrderBean> dataList = new ArrayList<OrderBean>();
        String selectQuery2 =
                "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery2, null);
        if (cursor.moveToFirst()) {
            do {

               // ClientDetailsModel model = new ClientDetailsModel();
              //  model.setId(cursor.getString(cursor.getColumnIndex(AUTO_INCREMENT_ID)));

              //  dataList.add(model);
            } while (cursor.moveToNext());
        }
        return dataList;
    }
}
