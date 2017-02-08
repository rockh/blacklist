package me.caketalk.blacklist.manager;

import android.content.Context;
import me.caketalk.blacklist.dao.HistoryDao;
import me.caketalk.blacklist.dao.IBlacklistDao;
import me.caketalk.blacklist.dao.IHistoryDao;
import me.caketalk.blacklist.model.History;
import me.caketalk.blacklist.model.PhoneAction;

import java.util.List;
import java.util.Map;

/**
 * @author Rock created at 18:04 05/05/13
 */
public class BlacklistManager implements IBlacklistManager{

    private IBlacklistDao blacklistDao; // TODO: refactor: method calls in UI layer should use manager instead of blacklistDao
    private IHistoryDao historyDao;
    private BlacklistManager() {}

    public BlacklistManager(Context context) {
        historyDao = new HistoryDao(context);
    }

    public BlacklistManager(IHistoryDao historyDao) {
        this.historyDao = historyDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean recordsHistory(String number, PhoneAction action, String detail) {
        History history = new History(number, action);
        history.setDetail(detail);
        return historyDao.add(history);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> getHistoryRecords(String phoneNumber) {
        return historyDao.findListOfMap(phoneNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeRecord(int id) {
        return historyDao.deleteById(id) == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int removeCallHistory(String phoneNumber) {
        return historyDao.deleteByPhoneAction(phoneNumber, PhoneAction.CALL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int removeSmsHistory(String phoneNumber) {
        return historyDao.deleteByPhoneAction(phoneNumber, PhoneAction.SMS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int removeAllHistory(String phoneNumber) {
        return historyDao.deleteByPhoneAction(phoneNumber, PhoneAction.BOTH);
    }

}