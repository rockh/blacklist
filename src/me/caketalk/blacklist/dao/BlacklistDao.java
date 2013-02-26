package me.caketalk.blacklist.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import me.caketalk.blacklist.model.Blacklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rock Huang
 * @version 0.1
 */
public class BlacklistDao extends BaseDao {

    private static final String TAG_ID = "_id";
    private static final String TAG_PHONE = "phone";

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

    public int remove(int id) {
        return delete(T_BLACKLIST, "_id=" + id);
    }

    public List<Map<String, String>> getAllBlacklist() {
        String cmd = "SELECT DISTINCT _id, phone FROM " + T_BLACKLIST;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(cmd, null);

        List<Map<String, String>> blacklist = new ArrayList<Map<String, String>>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(TAG_ID, cursor.getString(0));
                map.put(TAG_PHONE, cursor.getString(1));
                blacklist.add(map);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return blacklist;
    }

    public boolean isExist(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select 1 from " + T_BLACKLIST + " where phone=?", new String[]{phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }
}
