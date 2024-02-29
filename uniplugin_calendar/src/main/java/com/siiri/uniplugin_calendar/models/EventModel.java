package com.siiri.uniplugin_calendar.models;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;

public class EventModel {
    private long eventId = -1L;
    private long calendarId;
    private String title;
    private String desc;
    private long startDate;
    private long endDate;
    private String reminders;
    private boolean allDay;
    private int availability;
    private int status;

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getReminders() {
        return reminders;
    }

    public void setReminders(String reminders) {
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public String toString() {
        return "EventModel{" +
                "eventId=" + eventId +
                ", calendarId=" + calendarId +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", reminders=" + reminders +
                ", allDay=" + allDay +
                ", availability=" + availability +
                ", status=" + status +
                '}';
    }

    // toJson
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("eventId", eventId);
        jsonObject.put("calendarId", calendarId);
        jsonObject.put("title", title);
        jsonObject.put("desc", desc);
        jsonObject.put("startDate", startDate);
        jsonObject.put("endDate", endDate);
        jsonObject.put("reminders", reminders);
        jsonObject.put("allDay", allDay);
        jsonObject.put("availability", availability);
        jsonObject.put("status", status);
        return jsonObject;
    }

}
