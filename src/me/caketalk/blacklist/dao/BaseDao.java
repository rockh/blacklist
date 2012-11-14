package me.caketalk.blacklist.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Rock Huang
 * @version 0.1
 */
public abstract class BaseDao extends SQLiteOpenHelper {

    private static final String DB_NAME = "blacklist.db";
    private static final int DB_VERSION = 1;

    protected static final String T_BLACKLIST = "Blacklist";


    public BaseDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_BLACKLIST + " (id INTEGER PRIMARY KEY AUTOINCREMENT, phone VARCHAR, comment VARCHAR, created_date DATETIME DEFAULT CURRENT_TIMESTAMP)");
        Log.i(this.getClass().getName(), String.format("%s database has been created.", DB_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_BLACKLIST);
        onCreate(db);
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
