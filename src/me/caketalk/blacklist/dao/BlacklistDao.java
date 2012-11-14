package me.caketalk.blacklist.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import me.caketalk.blacklist.model.Blacklist;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rock Huang
 * @version 0.1
 */
public class BlacklistDao extends BaseDao {

    public BlacklistDao(Context context) {
        super(context);
    }

    public boolean add(Blacklist blacklist) {
        ContentValues v = new ContentValues(2);
        v.put("phone", blacklist.getPhone());
        v.put("comment", blacklist.getComment());
        return insert(T_BLACKLIST, v);
    }

    public int remove(String phone) {
        return delete(T_BLACKLIST, "phone=?", phone);
    }

    public List<String> getAllBlacklist() {
        String cmd = "SELECT DISTINCT phone FROM " + T_BLACKLIST;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(cmd, null);

        List<String> blacklist = new ArrayList<String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                blacklist.add(cursor.getString(0));
            } while(cursor.moveToNext());
        }

        return blacklist;
    }

    public boolean isExist(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select 1 from " + T_BLACKLIST + " where phone=?", new String[]{phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close();

        return exists;
    }
}
