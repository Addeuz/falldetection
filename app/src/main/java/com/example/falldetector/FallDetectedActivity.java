package com.example.falldetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.falldetector.ui.home.HomeFragment;

public class FallDetectedActivity extends AppCompatActivity {
    private Handler handler;

    TextView fallDetectedInfo;
    Button fallDetectedButton;
    Boolean contactsAlerted = false;

    SmsManager smsManager = SmsManager.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_detected);
        System.out.println("Fall detected bruh");

        fallDetectedInfo = findViewById(R.id.fallDetectedInfo);
        fallDetectedButton = findViewById(R.id.fallDetectedButton);

        handler = new Handler();
    }

    @Override
    protected void onStart() {
        super.onStart();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                contactsAlerted = true;
                System.out.println("Text emergency contacts");
                fallDetectedInfo.setText("We have alerted your emergency contacts");
                fallDetectedButton.setText("Go back to start");

                // Send the text
                smsManager.sendTextMessage("+46768660622", null, "Hello test", null, null);
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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    // Overriding onBackPressed so that the user don't accidentally presses back when the fall has been detected
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}