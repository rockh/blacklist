package me.caketalk.blacklist.manager;

import me.caketalk.blacklist.dao.IHistoryDao;
import me.caketalk.blacklist.model.History;
import me.caketalk.blacklist.model.PhoneAction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Rock Created at 11:12 09/05/13
 */
public class BlacklistManagerTest {

    private String phoneNumber = "01158882978";
    private IBlacklistManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new BlacklistManager(new HistoryDaoImplTest());
    }

    @After
    public void tearDown() throws Exception {
        manager = null;
    }

    @Test
    public void testRecordsHistory() {
        Assert.assertTrue("Should return true but false",
                manager.recordsHistory(phoneNumber, PhoneAction.CALL, null));
    }

    @Test
    public void testGetHistoryRecordsByPhoneNumber() {
        List<Map<String, Object>> records = manager.getHistoryRecords(phoneNumber);
        Assert.assertTrue("Should have 2 records", records.size()==2);
    }

    @Test
    public void testRemoveRecordById() {
        Assert.assertTrue("Should return true but false", manager.removeRecord(1));
    }

    @Test
    public void testRemoveCallHistory() {
        int affectedRow = manager.removeCallHistory(phoneNumber);
        Assert.assertTrue("Should return 5 but false", affectedRow == 5);
    }

    private class HistoryDaoImplTest implements IHistoryDao {
        @Override
        public boolean add(History history) {
            return true;
        }
        @Override
        public List<History> findByPhoneNumber(String number) {
            // Simulates 2 history records
            List<History> records = new ArrayList<History>();
            records.add(new History(phoneNumber, PhoneAction.CALL));
            records.add(new History(phoneNumber, PhoneAction.SMS));
            return records;
        }
        @Override
        public int deleteById(int id) {
            return 1;
        }

        @Override
        public int deleteByPhoneAction(String phoneNumber, PhoneAction call) {
            return 5;
        }

        @Override
        public List<Map<String, Object>> findListOfMap(String number) {
            List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();

            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put(History.F_NUMBER, phoneNumber);
            map1.put(History.F_ACTION, PhoneAction.CALL);
            records.add(map1);

            Map<String, Object> map2 = new HashMap<String,Object>();
            map2.put(History.F_NUMBER, phoneNumber);
            map2.put(History.F_ACTION, PhoneAction.SMS);
            records.add(map2);

            return records;
        }
    }
}
