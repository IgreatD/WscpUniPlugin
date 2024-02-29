package com.siiri.uniplugin_calendar.models;

public class CalendarIdModel {

    private long calendarId;

    public CalendarIdModel(long eventId) {
        this.calendarId = eventId;
    }


    public long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }
}
