package me.caketalk.blacklist.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Rock Huang
 * @version 0.2
 */
public abstract class BaseDao extends SQLiteOpenHelper {

    private static final String DB_NAME = "blacklist.db";
    private static final int DB_VERSION = 2;

    protected static final String T_BLACKLIST = "Blacklist";


    public BaseDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_BLACKLIST + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, phone VARCHAR, call_times INTEGER, sms_times INTEGER, comment VARCHAR, created_date DATETIME DEFAULT CURRENT_TIMESTAMP)");
        Log.i(this.getClass().getName(), String.format("%s database has been created.", DB_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(this.getClass().getName(), ">> Database upgrade ... ");
        db.execSQL("CREATE TEMPORARY TABLE TEMPLIST (id INTEGER, phone VARCHAR, comment VARCHAR, created_date DATETIME);");
        Log.d(this.getClass().getName(), ">> Create temporary table TempList ... ");
        db.execSQL("INSERT INTO TEMPLIST SELECT id, phone, comment, created_date FROM Blacklist;");
        Log.d(this.getClass().getName(), ">> Copy data from table Blacklist to TempList ...");
        db.execSQL("DROP TABLE Blacklist;");
        Log.d(this.getClass().getName(), ">> Drop table Blacklist ... ");
        db.execSQL("CREATE TABLE Blacklist (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, phone VARCHAR, call_times INTEGER, sms_times INTEGER, comment VARCHAR, created_date DATETIME DEFAULT CURRENT_TIMESTAMP);");
        Log.d(this.getClass().getName(), ">> Create Blacklist in new structure ... ");
        db.execSQL("INSERT INTO Blacklist (_id, phone, comment, created_date) SELECT id, phone, comment, created_date FROM TEMPLIST;");
        Log.d(this.getClass().getName(), ">> Copy data from TempList to new Blacklist table ... ");
        db.execSQL("DROP TABLE TEMPLIST;");
        Log.d(this.getClass().getName(), ">> Drop table TempList ...");
        Log.d(this.getClass().getName(), ">> Database has been upgraded. END.");
    }

    protected boolean insert(String tableName, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long insertId = db.insert(tableName, null, values);
        db.close();
        return insertId != -1;
    }

    protected int delete(String tableName, String whereClause, String ... whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        int affected = db.delete(tableName, whereClause, whereArgs);
        db.close();
        return affected;
    }
}
