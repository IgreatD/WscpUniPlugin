package siirisoft.app.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import siirisoft.wscp.uniplugin_video_play.VideoPlayActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_video).setOnClickListener(v -> {
            Intent intent = new Intent(this, VideoPlayActivity.class);
            intent.putExtra("url", "https://eplus.cmec.com/wscp-file/im/6350a69a4cedfd0007bcbdb1/64dc6086c9e77c000800b525.mp4");
            startActivity(intent);
        });
    }
}