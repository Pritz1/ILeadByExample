package com.eis.ileadbyexample.Models;

public class User {

    private String log_id, ecode, mobile_no, deldate, logged_in_status, user_status;

    public User(String log_id, String ecode, String mobile_no, String deldate, String logged_in_status, String user_status) {
        this.log_id = log_id;
        this.ecode = ecode;
        this.mobile_no = mobile_no;
        this.deldate = deldate;
        this.logged_in_status = logged_in_status;
        this.user_status = user_status;
    }

    public String getLog_id() {
        return log_id;
    }

    public String getEcode() {
        return ecode;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public String getDeldate() {
        return deldate;
    }

    public String getLogged_in_status() {
        return logged_in_status;
    }

    public String getUser_status() {
        return user_status;
    }
}
