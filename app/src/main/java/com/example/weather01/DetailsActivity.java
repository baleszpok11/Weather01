package com.example.weather01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    DailyTemperature[] temperatures;
    ImageView imageView;
    TextView temperatureTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String timeOfDay = getIntent().getStringExtra("TIME_OF_DAY");

        View rootView = findViewById(R.id.main); // rootView a fő layout ID-ja.

        if (timeOfDay != null) {
            switch (timeOfDay) {
                case "sunrise":
                    rootView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_orange)); // Halvány narancssárga szín
                    break;
                case "sunset":
                    rootView.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow)); // Halvány kék szín
                    break;
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        temperatureTv = findViewById(R.id.temperature);
        /*TextView maxTv = findViewById(R.id.max);
        TextView minTv = findViewById(R.id.min);
        TextView pressureTv = findViewById(R.id.pressure);
        TextView humidityTv = findViewById(R.id.humidity);
        TextView descTv = findViewById(R.id.description);*/
        ListView lv = findViewById(R.id.list1);
        imageView = (ImageView) findViewById(R.id.picture);

        int days = intent.getIntExtra("hours",0);
        temperatures=new DailyTemperature[days];
        String day = "";
        for(int i =0;i<days;i++){
            day = "hour"+i;
            temperatures[i]= (DailyTemperature) intent.getSerializableExtra(day);
            if(i==0)
                setTitle();
        }
        CustomAdapter adapter = new CustomAdapter(this,temperatures);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> partent, View view, int i, long id){
                Intent intent = new Intent(DetailsActivity.this, Day.class);
                intent.putExtra("day",(Serializable) temperatures[i]);
                startActivity(intent);
            }
        });
    }

    private void setTitle() {
        switch (temperatures[0].getMainDesc()){
            case "Clouds":
                imageView.setImageResource(R.drawable.cloud);
                break;
            case "Rain":
                imageView.setImageResource(R.drawable.rain);
                break;
            default:
                imageView.setImageResource(R.drawable.sun);
        }

        //átlag hőmérséklet kiíratás
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.00", symbols);

        double averageTemperature = (temperatures[0].getMax() + temperatures[0].getMin()) / 2.0;

        temperatureTv.setText(df.format(averageTemperature) + "°");
    }

    public void onClick(View view) {
        Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
