package com.chatapp.messenger.Model.AnalyticsFragMent;

import android.graphics.drawable.Drawable;

public class Analytics {
    private int launches;
    private String appName;
    private Drawable image;
    private long sec;
    private int min;

    public Analytics(int launches, String appName, Drawable image, long sec) {

        this.launches = launches;
        this.appName = appName;
        this.image = image;
        if(sec>60){
            this.min=(int)sec/60;
            this.sec=sec%60;
        }
        else
        {
            this.sec=sec;
        }
    }

    public int getLaunches() {
        return launches;
    }

    public void setLaunches(int launches) {
        this.launches = launches;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public void setSec(long sec) {
        this.sec = sec;
    }


    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public long getSec() {
        return sec;
    }
}
