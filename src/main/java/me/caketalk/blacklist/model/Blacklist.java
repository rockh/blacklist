package me.caketalk.blacklist.model;

import java.util.Date;

/**
 * @author Rock Huang
 * @version 0.1
 */
public class Blacklist {

    private int id;
    private String phone;
    private int blockOptId;
    private String comment;
    private Date createDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getBlockOptId() {
        return blockOptId;
    }

    public void setBlockOptId(int blockOptId) {
        this.blockOptId = blockOptId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String toString() {
        return String.format("{id:%d, phone:%s, comment:%s, createDate:%tD}", id, phone, comment, createDate);
    }
}
