package com.amt.sendsms;

public class SMSModelClass {
    public String _id, address, body, date, type;

    public SMSModelClass(String _id, String address, String body, String date, String type) {
        this._id = _id;
        this.address = address;
        this.body = body;
        this.date = date;
        this.type = type;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}