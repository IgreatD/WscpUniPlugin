package siirisoft.wscp.uniplugin_location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

public class LocationModule extends UniModule {

    @UniJSMethod(uiThread = true)
    public void getLocation(UniJSCallback callback) {
        Context context = mWXSDKInstance.getContext();
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            Location location;
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (location != null) {
                    callback.invoke(Result.success("获取成功", new LocationModel(location.getLongitude(), location.getLatitude())).toJson());
                } else {
                    callback.invoke(Result.error("获取失败").toJson());
                }
            } else {
                callback.invoke(Result.error("获取失败").toJson());
            }
        } else {
            callback.invoke(Result.error("没有定位权限", -2).toJson());
        }
    }

}
