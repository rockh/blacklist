package me.caketalk.blacklist.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    private static final String F_ID = "_id";
    private static final String F_PHONE = "phone";
    private static final String F_BLOCK_OPT_ID = "block_opt_id";
    private static final String F_COMMENT = "comment";

    public BlacklistDao(Context context) {
        super(context);
    }

    public boolean add(Blacklist blacklist) {
        ContentValues v = new ContentValues(3);
        v.put(F_PHONE, blacklist.getPhone());
        v.put(F_BLOCK_OPT_ID, blacklist.getBlockOptId());
        v.put(F_COMMENT, blacklist.getComment());
        return insert(T_BLACKLIST, v);
    }

    public int remove(String phone) {
        return delete(T_BLACKLIST, "phone=?", phone);
    }

    public int remove(int id) {
        return delete(T_BLACKLIST, "_id=" + id);
    }

    public int update(ContentValues v, String phone) {
        return update(T_BLACKLIST, v, "phone=?", phone);
    }

    public List<Map<String, Object>> getAllBlacklist() {
        String cmd = String.format("SELECT DISTINCT %s, %s, %s FROM %s", F_ID, F_PHONE, F_BLOCK_OPT_ID, T_BLACKLIST);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(cmd, null);

        List<Map<String, Object>> blacklist = new ArrayList<Map<String, Object>>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(F_ID, cursor.getString(0));
                map.put(F_PHONE, cursor.getString(1));
                map.put(F_BLOCK_OPT_ID, cursor.getInt(2));
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

    public int findBlockOptId(String phone) {
        if (phone == null || phone.equals("")) {
            return -1;
        }

        SQLiteDatabase db = this.getReadableDatabase();

        String sql = String.format("select %s from %s where phone=?", F_BLOCK_OPT_ID, T_BLACKLIST);
        Cursor cursor = db.rawQuery(sql, new String[]{phone});
        int id = cursor.moveToFirst() ? cursor.getInt(0) : -1;
        cursor.close();
        db.close();

        return id;
    }
}
