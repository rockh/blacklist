package me.caketalk.blacklist.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import me.caketalk.blacklist.model.Blacklist;
import me.caketalk.blacklist.model.History;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rock
 * Date: 06/05/13
 * Time: 14:29
 */
public class BlacklistDao extends BaseDao implements IBlacklistDao {

    private static final String F_ID = "_id";
    private static final String F_PHONE = "phone";
    private static final String F_BLOCK_OPT_ID = "block_opt_id";
    private static final String F_COMMENT = "comment";

    public BlacklistDao(Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(Blacklist blacklist) {
        ContentValues v = new ContentValues(3);
        v.put(F_PHONE, blacklist.getPhone());
        v.put(F_BLOCK_OPT_ID, blacklist.getBlockOptId());
        v.put(F_COMMENT, blacklist.getComment());
        return insert(T_BLACKLIST, v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int remove(String phone) {
        return delete(T_BLACKLIST, "phone=?", phone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int remove(int id) {
        return delete(T_BLACKLIST, "_id=" + id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(ContentValues v, String phone) {
        return update(T_BLACKLIST, v, "phone=?", phone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> getAllBlacklist() {
        String cmd = String.format("SELECT DISTINCT %s, %s, %s FROM %s", F_ID, F_PHONE, F_BLOCK_OPT_ID, T_BLACKLIST);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(cmd, null);

        List<Map<String, Object>> blacklist = new ArrayList<Map<String, Object>>(cursor.getCount());
        if (cursor.moveToFirst()) do {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(F_ID, cursor.getString(0));
            map.put(F_PHONE, cursor.getString(1));
            map.put(F_BLOCK_OPT_ID, cursor.getInt(2));
            blacklist.add(map);
        } while (cursor.moveToNext());
        cursor.close();
        db.close();

        return blacklist;
    }

    @Override
    public List<Map<String, Object>> findBlockedPhonesAndHistoryCounts() {
        String sql = "SELECT _id, phone, block_opt_id, " +
                "(SELECT COUNT(_id) FROM history WHERE number=blacklist.phone AND action=1) AS call_counts, " +
                "(SELECT COUNT(_id) FROM history WHERE number=blacklist.phone AND action=2) AS sms_counts " +
                "FROM blacklist";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        List<Map<String, Object>> blacklist = new ArrayList<Map<String, Object>>(cursor.getCount());
        if (cursor.moveToFirst()) do {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(F_ID, cursor.getInt(0));
            map.put(F_PHONE, cursor.getString(1));
            map.put(F_BLOCK_OPT_ID, cursor.getInt(2));
            map.put(History.CALL_COUNTS, cursor.getInt(3));
            map.put(History.SMS_COUNTS, cursor.getInt(4));
            map.put(History.TOTAL_COUNTS, cursor.getInt(3) + cursor.getInt(4));
            blacklist.add(map);
        } while (cursor.moveToNext());

        cursor.close();
        db.close();

        return blacklist;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExist(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select 1 from " + T_BLACKLIST + " where phone=?", new String[]{phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
