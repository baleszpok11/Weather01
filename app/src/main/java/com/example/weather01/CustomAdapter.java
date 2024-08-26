package com.example.weather01;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<DailyTemperature> {

    DetailsActivity detailsActivity = null;
    DailyTemperature[] dailyTemperatures = null;

    public CustomAdapter(DetailsActivity detailsActivity, DailyTemperature[] dailyTemperatures) {
        super(detailsActivity, R.layout.hours, dailyTemperatures);
        this.detailsActivity = detailsActivity;
        this.dailyTemperatures = dailyTemperatures;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = detailsActivity.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.hours, null, true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);


        titleText.setText(dailyTemperatures[i].getDtText().substring(11, 16) + "h");

        subtitleText.setText("max: " + dailyTemperatures[i].getMax() + "° min: " + dailyTemperatures[i].getMin() + "°");

        switch (dailyTemperatures[i].getMainDesc()) {
            case "Clouds":
                imageView.setImageResource(R.drawable.cloud);
                break;
            case "Rain":
                imageView.setImageResource(R.drawable.rain);
                break;
            default:
                imageView.setImageResource(R.drawable.sun);
        }
        return rowView;
    }

}
