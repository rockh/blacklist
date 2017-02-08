package me.caketalk.blacklist.dao;


import me.caketalk.blacklist.model.History;
import me.caketalk.blacklist.model.PhoneAction;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rock
 * Date: 08/05/13
 * Time: 19:40
 */
public interface IHistoryDao {

    boolean add(History history);

    List<History> findByPhoneNumber(String number);

    List<Map<String,Object>> findListOfMap(String number);

    int deleteById(int id);

    int deleteByPhoneAction(String phoneNumber, PhoneAction call);
}
