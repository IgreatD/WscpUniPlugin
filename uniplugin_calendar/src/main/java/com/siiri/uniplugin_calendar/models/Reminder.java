package com.siiri.uniplugin_calendar.models;

public class Reminder {
    private int minutes;

    public Reminder(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

}
