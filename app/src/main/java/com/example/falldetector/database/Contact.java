package com.example.falldetector.database;

public class Contact {
    int _id;
    String _name;
    String _phone_number;

    public Contact() {}

    public Contact(int _id, String _name, String _phone_number) {
        this._id = _id;
        this._name = _name;
        this._phone_number = _phone_number;
    }

    public Contact(String _name, String _phone_number) {
        this._name = _name;
        this._phone_number = _phone_number;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getPhoneNumber() {
        return _phone_number;
    }

    public void setPhoneNumber(String _phone_number) {
        this._phone_number = _phone_number;
    }
}
