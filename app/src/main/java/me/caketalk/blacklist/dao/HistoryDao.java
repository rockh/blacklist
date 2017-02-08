package me.caketalk.blacklist.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import me.caketalk.blacklist.model.History;
import me.caketalk.blacklist.model.PhoneAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rock
 * Date: 08/05/13
 * Time: 21:01
 */
public class HistoryDao extends BaseDao implements IHistoryDao {

    private static final String SELECT_RECORDS_BY_NUMBER =
            "SELECT _id, action, detail, created_date FROM history " +
            "WHERE number=? ORDER BY created_date DESC";

    public HistoryDao(Context context) {
        super(context);
    }

    @Override
    public List<History> findByPhoneNumber(String number) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_RECORDS_BY_NUMBER, new String[]{number});

        List<History> records = new ArrayList<History>(cursor.getCount());
        if (cursor.moveToFirst()) do {
            PhoneAction action = cursor.getInt(1) == 1 ? PhoneAction.CALL : PhoneAction.SMS;
            History history = new History(number, action);
            history.setId(cursor.getInt(0));
            history.setDetail(cursor.getString(2));
            history.setCreatedDate(cursor.getString(3));
            records.add(history);
        } while (cursor.moveToNext());

        cursor.close();
        db.close();

        return records;
    }

    @Override
    public List<Map<String, Object>> findListOfMap(String number) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_RECORDS_BY_NUMBER, new String[]{number});

        List<Map<String, Object>> records = new ArrayList<Map<String, Object>>(cursor.getCount());
        if (cursor.moveToFirst()) do {
            PhoneAction action = cursor.getInt(1) == 1 ? PhoneAction.CALL : PhoneAction.SMS;
            Map record = new HashMap(4);
            record.put(History.F_NUMBER, number);
            record.put(History.F_ACTION, action);
            record.put(History.F_ID, cursor.getInt(0));
            record.put(History.F_DETAIL, cursor.getString(2));
            record.put(History.F_CREATED_DATE, cursor.getString(3));
            records.add(record);
        } while (cursor.moveToNext());

        cursor.close();
        db.close();

        return records;
    }

    @Override
    public int deleteById(int id) {
        return delete(History.TABLE_NAME, "_id=?", String.valueOf(id));
    }

    @Override
    public int deleteByPhoneAction(String phoneNumber, PhoneAction action) {
        boolean deleteAllHistory = ( action == null || action.getId() == 0 );
        if (deleteAllHistory) {
            return delete(History.TABLE_NAME, "number=?", phoneNumber);
        } else {
            return delete(History.TABLE_NAME, "number=? AND action=?", phoneNumber, String.valueOf(action.getId()));
        }
    }

    @Override
    public boolean add(History history) {
        ContentValues values = new ContentValues(3);
        values.put(History.F_NUMBER, history.getNumber());
        values.put(History.F_ACTION, history.getAction().getId());
        values.put(History.F_DETAIL, history.getDetail());
        return insert(History.TABLE_NAME, values);
    }
}
