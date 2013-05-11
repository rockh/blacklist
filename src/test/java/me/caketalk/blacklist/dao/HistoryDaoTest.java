package me.caketalk.blacklist.dao;

import android.database.sqlite.SQLiteDatabase;
import me.caketalk.blacklist.MainActivity;
import me.caketalk.blacklist.model.History;
import me.caketalk.blacklist.model.PhoneAction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rock
 * Date: 08/05/13
 * Time: 19:47
 */
@RunWith(RobolectricTestRunner.class)
public class HistoryDaoTest {

    private String phoneNumber = "01158882978";
    private IHistoryDao historyDao;
    private SQLiteDatabase db;

    @Before
    public void setUp() throws Exception {
        historyDao = new HistoryDao(new MainActivity());
        db = ((HistoryDao) historyDao).getWritableDatabase();
    }

    @After
    public void tearDown() throws Exception {
        db.close();
        historyDao = null;
    }

    @Test
    public void testAddHistory() {
        History history = new History(phoneNumber, PhoneAction.CALL);
        Assert.assertTrue("Should return true but false", historyDao.add(history));
    }

    @Test
    public void testFindHistoryListByPhoneNumber() {
        prepareInterceptedHistoryForTest();
        List<History> records = historyDao.findByPhoneNumber(phoneNumber);

        Assert.assertNotNull("Should not be null", records);
        Assert.assertTrue("Should have 5 history records", records.size() == 5);
        Assert.assertTrue("The list item should be a History instance", records.get(0) instanceof History);
    }

    @Test
    public void testFindListOfMapByPhoneNumber() {
        prepareInterceptedHistoryForTest();
        List<Map<String, Object>> records = historyDao.findListOfMap(phoneNumber);
        Assert.assertNotNull("The list item should be a Map instance", records.get(0) instanceof Map);
    }

    @Test
    public void testDeleteById() {
        prepareInterceptedHistoryForTest();
        int affectedRow = historyDao.deleteById(1);
        Assert.assertTrue("Should return 1 but " + affectedRow, affectedRow == 1);
    }

    @Test
    public void testDeleteCallHistory() {
        prepareInterceptedHistoryForTest();
        int affectedRow = historyDao.deleteByPhoneAction(phoneNumber, PhoneAction.CALL);
        Assert.assertTrue("Should return 2 but " + affectedRow, affectedRow == 2);
    }

    @Test
    public void testDeleteSmsHistory() {
        prepareInterceptedHistoryForTest();
        int affectedRow = historyDao.deleteByPhoneAction(phoneNumber, PhoneAction.SMS);
        Assert.assertTrue("Should return 3 but " + affectedRow, affectedRow == 3);
    }

    @Test
    public void testDeleteAllHistory() {
        prepareInterceptedHistoryForTest();
        int affectedRow = historyDao.deleteByPhoneAction(phoneNumber, null);
        Assert.assertTrue("Should return 5 but " + affectedRow, affectedRow == 5);
    }

    // 2 call and 3 SMS
    private void prepareInterceptedHistoryForTest() {
        db.execSQL("INSERT INTO history (number, action, detail) VALUES(?, ?, ?)", new String[]{phoneNumber, "1", null});
        db.execSQL("INSERT INTO history (number, action, detail) VALUES(?, ?, ?)", new String[]{phoneNumber, "1", null});
        db.execSQL("INSERT INTO history (number, action, detail) VALUES(?, ?, ?)", new String[]{phoneNumber, "2", "Hello there"});
        db.execSQL("INSERT INTO history (number, action, detail) VALUES(?, ?, ?)", new String[]{phoneNumber, "2", "Give a call"});
        db.execSQL("INSERT INTO history (number, action, detail) VALUES(?, ?, ?)", new String[]{phoneNumber, "2", "Please call me"});
    }

}
