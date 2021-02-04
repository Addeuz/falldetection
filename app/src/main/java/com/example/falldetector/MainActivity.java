package com.example.falldetector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.controls.actions.ControlAction;
import android.widget.TextView;
import android.widget.Toast;

import com.example.falldetector.database.Contact;
import com.example.falldetector.database.DatabaseHandler;
import com.example.falldetector.ui.emergency.EmergencyFragment;
import com.example.falldetector.ui.history.HistoryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION_SEND_SMS = 1;
    private final int REQUEST_PERMISSION_READ_CONTACTS = 2;
    public final int REQUEST_PERMISSION_GPS = 3;
    private final int REQUEST_PERMISSION_INTERNET = 4;



    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHandler(this);
        EmergencyFragment.printContacts(this, db);
        HistoryFragment.printHistory(this, db);


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_emergency, R.id.navigation_history)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        final TextView weatherText = findViewById(R.id.text_home);


        // Check permission to send SMS
        int permissionCheckSendSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permissionCheckSendSMS != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                showExplanation(
                        "Need permission to send SMS",
                        "We need to be able to send SMS to your emergency contact",
                        Manifest.permission.SEND_SMS,
                        REQUEST_PERMISSION_SEND_SMS
                );
                showExplanation(
                        "Need permission to Contacts",
                        "We need permission to be able to send SMS to your emergency contact",
                        Manifest.permission.READ_CONTACTS,
                        REQUEST_PERMISSION_READ_CONTACTS
                );
            } else {
                requestPermission(Manifest.permission.SEND_SMS, REQUEST_PERMISSION_SEND_SMS);
                requestPermission(Manifest.permission.READ_CONTACTS, REQUEST_PERMISSION_READ_CONTACTS);

            }
        } else {
//            Toast.makeText(MainActivity.this, "Permission for sending SMS already granted", Toast.LENGTH_SHORT).show();
        }

        // Check permission to access contacts
        int permissionCheckReadContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permissionCheckReadContacts != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                showExplanation(
                        "Need permission to Contacts",
                        "We need permission to be able to send SMS to your emergency contact",
                        Manifest.permission.READ_CONTACTS,
                        REQUEST_PERMISSION_READ_CONTACTS
                );
            } else {
                requestPermission(Manifest.permission.READ_CONTACTS, REQUEST_PERMISSION_READ_CONTACTS);

            }
        } else {
//            Toast.makeText(MainActivity.this, "Permission for sending SMS already granted", Toast.LENGTH_SHORT).show();
        }
        // Check permission to internet
//        int permissionInternet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
//        if (permissionInternet != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
//                showExplanation(
//                        "Need permission to Internet",
//                        "We need permission to access the internet to get weather info",
//                        Manifest.permission.INTERNET,
//                        REQUEST_PERMISSION_INTERNET
//                );
//            } else {
//                requestPermission(Manifest.permission.INTERNET, REQUEST_PERMISSION_INTERNET);
//
//            }
//        } else {
////            Toast.makeText(MainActivity.this, "Permission for accessing INTERNET already granted", Toast.LENGTH_SHORT).show();
//        }

        // Check permission to use GPS
        int permissionCheckGPSFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckGPSCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheckGPSFine != PackageManager.PERMISSION_DENIED && permissionCheckGPSCoarse != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showExplanation(
                        "Need permission to read GPS",
                        "We need permission to read your gps status so that we can send your gps location to your emergency contacts",
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        REQUEST_PERMISSION_GPS
                );
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_GPS);
            }
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            System.out.println(contactData);
            Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    System.out.println(name);

                    if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor phones = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                                null,
                                null
                        );
                        while (phones.moveToNext()) {
                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            System.out.println("Phone number: " + phoneNumber);
                            /// ADD NAME AND PHONE NUMBER HERE TO CONTACTS TABLE
                            db.addContact(new Contact(name, phoneNumber));

//                            List<Contact> contacts = db.getAllContacts();
//
//                            for (Contact cn : contacts) {
//                                String log = "Id: " + cn.getId() + " ,Name: " + cn.getName() + " ,Phone: " +
//                                        cn.getPhoneNumber();
//                                // Writing Contacts to system out
//                                System.out.println(log);
//                            }
                            System.out.println("from main");
                            EmergencyFragment.printContacts(this, db);
                        }
                        phones.close();
                    }
                }
            }
            cursor.close();
        } else {
            Toast.makeText(this, "No contact chosen", Toast.LENGTH_SHORT).show();
        }



    }



}