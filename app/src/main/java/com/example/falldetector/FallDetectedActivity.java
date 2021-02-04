package com.example.falldetector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.falldetector.database.Contact;
import com.example.falldetector.database.DatabaseHandler;
import com.example.falldetector.ui.home.HomeFragment;

import java.util.List;

public class FallDetectedActivity extends AppCompatActivity {
    private Handler handler;

    TextView fallDetectedInfo;
    Button fallDetectedButton;
    Boolean contactsAlerted = false;

    DatabaseHandler db;

    List<Contact> emergencyContacts;

    private GpsTracker gpsTracker;
    private double longitude;
    private double latitude;

    SmsManager smsManager = SmsManager.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_detected);
        createNotificationChannel();
        System.out.println("Fall detected bruh");

        fallDetectedInfo = findViewById(R.id.fallDetectedInfo);
        fallDetectedButton = findViewById(R.id.fallDetectedButton);

        db = new DatabaseHandler(this);
        emergencyContacts = db.getAllContacts();
        handler = new Handler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // get the gps location
                gpsTracker = new GpsTracker(FallDetectedActivity.this);
                if(gpsTracker.canGetLocation()) {
                    latitude = gpsTracker.getLatitude();
                    longitude = gpsTracker.getLongitude();
                } else {
                    gpsTracker.showSettingsAlert();
                }
                System.out.println("Longitude: " + longitude + " Latitude: " + latitude);
                contactsAlerted = true;
                System.out.println("Text emergency contacts");
                fallDetectedInfo.setText("We have alerted your emergency contacts");
                fallDetectedButton.setText("Go back to start");

                for (Contact cn : emergencyContacts) {
                    // Send the text
                    smsManager.sendTextMessage(cn.getPhoneNumber(), null, "You can see Andreas location here: \n http://www.google.com/maps/place/"+latitude+","+longitude, null, null);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(FallDetectedActivity.this, "1")
                        .setSmallIcon(R.drawable.ic_baseline_person_24)
                        .setContentTitle("Fall Detector")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentText("We have sent out a SMS to your emergency contacts");

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(FallDetectedActivity.this);
                notificationManager.notify(1, builder.build());
            }
        }, 5000);
    }


    public void fallDetected(View view) {
        if (!contactsAlerted) {
            System.out.println("IM OKAY");
            handler.removeCallbacksAndMessages(null);
            // Reset falls detected so that we can detect more falls
            RidingActivity.resetFallsDetected();
            // Go back and continue the timer and tracking accelerometer if user is okay
            finish();
        } else {
            RidingActivity.resetFallsDetected();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    // Overriding onBackPressed so that the user don't accidentally presses back when the fall has been detected
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FallDetectionChannel";
            String description = "A notification channel for the FallDetector app";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String CHANNEL_ID = "1";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}