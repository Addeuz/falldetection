package com.example.falldetector.ui.history;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.falldetector.database.DatabaseHandler;
import com.example.falldetector.database.Ride;

import java.util.List;

public class HistoryViewModel extends ViewModel {

    private final MutableLiveData<List<Ride>> mRides = new MutableLiveData<>();
    private int nbRides;
    private final DatabaseHandler db;

    public HistoryViewModel(Context context) {
        db = new DatabaseHandler(context);
        updateLiveDataList();
    }

    public void clearList(Ride ride) {
        db.deleteRide(ride);
        updateLiveDataList();
    }

    public MutableLiveData<List<Ride>> getRides() {
        return mRides;
    }

    private void updateLiveDataList() {
        List<Ride> rideList = db.getAllRides();
        nbRides = rideList.size();
        mRides.postValue(rideList);
    }

    @Override
    protected void onCleared() {
        db.close();
        super.onCleared();
    }
}