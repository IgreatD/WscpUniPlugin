package com.siiri.uniplugin_calendar.common;

import android.provider.CalendarContract;

public class Constants {

    /*----------------------------------------- 日历账户常量 -----------------------------------------*/
    public static final String CALENDAR_ACCOUNT_TYPE = CalendarContract.ACCOUNT_TYPE_LOCAL; // 账户类型
    public static final int CALENDAR_PROJECTION_ID_INDEX = 0;
    public static final int CALENDAR_PROJECTION_ACCOUNT_NAME_INDEX = 1;
    public static final int CALENDAR_PROJECTION_ACCOUNT_TYPE_INDEX = 2;
    public static final int CALENDAR_PROJECTION_DISPLAY_NAME_INDEX = 3;
    public static final int CALENDAR_PROJECTION_OWNER_ACCOUNT_INDEX = 4;
    public static final int CALENDAR_PROJECTION_ACCESS_LEVEL_INDEX = 5;
    public static final int CALENDAR_PROJECTION_COLOR_INDEX = 6;
    public static final int CALENDAR_PROJECTION_IS_PRIMARY_INDEX = 7;

    public static String[] CALENDAR_PROJECTION = new String[]{
            CalendarContract.Calendars._ID, // 0
            CalendarContract.Calendars.ACCOUNT_NAME, // 1
            CalendarContract.Calendars.ACCOUNT_TYPE, // 2
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, // 3
            CalendarContract.Calendars.OWNER_ACCOUNT, // 4
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, // 5
            CalendarContract.Calendars.CALENDAR_COLOR, // 6
            CalendarContract.Calendars.IS_PRIMARY, // 7
    };
    /*----------------------------------------- 日历账户常量 -----------------------------------------*/

    public static final int EVENT_PROJECTION_ID_INDEX = 0;
    public static final int EVENT_PROJECTION_TITLE_INDEX = 1;
    public static final int EVENT_PROJECTION_DESCRIPTION_INDEX = 2;
    public static final int EVENT_PROJECTION_BEGIN_INDEX = 3;
    public static final int EVENT_PROJECTION_END_INDEX = 4;
    public static final int EVENT_PROJECTION_RECURRING_RULE_INDEX = 7;
    public static final int EVENT_PROJECTION_ALL_DAY_INDEX = 8;
    public static final int EVENT_PROJECTION_EVENT_LOCATION_INDEX = 9;
    public static final int EVENT_PROJECTION_CUSTOM_APP_URI_INDEX = 10;
    public static final int EVENT_PROJECTION_START_TIMEZONE_INDEX = 11;
    public static final int EVENT_PROJECTION_END_TIMEZONE_INDEX = 12;
    public static final int EVENT_PROJECTION_AVAILABILITY_INDEX = 13;
    public static final int EVENT_PROJECTION_STATUS_INDEX = 14;

    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.DURATION,
            CalendarContract.Events.RDATE,
            CalendarContract.Events.RRULE,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.CUSTOM_APP_URI,
            CalendarContract.Events.EVENT_TIMEZONE,
            CalendarContract.Events.EVENT_END_TIMEZONE,
            CalendarContract.Events.AVAILABILITY,
            CalendarContract.Events.STATUS
    };

    public static final int REMINDER_MINUTES_INDEX = 1;

    public static final String[] REMINDER_PROJECTION = new String[]{
            CalendarContract.Reminders.EVENT_ID,
            CalendarContract.Reminders.MINUTES
    };

}
