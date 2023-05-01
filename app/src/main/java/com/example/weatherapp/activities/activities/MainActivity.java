package com.example.weatherapp.activities.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.weatherapp.R;
import com.example.weatherapp.activities.network.Network;
import com.example.weatherapp.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    String currentLocation;

    String minTemp;

    String currentTemp;

    String maxTemp;
    String latitude;
    String longitude;

    private final String TAG = "ROLDAN DEBUGGING";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        latitude = intent.getStringExtra("LATITUDE");
        longitude = intent.getStringExtra("LONGITUDE");
        currentLocation = latitude + "," +longitude;
        Log.v(TAG,currentLocation);

        final OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(Network.openWeatherAPI + "forecast.json?key=" + Network.getOpenWeatherAPIKey
                        + "&q=" + currentLocation)
                .build();



        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground (Void...voids){
                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()){
                        return null;
                    }
                    return response.body().string();

                }  catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                    if (s != null){
                        JSONObject jsonResponse = null;

                        try{
                            jsonResponse = new JSONObject(s);

                            JSONObject locationObject = jsonResponse.getJSONObject("location");
                            JSONObject currentObject = jsonResponse.getJSONObject("current");
                            JSONObject forecastObject = jsonResponse.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0);
                            JSONObject dayObject = forecastObject.getJSONObject("day");

                            String iconWeather = currentObject.getJSONObject("condition").getString("icon");
                            Glide.with(MainActivity.this)
                                    .load("https:"+iconWeather)
                                    .into(binding.weatherPic);

                            minTemp = getString(R.string.min_text, dayObject.getString("mintemp_c"), getString(R.string.celcius));
                            currentTemp = getString(R.string.current_text, currentObject.getString("temp_c"), getString(R.string.celcius));
                            maxTemp = getString(R.string.max_text, dayObject.getString("maxtemp_c"), getString(R.string.celcius));
//

                            binding.locationText.setText(locationObject.getString("name"));
                            binding.miniumText.setText(minTemp);
                            binding.currentText.setText(currentTemp);
                            binding.maximumText.setText(maxTemp);


                        } catch (JSONException e){
                            throw new RuntimeException(e);
                        }
                    }
                }
            };
            asyncTask.execute();
        }
}


