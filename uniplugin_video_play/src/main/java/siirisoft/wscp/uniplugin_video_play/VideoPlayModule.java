package siirisoft.wscp.uniplugin_video_play;

import android.content.Intent;

import com.alibaba.fastjson.JSONObject;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

public class VideoPlayModule extends UniModule {

    @UniJSMethod(uiThread = true)
    public void play(JSONObject options, UniJSCallback callback) {
        String url = options.getString("url");
        if (url == null || url.isEmpty()) {
            callback.invoke("url is empty");
            return;
        }
        String title = options.getString("title");
        String thumb = options.getString("thumb");
        Intent intent = new Intent(mUniSDKInstance.getContext(), VideoPlayActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("thumb", thumb);
        mUniSDKInstance.getContext().startActivity(intent);
    }

}
