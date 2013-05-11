package me.caketalk.blacklist.dao;

import android.database.sqlite.SQLiteDatabase;
import me.caketalk.blacklist.MainActivity;
import me.caketalk.blacklist.model.Blacklist;
import me.caketalk.blacklist.model.History;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: rock
 * Date: 06/05/13
 * Time: 14:31
 */
//@DatabaseConfig.UsingDatabaseMap(SQLiteMap.class)
@RunWith(RobolectricTestRunner.class)
public class BlacklistDaoTest {

    final private String phoneNumber = "01158882978";
    private IBlacklistDao blacklistDao;
    private SQLiteDatabase db;

    @Before
    public void setUp() {
        blacklistDao = new BlacklistDao(new MainActivity());
        db = ((BlacklistDao) blacklistDao).getWritableDatabase();
    }

    @After
    public void tearDown() {
        db.close();
        blacklistDao = null;
    }

    @Test
    public void testAdd() {
        Blacklist blacklist = new Blacklist(phoneNumber);
        Assert.assertTrue("Should return true but false", blacklistDao.add(blacklist));
    }

    @Test
    public void testIsExist() {
        prepareBlacklistForTest();
        Assert.assertTrue("Should return true but false", blacklistDao.isExist(phoneNumber));
    }

    @Test
    public void testRemove() {
        prepareBlacklistForTest();
        int i = blacklistDao.remove(phoneNumber);
        Assert.assertTrue("Should return 1 but " + i, i == 1);
    }

    @Test
    public void testFindBlockedPhoneAndHistoryCounts() {
        prepareBlacklistForTest();
        prepareInterceptedHistoryForTest();
        List<Map<String, Object>> list = blacklistDao.findBlockedPhonesAndHistoryCounts();
        Map map = list.get(0);
        int callCounts = (Integer) map.get(History.CALL_COUNTS);
        int smsCounts = (Integer) map.get(History.SMS_COUNTS);
        int totalCounts = (Integer) map.get(History.TOTAL_COUNTS);
        Assert.assertTrue("Should have 2 records for calls", callCounts==2);
        Assert.assertTrue("Should have 1 record for SMS", smsCounts==1);
        Assert.assertTrue("Should have total 3 records", totalCounts==3);
    }

    private void prepareBlacklistForTest() {
        db.execSQL("INSERT INTO blacklist (phone) VALUES(?)", new String[]{phoneNumber});
    }

    private void prepareInterceptedHistoryForTest() {  // adds two records for call action and one for sms
        db.execSQL("INSERT INTO history (number, action, detail) VALUES(?, ?, ?)", new String[]{phoneNumber, "1", null});
        db.execSQL("INSERT INTO history (number, action, detail) VALUES(?, ?, ?)", new String[]{phoneNumber, "1", null});
        db.execSQL("INSERT INTO history (number, action, detail) VALUES(?, ?, ?)", new String[]{phoneNumber, "2", "Hello there"});
    }
}