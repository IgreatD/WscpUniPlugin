package com.siiri.uniplugin_calendar.calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;

import com.siiri.uniplugin_calendar.common.Constants;
import com.siiri.uniplugin_calendar.models.CalendarModel;
import com.siiri.uniplugin_calendar.models.EventModel;
import com.siiri.uniplugin_calendar.models.Reminder;
import com.siiri.uniplugin_calendar.models.Result;
import com.siiri.uniplugin_calendar.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class EventManager {

    private final StringBuilder builder = new StringBuilder();

    private EventManager() {
    }

    private static final class InstanceHolder {
        // 单利模式
        static final EventManager instance = new EventManager();
    }

    public static EventManager getInstance() {
        return EventManager.InstanceHolder.instance;
    }

    public Result<Long> createOrUpdateEvent(Context context, EventModel event) {
        if (Utils.arePermissionsGranted(context)) {
            long calendarId = AccountManager.getInstance().obtainCalendarAccountId(context);
            if (calendarId != -1) {
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues values = buildEventContentValues(calendarId, event);
                if (event.getEventId() == -1) {
                    try {
                        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
                        if (uri == null) {
                            return Result.error("创建事件失败");
                        } else {
                            long eventId = Long.parseLong(uri.getLastPathSegment());
                            event.setEventId(eventId);
                            String reminderStr = event.getReminders();
                            List<Reminder> reminders = new ArrayList<>();
                            if (!TextUtils.isEmpty(reminderStr)) {
                                String[] split = reminderStr.split(",");
                                for (String s : split) {
                                    if (TextUtils.isEmpty(s)) {
                                        continue;
                                    }
                                    Reminder reminder = new Reminder(Integer.parseInt(s));
                                    reminders.add(reminder);
                                }
                            }

                            insertReminders(contentResolver, eventId, reminders);
                            return Result.success("创建事件成功", eventId);
                        }
                    } catch (NumberFormatException e) {
                        return Result.error("创建事件失败");
                    }
                } else {
                    Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getEventId());
                    int rows = contentResolver.update(uri, values, null, null);
                    if (rows == 0) {
                        return Result.error("更新事件失败");
                    } else {
                        return Result.success("更新事件成功", event.getEventId());
                    }
                }
            } else {
                return Result.error("日历账户不存在");
            }
        } else {
            return Result.error("没有权限");
        }

    }

    private void insertReminders(ContentResolver contentResolver, long eventId, List<Reminder> reminders) {
        if (reminders != null) {
            ArrayList<ContentValues> contentValues = new ArrayList<>();
            for (Reminder reminder : reminders) {
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Reminders.EVENT_ID, eventId);
                values.put(CalendarContract.Reminders.MINUTES, reminder.getMinutes());
                values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                contentValues.add(values);
            }
            try {
                contentResolver.bulkInsert(CalendarContract.Reminders.CONTENT_URI, contentValues.toArray(new ContentValues[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Result<String> deleteAllEvent(Context context) {
        if (Utils.arePermissionsGranted(context)) {
            Result<CalendarModel> account = AccountManager.getInstance().checkCalendarAccount(context);
            if (account.isSuccess()) {
                CalendarModel calendarModel = account.getData();
                if (calendarModel != null) {
                    if (calendarModel.isReadOnly()) {
                        return Result.error("日历账户只读");
                    }
                    ContentResolver contentResolver = context.getContentResolver();
                    Uri uri = CalendarContract.Events.CONTENT_URI;
                    String selection = CalendarContract.Events.CALENDAR_ID + " = ?";
                    String[] selectionArgs = new String[]{String.valueOf(account.getData().getId())};
                    try {
                        int delete = contentResolver.delete(uri, selection, selectionArgs);
                        if (delete == 0) {
                            return Result.error("删除事件失败");
                        } else {
                            return Result.success("删除事件成功");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Result.error("删除事件失败");
                    }
                } else {
                    return Result.error("日历账户不存在");
                }
            } else {
                return Result.error("日历账户不存在");
            }
        } else {
            return Result.error("没有权限");
        }
    }

    public Result<String> deleteEvent(Context context, long eventId) {
        if (Utils.arePermissionsGranted(context)) {
            Long accountId = AccountManager.getInstance().obtainCalendarAccountId(context);
            Result<CalendarModel> account = AccountManager.getInstance().retrieveCalendarAccount(context, accountId);
            if (account.isSuccess()) {
                CalendarModel calendarModel = account.getData();
                if (calendarModel != null) {
                    if (calendarModel.isReadOnly()) {
                        return Result.error("日历账户只读");
                    }
                    try {
                        ContentResolver contentResolver = context.getContentResolver();
                        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
                        int delete = contentResolver.delete(uri, null, null);
                        if (delete == 0) {
                            return Result.error("删除事件失败");
                        } else {
                            return Result.success("删除事件成功");
                        }
                    } catch (Exception e) {
                        return Result.error("删除事件失败");
                    }
                } else {
                    return Result.error("日历账户不存在");
                }
            } else {
                return Result.error("日历账户不存在");
            }
        } else {
            return Result.error("没有权限");
        }
    }

    public Result<EventModel> queryEvent(Context context, long eventId) {
        if (Utils.arePermissionsGranted(context)) {
            Result<CalendarModel> account = AccountManager.getInstance().checkCalendarAccount(context);
            if (account.isSuccess()) {
                CalendarModel calendarModel = account.getData();
                if (calendarModel != null) {
                    Long calendarId = calendarModel.getId();

                    ContentResolver contentResolver = context.getContentResolver();
                    Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
                    ContentUris.appendId(builder, new Date(0).getTime());
                    ContentUris.appendId(builder, new Date(Long.MAX_VALUE).getTime());

                    Uri eventsUri = builder.build();

                    String eventsCalendarQuery = "(" + CalendarContract.Events.CALENDAR_ID + " = " + calendarId + ")";
                    String eventsNotDeletedQuery = "(" + CalendarContract.Events.DELETED + " != 1)";

                    String eventsSelectionQuery = eventsCalendarQuery + " AND " + eventsNotDeletedQuery + " AND " + CalendarContract.Instances.EVENT_ID + " = " + eventId;

                    try {
                        Cursor cursor = contentResolver.query(eventsUri, Constants.EVENT_PROJECTION, eventsSelectionQuery, null, null);

                        if (null == cursor) {
                            return Result.error("查询事件失败");
                        }
                        if (cursor.moveToFirst()) {
                            EventModel eventModel = parseEvent(cursor, calendarId);
                            List<Reminder> reminders = parseReminders(eventModel.getEventId(), contentResolver);
                            if (reminders != null && reminders.size() > 0) {
                                // reminders 获取 minutes，拼接到字符串里，并且最后一个不加逗号
                                StringBuilder builder1 = new StringBuilder();
                                for (int i = 0; i < reminders.size(); i++) {
                                    Reminder reminder = reminders.get(i);
                                    builder1.append(reminder.getMinutes());
                                    if (i != reminders.size() - 1) {
                                        builder1.append(",");
                                    }
                                }
                                eventModel.setReminders(builder1.toString());
                            }
                            cursor.close();
                            return Result.success("查询事件成功", eventModel);
                        } else {
                            cursor.close();
                            return Result.error("查询事件失败");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Result.error("查询事件失败");
                    }

                } else {
                    return Result.error("日历账户不存在");
                }
            } else {
                return Result.error("日历账户不存在");
            }
        } else {
            return Result.error("没有权限");
        }
    }

    /**
     * 查询所有的事件
     *
     * @return 事件列表
     */
    public Result<List<EventModel>> queryAllEvent(Context context) {
        if (Utils.arePermissionsGranted(context)) {
            Result<CalendarModel> account = AccountManager.getInstance().checkCalendarAccount(context);
            if (account.isSuccess()) {
                CalendarModel calendarModel = account.getData();
                if (calendarModel != null) {
                    Long calendarId = calendarModel.getId();

                    ContentResolver contentResolver = context.getContentResolver();
                    Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
                    ContentUris.appendId(builder, new Date(0).getTime());
                    ContentUris.appendId(builder, new Date(Long.MAX_VALUE).getTime());

                    Uri eventsUri = builder.build();

                    String eventsCalendarQuery = "(" + CalendarContract.Events.CALENDAR_ID + " = " + calendarId + ")";
                    String eventsNotDeletedQuery = "(" + CalendarContract.Events.DELETED + " != 1)";

                    String eventsSelectionQuery = eventsCalendarQuery + " AND " + eventsNotDeletedQuery;

                    String sortOrder = CalendarContract.Events.DTSTART + " DESC";
                    try {
                        Cursor query = contentResolver.query(eventsUri, Constants.EVENT_PROJECTION, eventsSelectionQuery, null, sortOrder);

                        if (null == query) {
                            return Result.error("查询事件失败");
                        }

                        ArrayList<EventModel> events = new ArrayList<>();
                        while (query.moveToNext()) {
                            EventModel eventModel = parseEvent(query, calendarId);
                            if (eventModel != null) {
                                events.add(eventModel);
                            }
                        }
                        for (EventModel event : events) {
                            List<Reminder> reminders = parseReminders(event.getEventId(), contentResolver);
                            if (reminders != null && reminders.size() > 0) {
                                // reminders 获取 minutes，拼接到字符串里，并且最后一个不加逗号
                                StringBuilder builder1 = new StringBuilder();
                                for (int i = 0; i < reminders.size(); i++) {
                                    Reminder reminder = reminders.get(i);
                                    builder1.append(reminder.getMinutes());
                                    if (i != reminders.size() - 1) {
                                        builder1.append(",");
                                    }
                                }
                                event.setReminders(builder1.toString());
                            }
                        }
                        query.close();
                        return Result.success("查询事件成功", events);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Result.error("查询事件失败");
                    }

                } else {
                    return Result.error("日历账户不存在");
                }
            } else {
                return Result.error("日历账户不存在");
            }
        } else {
            return Result.error("没有权限");
        }
    }

    private EventModel parseEvent(Cursor cursor, Long calendarId) {
        if (null == cursor) {
            return null;
        }
        try {
            long eventId = cursor.getLong(Constants.EVENT_PROJECTION_ID_INDEX);
            String title = cursor.getString(Constants.EVENT_PROJECTION_TITLE_INDEX);
            String description = cursor.getString(Constants.EVENT_PROJECTION_DESCRIPTION_INDEX);
            int availability = cursor.getInt(Constants.EVENT_PROJECTION_AVAILABILITY_INDEX);
            int status = cursor.getInt(Constants.EVENT_PROJECTION_STATUS_INDEX);
            int allDay = cursor.getInt(Constants.EVENT_PROJECTION_ALL_DAY_INDEX);
            long dtStart = cursor.getLong(Constants.EVENT_PROJECTION_BEGIN_INDEX);
            long dtEnd = cursor.getLong(Constants.EVENT_PROJECTION_END_INDEX);

            EventModel event = new EventModel();

            event.setEventId(eventId);
            event.setCalendarId(calendarId);
            event.setTitle(title);
            event.setDesc(description);
            event.setAvailability(getAvailability(availability));
            event.setStatus(getStatus(status));
            event.setAllDay(allDay == 1);
            event.setStartDate(dtStart);
            event.setEndDate(dtEnd);
            return event;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Reminder> parseReminders(long eventId, ContentResolver resolver) {
        ArrayList<Reminder> reminders = new ArrayList<>();
        String remindersQuery = CalendarContract.Reminders.EVENT_ID + " = " + eventId;
        try {
            Cursor cursor = resolver.query(CalendarContract.Reminders.CONTENT_URI, Constants.REMINDER_PROJECTION, remindersQuery, null, null);

            if (null == cursor) {
                return reminders;
            }
            while (cursor.moveToNext()) {
                int min = cursor.getInt(Constants.REMINDER_MINUTES_INDEX);
                Reminder reminder = new Reminder(min);
                reminders.add(reminder);
            }
            cursor.close();
            return reminders;
        } catch (Exception e) {
            e.printStackTrace();
            return reminders;
        }
    }

    private ContentValues buildEventContentValues(long calendarId, EventModel eventModel) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        values.put(CalendarContract.Events.ALL_DAY, eventModel.isAllDay() ? 1 : 0);
        values.put(CalendarContract.Events.DTSTART, eventModel.getStartDate());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put(CalendarContract.Events.TITLE, eventModel.getTitle());
        values.put(CalendarContract.Events.DESCRIPTION, eventModel.getDesc());
        values.put(CalendarContract.Events.AVAILABILITY, getAvailability(eventModel.getAvailability()));
        values.put(CalendarContract.Events.HAS_ALARM, 1);
        int status = getStatus(eventModel.getStatus());
        if (status != -1) {
            values.put(CalendarContract.Events.STATUS, status);
        }
        values.put(CalendarContract.Events.EVENT_END_TIMEZONE, TimeZone.getDefault().getID());
        values.put(CalendarContract.Events.DTEND, eventModel.getEndDate());
//        if (!TextUtils.isEmpty(eventModel.getrRule())) {
//            String ruleParams = buildRuleParams(eventModel.getrRule(), eventModel.getStartDate(), eventModel.getEndDate());
//            values.put(CalendarContract.Events.RRULE, ruleParams);
//        }
        return values;
    }

    /*----------------------------------------------- ------------------------------------------------------*/
    private int getAvailability(int availability) {
        switch (availability) {
            case 1:
                return CalendarContract.Events.AVAILABILITY_FREE;
            case 2:
                return CalendarContract.Events.AVAILABILITY_TENTATIVE;
            case 0:
            default:
                return CalendarContract.Events.AVAILABILITY_BUSY;
        }
    }

    private int getStatus(int status) {
        switch (status) {
            case 1:
                return CalendarContract.Events.STATUS_CONFIRMED;
            case 2:
                return CalendarContract.Events.STATUS_CANCELED;
            case 0:
                return CalendarContract.Events.STATUS_TENTATIVE;
            default:
                return -1;
        }
    }

}
