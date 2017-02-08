package me.caketalk.blacklist.dao;

import android.content.ContentValues;
import me.caketalk.blacklist.model.Blacklist;

import java.util.List;
import java.util.Map;

/**
 * @author Rock Huang
 * @version 0.1
 */
public interface IBlacklistDao {

    /**
     * Adds a new phone.
     *
     * @param phone A instance of phone.
     * @return boolean - True if added successfully.
     */
    boolean add(Blacklist phone);

    /**
     * Removes a phone by given number.
     *
     * @param number A phone number.
     * @return int - Returns 1 if removed successfully.
     */
    int remove(String number);

    /**
     * Removes a phone by given id.
     *
     * @param id A phone instance id.
     * @return int - Returns 1 if removed successfully.
     */
    int remove(int id);

    /**
     * Updates a phone.
     *
     * @param values A content values to be updated.
     * @param number A given number to indicate which phone should be updated.
     * @return int - Returns 1 if updated successfully.
     */
    int update(ContentValues values, String number);

    /**
     * Gets list of phones.
     *
     * @return List - Returns a list of map.
     */
    List<Map<String, Object>> getAllBlacklist();

    /**
     * Finds all blocked phone with count of intercepted history records.
     *
     * @return List - Returns a list of map.
     */
    List<Map<String,Object>> findBlockedPhonesAndHistoryCounts();

    /**
     * To determine if given phone exists.
     *
     * @param phone A phone instance.
     * @return boolean - Returns true if the given phone exists.
     */
    boolean isExist(String phone);

    /**
     * Finds a block option id.
     *
     * @param phone A phone instance.
     * @return int - Block option id.
     */
    int findBlockOptId(String phone);

}
