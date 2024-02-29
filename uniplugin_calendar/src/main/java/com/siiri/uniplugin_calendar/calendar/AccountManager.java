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
import com.siiri.uniplugin_calendar.models.Result;
import com.siiri.uniplugin_calendar.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AccountManager {

    private AccountManager() {
    }

    private static final class InstanceHolder {
        // 单利模式
        static final AccountManager instance = new AccountManager();
    }

    public static AccountManager getInstance() {
        return InstanceHolder.instance;
    }

    public Long obtainCalendarAccountId(Context context) {
        if (!Utils.arePermissionsGranted(context)) {
            return -1L;
        }
        Result<CalendarModel> account = checkCalendarAccount(context);
        if (account.isSuccess()) {
            return account.getData().getId();
        } else {
            return createDefaultCalendarAccount(context).getData();
        }
    }

    /**
     * 检查是否存在日历账户
     *
     * @param context 上下文
     */
    public Result<CalendarModel> checkCalendarAccount(Context context) {
        if (!Utils.arePermissionsGranted(context)) {
            return Result.error("请先获取日历权限");
        }
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
        String[] selectionArgs = new String[]{Utils.getAppName(context), CalendarContract.ACCOUNT_TYPE_LOCAL};
        try {
            Cursor cursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, Constants.CALENDAR_PROJECTION,
                    selection, selectionArgs, null);
            if (cursor == null) {
                return Result.error("日历账户不存在");
            } else {
                long accountID;
                try {
                    long count = cursor.getCount();
                    if (count > 0 && cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(CalendarContract.Calendars._ID);
                        if (columnIndex != -1) {
                            accountID = cursor.getLong(columnIndex);
                            return retrieveCalendarAccount(context, accountID);
                        } else {
                            return Result.error("日历账户不存在");
                        }
                    } else {
                        return Result.error("日历账户不存在");
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("日历账户不存在");
        }
    }

    public Result<Long> createDefaultCalendarAccount(Context context) {
        if (Utils.arePermissionsGranted(context)) {
            String appName = Utils.getAppName(context);
            CalendarModel calendarModel = new CalendarModel(null, appName, appName, Constants.CALENDAR_ACCOUNT_TYPE, 0x00FF0000, appName);
            return createCalendarAccount(context, calendarModel);
        }
        return Result.error("请先获取日历权限");
    }

    public Result<Long> createCalendarAccount(Context context, CalendarModel calendarModel) {
        if (Utils.arePermissionsGranted(context)) {
            try {
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = CalendarContract.Calendars.CONTENT_URI;
                uri = uri.buildUpon()
                        .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                        .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, calendarModel.getAccountName())
                        .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, Constants.CALENDAR_ACCOUNT_TYPE)
                        .build();
                ContentValues contentValues = new ContentValues();
                contentValues.put(CalendarContract.Calendars.NAME, calendarModel.getName());
                contentValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, calendarModel.getName());
                contentValues.put(CalendarContract.Calendars.ACCOUNT_NAME, calendarModel.getAccountName());
                contentValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, Constants.CALENDAR_ACCOUNT_TYPE);
                contentValues.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
                contentValues.put(CalendarContract.Calendars.CALENDAR_COLOR, calendarModel.getCalendarColor());
                contentValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, calendarModel.getAccountName());
                contentValues.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, java.util.Calendar.getInstance().getTimeZone().getID());
                contentValues.put(CalendarContract.Calendars.SYNC_EVENTS, 0);
                Uri result = contentResolver.insert(uri, contentValues);
                long calendarId = Long.parseLong(result.getLastPathSegment());
                if (calendarId > 0) {
                    return Result.success("创建日历账户成功", calendarId);
                } else {
                    return Result.error("创建日历账户失败");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return Result.error("创建日历账户失败");
            }
        } else {
            return Result.error("请先获取日历权限");
        }
    }

    public Result<Integer> deleteCalendarAccount(Context context, Long calendarId) {
        if (calendarId == null || calendarId <= 0) {
            return Result.error("日历ID不能为空");
        }
        if (Utils.arePermissionsGranted(context)) {
            Result<CalendarModel> calendarAccount = retrieveCalendarAccount(context, calendarId);
            if (null == calendarAccount) {
                return Result.error("查询日历账户失败");
            }
            try {
                Uri uri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, calendarId);
                ContentResolver contentResolver = context.getContentResolver();
                int result = contentResolver.delete(uri, null, null);
                if (result > 0) {
                    return Result.success("删除日历成功", result);
                } else {
                    return Result.error("删除日历失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Result.error("删除日历失败");
            }
        } else {
            return Result.error("请先获取日历权限");
        }
    }

    public Result<List<CalendarModel>> retrieveCalendars(Context context) {
        if (Utils.arePermissionsGranted(context)) {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = CalendarContract.Calendars.CONTENT_URI;
            Cursor cursor = contentResolver.query(uri, Constants.CALENDAR_PROJECTION, null, null, null);
            if (null == cursor) {
                return Result.error("查询日历账户失败");
            }
            List<CalendarModel> calendarModels = new ArrayList<>();
            try {
                while (cursor.moveToNext()) {
                    CalendarModel calendarModel = parseCalendarRow(cursor);
                    if (null != calendarModel) {
                        calendarModels.add(calendarModel);
                    }
                }
                return Result.success("查询日历账户成功", calendarModels);
            } catch (Exception e) {
                return Result.error("查询日历账户失败, error: " + e.getMessage());
            } finally {
                cursor.close();
            }
        } else {
            return Result.error("请先获取日历权限");
        }
    }

    public Result<CalendarModel> retrieveCalendarAccount(Context context, Long calendarId) {
        if (calendarId == null || calendarId <= 0) {
            return Result.error("日历ID不能为空");
        }
        if (Utils.arePermissionsGranted(context)) {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = CalendarContract.Calendars.CONTENT_URI;
            String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                    + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
            String[] selectionArgs = new String[]{Utils.getAppName(context), CalendarContract.ACCOUNT_TYPE_LOCAL};
            Cursor cursor = contentResolver.query(ContentUris.withAppendedId(uri, calendarId), Constants.CALENDAR_PROJECTION, selection, selectionArgs, null);
            if (null == cursor) {
                return Result.error("查询日历账户失败");
            }
            if (cursor.moveToFirst()) {
                CalendarModel calendarModel = parseCalendarRow(cursor);
                return Result.success("查询日历账户成功", calendarModel);
            } else {
                return Result.error("查询日历账户失败");
            }
        } else {
            return Result.error("请先获取日历权限");
        }
    }

    private CalendarModel parseCalendarRow(Cursor cursor) {
        if (null == cursor) {
            return null;
        }
        long calendarId = cursor.getLong(Constants.CALENDAR_PROJECTION_ID_INDEX);
        String displayName = cursor.getString(Constants.CALENDAR_PROJECTION_DISPLAY_NAME_INDEX);
        int accessLevel = cursor.getInt(Constants.CALENDAR_PROJECTION_ACCESS_LEVEL_INDEX);
        int color = cursor.getInt(Constants.CALENDAR_PROJECTION_COLOR_INDEX);
        String accountName = cursor.getString(Constants.CALENDAR_PROJECTION_ACCOUNT_NAME_INDEX);
        String accountType = cursor.getString(Constants.CALENDAR_PROJECTION_ACCOUNT_TYPE_INDEX);
        String ownerAccount = cursor.getString(Constants.CALENDAR_PROJECTION_OWNER_ACCOUNT_INDEX);

        CalendarModel calendarModel = new CalendarModel(calendarId, displayName, accountName, accountType, color, ownerAccount);
        calendarModel.setReadOnly(isCalendarReadOnly(accessLevel));

        String isPrimary = cursor.getString(Constants.CALENDAR_PROJECTION_IS_PRIMARY_INDEX);

        calendarModel.setDefault("1".equals(isPrimary) || TextUtils.isEmpty(ownerAccount));

        return calendarModel;

    }

    private boolean isCalendarReadOnly(int accessLevel) {
        boolean result = accessLevel == CalendarContract.Events.CAL_ACCESS_CONTRIBUTOR
                || accessLevel == CalendarContract.Events.CAL_ACCESS_ROOT
                || accessLevel == CalendarContract.Events.CAL_ACCESS_OWNER
                || accessLevel == CalendarContract.Events.CAL_ACCESS_EDITOR;
        return !result;
    }

}
