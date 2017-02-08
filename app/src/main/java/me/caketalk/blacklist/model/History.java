package me.caketalk.blacklist.model;

/**
 * Created with IntelliJ IDEA.
 * User: rock
 * Date: 08/05/13
 * Time: 21:55
 */
public class History {

    public static final String F_ID = "_id";
    public static final String TABLE_NAME = "history";
    public static final String F_NUMBER = "number";
    public static final String F_DETAIL = "detail";
    public static final String F_ACTION = "action";
    public static final String F_CREATED_DATE = "created_date";
    public static final String CALL_COUNTS = "call_counts";
    public static final String SMS_COUNTS = "sms_counts";
    public static final String TOTAL_COUNTS = "total_counts";

    private int id;
    private String number;
    private String detail;
    private PhoneAction action;
    private String createdDate;

    public History(String number, PhoneAction action) {
        this.number = number;
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? "" : detail;
    }

    public PhoneAction getAction() {
        return action;
    }

    public void setAction(PhoneAction action) {
        this.action = action;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getCreatedDate(boolean brief) {
        if (createdDate != null && brief) {
            return createdDate.substring(5);
        } else {
            return createdDate;
        }
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("History");
        sb.append("{id=").append(id);
        sb.append(", number='").append(number).append('\'');
        sb.append(", detail='").append(detail).append('\'');
        sb.append(", action=").append(action);
        sb.append(", createdDate='").append(createdDate).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
