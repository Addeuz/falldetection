package com.example.falldetector.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.falldetector.GpsTracker;
import com.example.falldetector.Network;
import com.example.falldetector.R;
import com.example.falldetector.RidingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    RequestQueue queue;
    String url;
    String apiKey;
    private GpsTracker gpsTracker;
    private double longitude;
    private double latitude;

    SharedPreferences sharedPref;
    SharedPreferences.Editor sharedPrefEditor;

    TextView textView;


    private HomeViewModel homeViewModel;

    public static final String INTENT_MESSAGE = "com.example.falldetector.TEST";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
//        homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        sharedPrefEditor = sharedPref.edit();

        System.out.println("Shared preferences: " + sharedPref.getString("outText", null));

        String savedWeather = sharedPref.getString("outText", null);

        if (savedWeather == null) {
            apiKey = "3b9f499d7217f6836541a553a16fc4bd";
            queue = Volley.newRequestQueue(getContext());

            gpsTracker = new GpsTracker(getContext());
            if(gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
            } else {
                gpsTracker.showSettingsAlert();
            }
            System.out.println("Longitude: " + String.valueOf(longitude).split("\\.")[0] + " Latitude: " + String.valueOf(latitude).split("\\.")[0]);

            String longitudeString = Double.toString(longitude);
            String[] separatedLong = longitudeString.split("\\.");
            System.out.println(separatedLong[0]);




            url = "https://api.openweathermap.org/data/2.5/weather?lat=" + String.valueOf(latitude).split("\\.")[0] +"&lon=" + String.valueOf(longitude).split("\\.")[0] + "&appid="+ apiKey + "&units=metric";
            if (Network.isNetworkAvailable(getContext())) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        response -> {
                            // Display the first 500 characters of the response string.
                            System.out.println(response);
                            try {

                                JSONObject responseObject = new JSONObject(response);
                                JSONArray weather = responseObject.getJSONArray("weather");
                                JSONObject main = responseObject.getJSONObject("main");
                                JSONObject weatherInfo = weather.getJSONObject(0);

                                System.out.println(main.getDouble("temp"));

                                System.out.println(weatherInfo.getString("description"));

                                String outText = "Right now it is " + weatherInfo.getString("description") + " and " + main.getDouble("temp") + " °C.\nBut it feels like " + main.getDouble("feels_like") + " °C";

                                sharedPrefEditor.putString("outText", outText);
                                sharedPrefEditor.apply();

                                textView.setText(outText);



                            } catch (JSONException e) {
                                e.printStackTrace();
                                System.out.println(e.getMessage());
                            }
                        }, (VolleyError error) -> {
                    System.out.println(error);
                    textView.setText("Couldn't get the weather!");
                });
                queue.add(stringRequest);
            } else {
                System.out.println("HELLOOOO no internet");
                String outText = "No internet connection";

                textView.setText(outText);
            }
        } else {
            System.out.println("text from shared preferences");
            textView.setText(savedWeather);
        }






        Button startClockButton = (Button) root.findViewById(R.id.startClockButton);
        startClockButton.setOnClickListener(v -> {
            System.out.println("Hello from startclockbutton");
            Intent intent = new Intent(getContext(), RidingActivity.class);
            intent.putExtra(INTENT_MESSAGE, "Hello from main activity");
            startActivity(intent);
        });

        return root;
    }

//    public boolean isNetworkAvailable(Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//
//        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
//    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.top_bar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshWeatherAction:
                System.out.println("Refresh weather");
                sharedPrefEditor.clear();
                sharedPrefEditor.apply();

                textView.setText("Refreshing...");

                apiKey = "3b9f499d7217f6836541a553a16fc4bd";
                queue = Volley.newRequestQueue(getContext());

                gpsTracker = new GpsTracker(getContext());
                if(gpsTracker.canGetLocation()) {
                    latitude = gpsTracker.getLatitude();
                    longitude = gpsTracker.getLongitude();
                } else {
                    gpsTracker.showSettingsAlert();
                }
                System.out.println("Longitude: " + String.valueOf(longitude).split("\\.")[0] + " Latitude: " + String.valueOf(latitude).split("\\.")[0]);

                String longitudeString = Double.toString(longitude);
                String[] separatedLong = longitudeString.split("\\.");
                System.out.println(separatedLong[0]);




                url = "https://api.openweathermap.org/data/2.5/weather?lat=" + String.valueOf(latitude).split("\\.")[0] +"&lon=" + String.valueOf(longitude).split("\\.")[0] + "&appid="+ apiKey + "&units=metric";
                if (Network.isNetworkAvailable(getContext())) {
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            response -> {
                                // Display the first 500 characters of the response string.
                                System.out.println(response);
                                try {

                                    JSONObject responseObject = new JSONObject(response);
                                    JSONArray weather = responseObject.getJSONArray("weather");
                                    JSONObject main = responseObject.getJSONObject("main");
                                    JSONObject weatherInfo = weather.getJSONObject(0);

                                    System.out.println(main.getDouble("temp"));

                                    System.out.println(weatherInfo.getString("description"));

                                    String outText = "Right now it is " + weatherInfo.getString("description") + " and " + main.getDouble("temp") + " °C.\nBut it feels like " + main.getDouble("feels_like") + " °C";

                                    sharedPrefEditor.putString("outText", outText);
                                    sharedPrefEditor.apply();

                                    textView.setText(outText);



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    System.out.println(e.getMessage());
                                }
                            }, (VolleyError error) -> {
                        System.out.println(error);
                        textView.setText("Couldn't get the weather!");
                    });
                    queue.add(stringRequest);
                } else {
                    System.out.println("HELLOOOO no internet");
                    String outText = "No internet connection";

                    textView.setText(outText);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}