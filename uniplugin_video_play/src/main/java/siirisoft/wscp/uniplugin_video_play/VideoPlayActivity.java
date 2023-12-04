package siirisoft.wscp.uniplugin_video_play;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

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
        CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
        videoPlayer = findViewById(R.id.video_player);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        String thumb = intent.getStringExtra("thumb");
        if (!TextUtils.isEmpty(thumb)) {
            setThumb(thumb);
        } else {
//            loadCover(url);
        }

        orientationUtils = new OrientationUtils(this, videoPlayer);
        videoPlayer.getBackButton().setImageResource(R.drawable.ic_baseline_close_24);
        videoPlayer.setEnlargeImageRes(R.drawable.ic_baseline_fullscreen_24);
        videoPlayer.setShrinkImageRes(R.drawable.ic_baseline_fullscreen_exit_24);
        if (TextUtils.isEmpty(title)) {
            videoPlayer.setUp(url, true, "");
        } else {
            videoPlayer.setUp(url, true, title);
        }
        videoPlayer.setFullHideStatusBar(true);
        videoPlayer.setFullHideActionBar(true);
        videoPlayer.getFullscreenButton().setOnClickListener(v -> orientationUtils.resolveByClick());
        videoPlayer.setIsTouchWiget(true);
        videoPlayer.setIsTouchWigetFull(true);
        videoPlayer.setNeedOrientationUtils(false);
        videoPlayer.getBackButton().setOnClickListener(v -> onBackPressed());

        videoPlayer.startPlayLogic();
    }

    private void loadCover(String url) {
//        ImageView imageView = new ImageView(this);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        Glide.with(this)
//                .setDefaultRequestOptions(
//                        new RequestOptions()
//                                .frame(0)
//                                .centerCrop()
//                )
//                .load(url).into(imageView);
//        videoPlayer.setThumbImageView(imageView);
//        videoPlayer.setThumbPlay(true);
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
