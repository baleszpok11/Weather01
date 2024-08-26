package com.example.weather01;

import java.io.Serializable;
public class DailyTemperature implements Serializable{
    long dateTime;
    double min;
    double max;
    int pressure;
    int humidity;
    String mainDesc;
    String dtText;
    double wind_speed;
    int deg;
    int cloudPercent;

    public DailyTemperature(long dateTime, double min, double max, int pressure, int humidity, String mainDesc, String dtText, double wind_speed, int deg, int cloudPercent) {
        this.dateTime = dateTime;
        this.min = min;
        this.max = max;
        this.pressure = pressure;
        this.humidity = humidity;
        this.mainDesc = mainDesc;
        this.dtText = dtText;
        this.wind_speed = wind_speed;
        this.deg = deg;
        this.cloudPercent = cloudPercent;
    }

    public long getDateTime() {
        return dateTime;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getMainDesc() {
        return mainDesc;
    }
    public String getDtText(){return dtText;}

    public double getWind_speed() {
        return wind_speed;
    }

    public int getDeg() {
        return deg;
    }

    public int getCloudPercent() {
        return cloudPercent;
    }

    @Override
    public String toString() {
        return  dtText + "\n" + min + "/" + max +
                "\n"  + mainDesc ;
    }
}

