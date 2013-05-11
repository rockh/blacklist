package me.caketalk.blacklist.manager;

import me.caketalk.blacklist.model.PhoneAction;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rock
 * Date: 09/05/13
 * Time: 09:46
 */
public interface IBlacklistManager {

    /**
     * Records intercepted history.
     *
     * @param number A phone number.
     * @param action Call or SMS.
     * @param detail SMS detail if any. Sets to null if action is a call.
     * @return boolean - Returns true if recorded successfully.
     */
    boolean recordsHistory(String number, PhoneAction action, String detail);

    /**
     * Gets a list of history records by given phone number.
     *
     * @param phoneNumber A phone number.
     * @return List - Returns a list of history data encapsulated in Map.
     */
    List<Map<String, Object>> getHistoryRecords(String phoneNumber);

    /**
     * Removes a record according to given id.
     *
     * @param id A history record id.
     * @return boolean - Returns true if removed successfully.
     */
    boolean removeRecord(int id);

    /**
     * Removes all call history by specified phone number.
     *
     * @param phoneNumber A specified phone number.
     * @return int - Returns affected row counts.
     */
    int removeCallHistory(String phoneNumber);

    /**
     * Removes all SMS history by specified phone number.
     *
     * @param phoneNumber A specified phone number.
     * @return int - Returns affected row counts.
     */
    int removeSmsHistory(String phoneNumber);

    /**
     * Removes all history by specified phone number.
     *
     * @param phoneNumber A specified phone number.
     * @return int - Returns affected row counts.
     */
    int removeAllHistory(String phoneNumber);
}
