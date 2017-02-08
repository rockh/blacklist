package me.caketalk.blacklist.model;

/**
 * Created with IntelliJ IDEA.
 * User: rock
 * Date: 08/05/13
 * Time: 22:46
 */
public enum  PhoneAction {

    NONE(-1), BOTH(0), CALL(1), SMS(2);

    private int phoneActionId;

    private PhoneAction(int phoneActionId) {
        this.phoneActionId = phoneActionId;
    }

    public int getId() {
        return this.phoneActionId;
    }

}
