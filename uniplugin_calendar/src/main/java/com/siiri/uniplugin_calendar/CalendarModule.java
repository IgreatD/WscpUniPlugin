package com.siiri.uniplugin_calendar;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.siiri.uniplugin_calendar.calendar.AccountManager;
import com.siiri.uniplugin_calendar.calendar.EventManager;
import com.siiri.uniplugin_calendar.models.EventIdModel;
import com.siiri.uniplugin_calendar.models.EventModel;
import com.siiri.uniplugin_calendar.models.Result;
import com.siiri.uniplugin_calendar.utils.Utils;

import java.util.List;

import io.dcloud.common.core.permission.PermissionControler;
import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

public class CalendarModule extends UniModule {

    public static final String TAG = "calendarModule";

    public static int WRITE_CALENDAR_REQUEST_CODE = 1000;
    public static int INSERT_EVENT_REQUEST_CODE = 1001;
    public static int DELETE_EVENT_REQUEST_CODE = 1002;
    public static int DELETE_ALL_EVENT_REQUEST_CODE = 1005;
    public static int QUERY_ALL_EVENT_REQUEST_CODE = 1003;
    public static int QUERY_EVENT_REQUEST_CODE = 1004;
    String[] perms = {Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR};

    private UniJSCallback mPermissionCallback;
    private UniJSCallback mInsertEventCallback;
    private UniJSCallback mDeleteEventCallback;
    private UniJSCallback mDeleteAllEventCallback;
    private UniJSCallback mQueryAllEventCallback;
    private UniJSCallback mQueryEventCallback;
    private EventModel mEventModel;
    private EventIdModel eventIdMode;

    @UniJSMethod(uiThread = false)
    public void insertEvent(JSONObject options, UniJSCallback callback) {
        mInsertEventCallback = callback;
        mEventModel = parseEventArgs(options);
        if (mEventModel != null) {
            PermissionControler.requestPermissions((Activity) mWXSDKInstance.getContext(), perms, INSERT_EVENT_REQUEST_CODE);
        }
    }

    //  判断 write calendar 权限
    @UniJSMethod(uiThread = true)
    public void checkPermission(UniJSCallback callback) {
        boolean hasPermission = Utils.arePermissionsGranted(mWXSDKInstance.getContext());
        if (hasPermission) {
            if (null != callback) {
                callback.invoke(Result.success("已获取日历权限").toJson());
            }
        } else {
            mPermissionCallback = callback;
            PermissionControler.requestPermissions((Activity) mWXSDKInstance.getContext(), perms, WRITE_CALENDAR_REQUEST_CODE);
        }
    }

    @UniJSMethod(uiThread = false)
    public void queryEvent(JSONObject option, UniJSCallback callback) {
        eventIdMode = parseEventId(option);
        if (eventIdMode != null) {
            mQueryEventCallback = callback;
            PermissionControler.requestPermissions((Activity) mWXSDKInstance.getContext(), perms, QUERY_EVENT_REQUEST_CODE);
        } else {
            callback.invoke(Result.error("事件id不能为空").toJson());
        }
    }

    @UniJSMethod(uiThread = false)
    public void queryAllEvent(UniJSCallback callback) {
        mQueryAllEventCallback = callback;
        PermissionControler.requestPermissions((Activity) mWXSDKInstance.getContext(), perms, QUERY_ALL_EVENT_REQUEST_CODE);
    }

    @UniJSMethod(uiThread = false)
    public void clearEvents(UniJSCallback callback) {
        mDeleteAllEventCallback = callback;
        PermissionControler.requestPermissions((Activity) mWXSDKInstance.getContext(), perms, DELETE_ALL_EVENT_REQUEST_CODE);
    }

    @UniJSMethod(uiThread = false)
    public void deleteEvent(JSONObject option, UniJSCallback callback) {
        mDeleteEventCallback = callback;
        if (option == null) {
            callback.invoke(Result.error("事件id不能为空").toJson());
        } else {
            try {
                eventIdMode = parseEventId(option);
                if (eventIdMode != null) {
                    PermissionControler.requestPermissions((Activity) mWXSDKInstance.getContext(), perms, DELETE_EVENT_REQUEST_CODE);
                } else {
                    callback.invoke(Result.error("事件id不能为空").toJson());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                if (null != callback) {
                    callback.invoke(Result.error("事件id格式错误").toJson());
                }
            }
        }
    }

    @UniJSMethod(uiThread = true)
    public void log(String msg) {
        Log.d(TAG, msg);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        String preName = permissions[0];
        int granted = grantResults[0];
        boolean hasGranted = Manifest.permission.WRITE_CALENDAR.equals(preName) && granted == PackageManager.PERMISSION_GRANTED;
        if (hasGranted) {
            if (requestCode == INSERT_EVENT_REQUEST_CODE) {
                if (mInsertEventCallback != null && mEventModel != null) {
                    Result<Long> result = EventManager.getInstance().createOrUpdateEvent(mUniSDKInstance.getContext(), mEventModel);
                    if (result.isSuccess()) {
                        mInsertEventCallback.invoke(Result.success("事件插入成功", result.getData()).toJson());
                    } else {
                        mInsertEventCallback.invoke(Result.error("事件插入失败").toJson());
                    }
                }
            } else if (requestCode == WRITE_CALENDAR_REQUEST_CODE) {
                if (mPermissionCallback != null) {
                    mPermissionCallback.invoke(Result.success("已获取日历权限").toJson());
                }
            } else if (requestCode == DELETE_EVENT_REQUEST_CODE) {
                Result<String> deleteEvent = EventManager.getInstance().deleteEvent(mUniSDKInstance.getContext(), eventIdMode.getEventId());
                if (deleteEvent.isSuccess()) {
                    if (mDeleteEventCallback != null) {
                        mDeleteEventCallback.invoke(Result.success("事件删除成功").toJson());
                    }
                } else {
                    if (mDeleteEventCallback != null) {
                        mDeleteEventCallback.invoke(Result.error("事件删除失败").toJson());
                    }
                }
            } else if (requestCode == QUERY_ALL_EVENT_REQUEST_CODE) {
                Result<List<EventModel>> queryAllEvent = EventManager.getInstance().queryAllEvent(mUniSDKInstance.getContext());
                if (queryAllEvent.isSuccess()) {
                    if (mQueryAllEventCallback != null) {
                        mQueryAllEventCallback.invoke(Result.success("查询成功", queryAllEvent.getData()).toJson());
                    }
                } else {
                    if (mQueryAllEventCallback != null) {
                        mQueryAllEventCallback.invoke(Result.error("查询失败").toJson());
                    }
                }
            } else if (requestCode == QUERY_EVENT_REQUEST_CODE) {
                Result<EventModel> queryEvent = EventManager.getInstance().queryEvent(mUniSDKInstance.getContext(), eventIdMode.getEventId());
                if (queryEvent.isSuccess()) {
                    if (mQueryEventCallback != null) {
                        mQueryEventCallback.invoke(Result.success("查询成功", queryEvent.getData()).toJson());
                    }
                } else {
                    if (mQueryEventCallback != null) {
                        mQueryEventCallback.invoke(Result.error("查询失败").toJson());
                    }
                }
            } else if (requestCode == DELETE_ALL_EVENT_REQUEST_CODE) {
                Result<String> deleteAllEvent = EventManager.getInstance().deleteAllEvent(mUniSDKInstance.getContext());
                if (deleteAllEvent.isSuccess()) {
                    if (mDeleteAllEventCallback != null) {
                        mDeleteAllEventCallback.invoke(Result.success("删除成功").toJson());
                    }
                } else {
                    if (mDeleteAllEventCallback != null) {
                        mDeleteAllEventCallback.invoke(Result.error("删除失败").toJson());
                    }
                }
            }
            reset();
        } else {
            if (mInsertEventCallback != null) {
                mInsertEventCallback.invoke(Result.error("未获取日历权限，权限结果为" + granted + "，请到设置中手动开启").toJson());
            } else if (mPermissionCallback != null) {
                mPermissionCallback.invoke(Result.error("未获取日历权限，权限结果为" + granted + "，请到设置中手动开启").toJson());
            } else if (mDeleteEventCallback != null) {
                mDeleteEventCallback.invoke(Result.error("未获取日历权限，权限结果为" + granted + "，请到设置中手动开启").toJson());
            } else if (mQueryAllEventCallback != null) {
                mQueryAllEventCallback.invoke(Result.error("未获取日历权限，权限结果为" + granted + "，请到设置中手动开启").toJson());
            } else if (mQueryEventCallback != null) {
                mQueryEventCallback.invoke(Result.error("未获取日历权限，权限结果为" + granted + "，请到设置中手动开启").toJson());
            } else if (mDeleteAllEventCallback != null) {
                mDeleteAllEventCallback.invoke(Result.error("未获取日历权限，权限结果为" + granted + "，请到设置中手动开启").toJson());
            }
            reset();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private EventModel parseEventArgs(JSONObject jsonObject) {
        if (jsonObject == null) {
            if (mInsertEventCallback != null) {
                mInsertEventCallback.invoke(Result.error("参数不能为空").toJson());
            }
            return null;
        }
        EventModel event = new EventModel();
        String title = jsonObject.getString("title");
        // title 不能为空
        if (TextUtils.isEmpty(title)) {
            if (mInsertEventCallback != null) {
                mInsertEventCallback.invoke(Result.error("事件标题不能为空").toJson());
            }
            return null;
        }
        event.setTitle(title);
        String description = jsonObject.getString("desc");
        // description 不能为空
        if (TextUtils.isEmpty(description)) {
            if (mInsertEventCallback != null) {
                mInsertEventCallback.invoke(Result.error("事件描述不能为空").toJson());
            }
            return null;
        }
        event.setDesc(description);
        long startDate = jsonObject.getLongValue("startDate");
        // startDate 不能为空
        if (startDate == 0) {
            if (mInsertEventCallback != null) {
                mInsertEventCallback.invoke(Result.error("开始时间不能为空").toJson());
            }
            return null;
        }
        event.setStartDate(startDate);
        long endDate = jsonObject.getLongValue("endDate");
//        Integer rRule = jsonObject.getInteger("rRule");

        if (endDate == 0) {
//            endDate = startDate + 10分钟
            endDate = startDate + 10 * 60 * 1000;
        }
        event.setEndDate(endDate);
//        if (rRule != null) {
//            event.setrRule(RRuleConst.getRRuleByInt(rRule));
//        }
        String reminders = jsonObject.getString("reminders");
        if (!TextUtils.isEmpty(reminders)) {
            event.setReminders(reminders);
        }
        event.setCalendarId(AccountManager.getInstance().obtainCalendarAccountId(mUniSDKInstance.getContext()));
        return event;
    }

    private EventIdModel parseEventId(JSONObject jsonObject) {
        if (jsonObject == null) {
            if (mDeleteEventCallback != null) {
                mDeleteEventCallback.invoke(Result.error("参数不能为空").toJson());
            }
            return null;
        }
        EventIdModel eventIdModel = new EventIdModel();
        eventIdModel.setEventId(jsonObject.getLongValue("eventId"));
        return eventIdModel;
    }

    private void reset() {
        mInsertEventCallback = null;
        mEventModel = null;
        mDeleteEventCallback = null;
        eventIdMode = null;
        mQueryAllEventCallback = null;
        mQueryEventCallback = null;
        mPermissionCallback = null;
    }

}
