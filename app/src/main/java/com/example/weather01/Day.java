package com.example.weather01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Day extends AppCompatActivity {

    DailyTemperature day;
    ImageView imageView;
    TextView temperatureTv;
    TextView humidity;
    TextView pressure;
    TextView wind;
    TextView clouds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imageView = (ImageView) findViewById(R.id.picture);
        temperatureTv = findViewById(R.id.temperature);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        wind = findViewById(R.id.wind);
        clouds = findViewById(R.id.clouds_cover);
        Intent intent = getIntent();
        day = (DailyTemperature) intent.getSerializableExtra("day");
        setData();
    }

    private void setData() {
        switch (day.getMainDesc()) {
            case "Clouds":
                imageView.setImageResource(R.drawable.cloud);
                break;
            case "Rain":
                imageView.setImageResource(R.drawable.rain);
                break;
            default:
                imageView.setImageResource(R.drawable.sun);
        }
        temperatureTv.setText(String.valueOf((int) (day.getMax() + day.getMin()) / 2) + "°");
        humidity.setText(String.valueOf(day.getHumidity()) + "%");
        pressure.setText(String.valueOf(day.getPressure()) + " mb");
        clouds.setText(String.valueOf(day.getCloudPercent()) + "%");



        // Szélsebesség mértékegység beállítása
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String unit = sharedPreferences.getString("unit", "metric"); // Default to "metric"
        Log.d("DayActivity", "Unit: " + unit);

        String speed;
        if ("imperial".equals(unit)) {
            // Convert to mph
            speed = String.format("%.2f mph", day.getWind_speed() * 2.23694); // m/s to mph
        } else {
            // Convert to km/h
            speed = String.format("%.2f km/h", day.getWind_speed() * 3.6); // m/s to km/h
        }


//        Szélírány + kép (északi irány)
        int deg = day.getDeg();
        int windImageResId = R.drawable.wind_direction;
        if (deg <= 23) {
            wind.setText("N " + speed);
        } else if (deg <= 68) {
            wind.setText("NE " + speed);
        } else if (deg <= 113) {
            wind.setText("E " + speed);
        } else if (deg <= 158) {
            wind.setText("SE " + speed);
        } else if (deg <= 203) {
            wind.setText("S " + speed);
        } else if (deg <= 248) {
            wind.setText("SW " + speed);
        } else if (deg <= 293) {
            wind.setText("W " + speed);
        } else if (deg <= 338) {
            wind.setText("NW " + speed);
        } else {
            wind.setText("N " + speed);
        }

//        Kép forgatás
        ImageView windImageView = findViewById(R.id.wind_deg);
        windImageView.setImageResource(windImageResId);
        windImageView.setRotation(deg);
    }

    public void onClick(View view) {
        Intent intent = new Intent(Day.this, MainActivity.class);
        startActivity(intent);
    }
}
