package siirisoft.wscp.uniplugin_bugly;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.tencent.bugly.crashreport.CrashReport;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

public class BuglyModule extends UniModule {

    @UniJSMethod(uiThread = true)
    public void init(JSONObject options, UniJSCallback callback) {
        if (options == null) {
            callback.invoke("options is null");
            return;
        }
        String appId = options.getString("appId");
        if (TextUtils.isEmpty(appId)) {
            callback.invoke("appId is null");
            return;
        }
        try {
            String userId = options.getString("userId");
            String deviceId = options.getString("deviceId");
            String deviceModel = options.getString("deviceModel");
            String appVersion = options.getString("appVersion");
            int tag = options.getIntValue("tag");
            boolean isDevelopmentDevice = options.getBooleanValue("isDevelopmentDevice");
            Context context = mUniSDKInstance.getContext();
            if (!TextUtils.isEmpty(deviceId)) {
                CrashReport.setDeviceId(context, deviceId);
            }
            if (!TextUtils.isEmpty(deviceModel)) {
                CrashReport.setDeviceModel(context, deviceModel);
            }
            if (!TextUtils.isEmpty(appVersion)) {
                CrashReport.setAppVersion(context, appVersion);
            }
            if (tag > 0) {
                CrashReport.setUserSceneTag(context, tag);
            }
            CrashReport.setIsDevelopmentDevice(context, isDevelopmentDevice);
            if (!TextUtils.isEmpty(userId)) {
                CrashReport.setUserId(userId);
            }
            CrashReport.initCrashReport(context, appId, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UniJSMethod(uiThread = true)
    public void reportError(JSONObject options) {
        if (options == null) {
            return;
        }
        String error = options.getString("error");
        if (TextUtils.isEmpty(error)) {
            return;
        }
        CrashReport.postCatchedException(new Throwable(error));
    }

}
