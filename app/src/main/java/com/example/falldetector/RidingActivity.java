package com.example.falldetector;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.falldetector.ui.home.HomeFragment;

import java.text.DecimalFormat;

public class RidingActivity extends AppCompatActivity {
    private static final String BOOLEAN_MESSAGE = "com.example.falldetector.BOOLEAN";
    private static final String RIDE_BUTTON_TEXT = "com.example.falldetector.RIDE_BUTTON";
    private static final String TIME_WHEN_STOPPED = "com.example.falldetector.TIME_WHEN_STOPPED";

    public static final String TIME_WHEN_ENDED = "com.example.falldetector.TIME_WHEN_ENDED";

    public static int fallsDetected = 0;


    // Accelerometer stuff
    private SensorManager sensorManager;
    private Sensor accelerometer;


    private Chronometer rideChronometer;
    private long timeWhenStopped = 0;
    private boolean rideRunning;

    public static void resetFallsDetected() {
        fallsDetected = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riding);

        // Get intent from HomeFragment
        Intent intent = getIntent();
        String intentMessage = intent.getStringExtra(HomeFragment.INTENT_MESSAGE);

        TextView textView = findViewById(R.id.textView);
        textView.setText(intentMessage);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        rideChronometer = findViewById(R.id.ridingChronometer);

        if(savedInstanceState == null) {
            System.out.println("First start");
            rideChronometer.setBase(SystemClock.elapsedRealtime());
            rideChronometer.start();
            rideRunning = true;
        } else {
            rideRunning = savedInstanceState.getBoolean(BOOLEAN_MESSAGE);
            rideChronometer.setBase(SystemClock.elapsedRealtime() - savedInstanceState.getLong(TIME_WHEN_STOPPED));
            timeWhenStopped = SystemClock.elapsedRealtime() - rideChronometer.getBase();
            if (rideRunning) {
                rideChronometer.start();
            }
            Button pauseOrStartButton = findViewById(R.id.pauseOrStartButton);
            pauseOrStartButton.setText(savedInstanceState.getString(RIDE_BUTTON_TEXT));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Resume and register listener");
        if (accelerometer != null) {
            sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Pause and unregister listener");

        if (accelerometer != null) {
            sensorManager.unregisterListener(accelerometerListener);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // save running variable so that the the timer doesn't reset on landscape orientation
        System.out.println("Saving");
        if (rideRunning) {
            outState.putString(RIDE_BUTTON_TEXT, "Pause ride");
            timeWhenStopped = SystemClock.elapsedRealtime() - rideChronometer.getBase();
        } else {
            outState.putString(RIDE_BUTTON_TEXT, "Start ride");
        }
        outState.putLong(TIME_WHEN_STOPPED, timeWhenStopped);
        outState.putBoolean(BOOLEAN_MESSAGE, rideRunning);
        super.onSaveInstanceState(outState);
    }

    // Make sure that you don't exit the ride on accident
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure you want to exit?")
                .setMessage("The ride you currently is tracking will not be saved")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RidingActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("DESTROY");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("STOP");
    }

    public void pauseOrStartRide(View view) {
        Button pauseOrStartButton = findViewById(R.id.pauseOrStartButton);
        if (rideRunning) {
            rideChronometer.stop();
            timeWhenStopped = SystemClock.elapsedRealtime() - rideChronometer.getBase();
            rideRunning = false;
            pauseOrStartButton.setText("Start ride");
            // Unregister the sensor listener so that we don't track for falls if the timer is paused
            if (accelerometer != null) {
                System.out.println("Pause and unregister listener");
                sensorManager.unregisterListener(accelerometerListener);
            }
        } else {
            rideChronometer.start();
            rideChronometer.setBase(SystemClock.elapsedRealtime() - timeWhenStopped);
            rideRunning = true;
            pauseOrStartButton.setText("Pause ride");
            // Register the simulator again so that it starts tracking the accelerometer and looks for falls
            if (accelerometer != null) {
                System.out.println("Resume and register listener");
                sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    public void stopRide(View view) {
        // send user to save info about ride
        // send chronometer time as well
        new AlertDialog.Builder(this)
                .setTitle("Are you sure you want to stop the ride?")
                .setMessage("You'll be able to save the ride on the next page")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(RidingActivity.this, SaveRideActivity.class);
                        intent.putExtra(TIME_WHEN_ENDED, rideChronometer.getBase());
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                System.out.println("Hello mate");
                double x = event.values[0];
                double y = event.values[1];
                double z = event.values[2];

                double accelerationReader = Math.sqrt(Math.pow(x,2)
                        + Math.pow(y, 2)
                        + Math.pow(z, 2));

                DecimalFormat precision = new DecimalFormat("0.00");
                double accelerationRounded = Double.parseDouble(precision.format(accelerationReader));
                System.out.println(fallsDetected);
//                System.out.println(accelerationRounded);
                if(accelerationRounded > 0.3d && accelerationRounded < 1.7d) {
                    System.out.println("FALLIIIIIIING");
//                    if (accelerometer != null) {
//                        sensorManager.unregisterListener(accelerometerListener);
//                    }
                    fallDetected();

                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            System.out.println(sensor.toString() + "-" + accuracy);
        }
    };

    public void fallDetected() {
        if (fallsDetected == 0) {
            fallsDetected++;
            Intent intent = new Intent(this, FallDetectedActivity.class);
            startActivity(intent);
        }
    }
}