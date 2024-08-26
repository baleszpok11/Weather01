package com.example.weather01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DailyTemperature[] dailyTemperaturesArr = null;
    private String[] listToMainActivity = null;
    private ListView list1;
    private SharedPreferences sh;
    private int sameDay = 0;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.nija);

        list1 = findViewById(R.id.list1);
        list1.setOnItemClickListener((parent, view, i, id) -> {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

            if (i == 0) {
                intent.putExtra("hours", sameDay);
                for (int c = 0; c < sameDay; c++) {
                    String day = "hour" + c;
                    intent.putExtra(day, dailyTemperaturesArr[c]);
                }
            } else {
                int startIdx = sameDay + (i - 1) * 8;
                intent.putExtra("hours", 8);
                for (int c = 0; c < 8; c++) {
                    String day = "hour" + c;
                    intent.putExtra(day, dailyTemperaturesArr[startIdx + c]);
                }
            }

            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather() {
        sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String location = sh.getString("location", "3189595");
        String unit = sh.getString("unit", "metric");

        if (haveNetworkConnection()) {
            String url = Utils.buildUrl(location, unit);
            download(url);
        } else {
            alertDialog();
        }
    }

    private void download(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> parseJson(response),
                error -> Log.d("WeatherNinja", "Error loading data")
        );

        queue.add(stringRequest);
    }

    private void parseJson(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONArray dailyArr = object.getJSONArray(Utils.OWM_LIST);
            int arraySize = dailyArr.length();
            dailyTemperaturesArr = new DailyTemperature[arraySize];

            for (int i = 0; i < arraySize; i++) {
                JSONObject dailyData = dailyArr.getJSONObject(i);
                JSONObject wind = dailyData.getJSONObject(Utils.OWM_WIND);
                double wind_speed = wind.getDouble(Utils.OWM_WIND_SPEED);
                int deg = wind.getInt(Utils.OWM_WIND_DIRECTION);
                JSONObject clouds = dailyData.getJSONObject(Utils.OWM_CLOUDS);
                int cloudPercent = clouds.getInt(Utils.OWM_CLOUDS_ALL);
                JSONObject main = dailyData.getJSONObject(Utils.OWM_MAIN);
                double min = main.getDouble(Utils.OWM_MIN);
                double max = main.getDouble(Utils.OWM_MAX);
                int humidity = main.getInt(Utils.OWM_HUMIDITY);
                int pressure = main.getInt(Utils.OWM_PRESSURE);
                JSONArray weatherData = dailyData.getJSONArray(Utils.OWM_WEATHER);
                JSONObject weatherObject = weatherData.getJSONObject(0);
                String weatherMain = weatherObject.getString(Utils.OWM_WEATHER_MAIN);
                long dt = dailyData.getLong(Utils.OWM_DT);
                String dtText = dailyData.getString(Utils.OWM_DT_TXT);
                dailyTemperaturesArr[i] = new DailyTemperature(dt, min, max, pressure, humidity, weatherMain, dtText, wind_speed, deg, cloudPercent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dataSelection();
    }

    private void dataSelection() {
        listToMainActivity = new String[5];
        int j = 0;
        int daysProcessed = 0;

        while (j < dailyTemperaturesArr.length && daysProcessed < 5) {
            if (daysProcessed == 0) {
                // Process the first segment
                String temp = dailyTemperaturesArr[j].getDtText().substring(0, 10);
                int segmentLength = 0;

                // Count consecutive days with the same date
                while (j < dailyTemperaturesArr.length && dailyTemperaturesArr[j].getDtText().substring(0, 10).equals(temp)) {
                    segmentLength++;
                    j++;
                }

                // Add the first segment to listToMainActivity
                listToMainActivity[0] = dataForListView1(0, segmentLength);
                sameDay = segmentLength; // Update sameDay count
                daysProcessed++;
            } else {
                // Process subsequent segments
                listToMainActivity[daysProcessed] = dataForListView1(j, j + 8);
                j += 8;
                daysProcessed++;
            }
        }

        fillListView2();
    }

    private String dataForListView1(int firstDay, int lastDay) {
        double min = dailyTemperaturesArr[firstDay].getMin();
        double max = dailyTemperaturesArr[firstDay].getMax();
        for (int i = firstDay; i < lastDay; i++) {
            if (max < dailyTemperaturesArr[i].getMax())
                max = dailyTemperaturesArr[i].getMax();
            if (min > dailyTemperaturesArr[i].getMin())
                min = dailyTemperaturesArr[i].getMin();
        }
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MM.d.", Locale.US);
        String formattedDate = "";
        try {
            Date date = inputFormat.parse(dailyTemperaturesArr[firstDay].getDtText().substring(0, 10));
            formattedDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String unit = sh.getString("unit", "metric");
        String tempUnit = "°C";
        if (unit.equals("imperial")) {
            tempUnit = "°F";
        }

        return formattedDate + "\nmax: " + max + " " + tempUnit + "  min: " + min + " " + tempUnit;
    }

    private void fillListView2() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                listToMainActivity
        );
        list1.setAdapter(adapter);
    }

    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("No Internet connection")
                .setCancelable(false)
                .setPositiveButton("Settings", (dialog, id) -> {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.cancel();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
