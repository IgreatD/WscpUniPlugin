package siirisoft.wscp.uniplugin_video_play;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

public class VideoPlayActivity extends Activity {

    private StandardGSYVideoPlayer videoPlayer;
    private OrientationUtils orientationUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        init();
    }

    private void init() {
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        videoPlayer = findViewById(R.id.video_player);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        String thumb = intent.getStringExtra("thumb");
        if (!TextUtils.isEmpty(thumb)) {
            setThumb(thumb);
        }
        if (TextUtils.isEmpty(title)) {
            videoPlayer.setUp(url, true, "视频播放");
        } else {
            videoPlayer.setUp(url, true, title);
        }
        orientationUtils = new OrientationUtils(this, videoPlayer);
        videoPlayer.setFullHideStatusBar(true);
        videoPlayer.setFullHideActionBar(true);
        videoPlayer.getFullscreenButton().setOnClickListener(v -> orientationUtils.resolveByClick());
        videoPlayer.setIsTouchWiget(true);
        videoPlayer.setIsTouchWigetFull(true);
        videoPlayer.setNeedOrientationUtils(false);
        videoPlayer.getBackButton().setOnClickListener(v -> onBackPressed());

        videoPlayer.startPlayLogic();
    }

    // glide加载图片
    private void setThumb(String thumb) {
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(thumb).into(imageView);
        videoPlayer.setThumbImageView(imageView);
        videoPlayer.setThumbPlay(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @Override
    public void onBackPressed() {
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.getFullscreenButton().performClick();
            return;
        }
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }
}
