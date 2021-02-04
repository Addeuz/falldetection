package com.example.falldetector.ui.emergency;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.falldetector.database.Contact;
import com.example.falldetector.database.DatabaseHandler;

import java.util.List;

public class EmergencyViewModel extends ViewModel {


    private final MutableLiveData<List<Contact>> mContacts = new MutableLiveData<>();
    private int nbContacts;
    private final DatabaseHandler db;

    public EmergencyViewModel(Context context) {
        db = new DatabaseHandler(context);
        updateLiveDataList();
    }

    public void clearList(Contact contact) {
        db.deleteContact(contact);
        updateLiveDataList();
    }

    public MutableLiveData<List<Contact>> getContacts() {
        return mContacts;
    }

    private void updateLiveDataList() {
        List<Contact> contactList = db.getAllContacts();
        nbContacts = contactList.size();
        mContacts.postValue(contactList);
    }

    @Override
    protected void onCleared() {
        db.close();
        super.onCleared();
    }
}