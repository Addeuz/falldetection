package com.example.falldetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.falldetector.database.DatabaseHandler;
import com.example.falldetector.database.Ride;

import java.util.List;

public class SaveRideActivity extends AppCompatActivity {

    public static final String HOW_LONG_RIDE = "com.example.falldetector.HOW_LONG_RIDE";
    public static final String STOP_TIME = "com.example.falldetector.STOP_TIME";

    Chronometer saveChronometer;
    TextView time;
    String howLongWasRideText;
    long stopTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_ride);

        time = findViewById(R.id.timeRide);
        saveChronometer = findViewById(R.id.saveChronometer);


        if(savedInstanceState != null) {
            howLongWasRideText = savedInstanceState.getString(HOW_LONG_RIDE);
            time.setText(howLongWasRideText);
        } else {
            Intent intent = getIntent();
            stopTime = intent.getLongExtra(RidingActivity.TIME_WHEN_ENDED, 0);
            saveChronometer.setBase(stopTime);
            howLongWasRideText = saveChronometer.getText().toString();
            time.setText(howLongWasRideText);
        }
    }

    public void submitForm(View view) {
        DatabaseHandler db = new DatabaseHandler(this);


        System.out.println("Ride was: " + howLongWasRideText);

        final Spinner howWasRideSpinner = (Spinner) findViewById(R.id.howWasRideEdit);
        String howWasRideText = howWasRideSpinner.getSelectedItem().toString();
        System.out.println("Ride was: " + howWasRideText);

        final EditText howWasWeatherEdit = (EditText) findViewById(R.id.howWasWeatherEdit);
        String howWasWeatherText = howWasWeatherEdit.getText().toString();

        System.out.println("Weather was: " + howWasWeatherText);

        db.addRide(new Ride(howLongWasRideText, howWasRideText, howWasWeatherText));

        List<Ride> rides = db.getAllRides();

        for(Ride rd : rides) {
            System.out.println("Ride = id: " + rd.getId() + ", How long: " + rd.getHowLongWasRide() + ", How was ride? " + rd.getHowWasRide() + ", How was weather? " + rd.getHowWasWeather());
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // save running variable so that the the timer doesn't reset on landscape orientation
        System.out.println("Saving: " + howLongWasRideText);
        outState.putString(HOW_LONG_RIDE, howLongWasRideText);
        outState.putLong(STOP_TIME, stopTime);
        super.onSaveInstanceState(outState);
    }
}