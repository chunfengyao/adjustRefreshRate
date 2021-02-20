package com.yaocf.utils.adjustRefreshRate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    Choreographer.FrameCallback frameCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView refreshRate = findViewById(R.id.refreshRate);
        MaterialButton miui60Hz = findViewById(R.id.miui60Hz);
        MaterialButton miui120Hz = findViewById(R.id.miui120Hz);
        MaterialButton miuiCustom90Hz = findViewById(R.id.miuiCustom90Hz);

        Cursor cursor = getContentResolver().query(Uri.parse("content://settings/system"), null, null, null, null);

        if (BuildConfig.DEBUG && cursor != null && cursor.moveToFirst()) {
            String result = "";
            while (cursor.moveToNext()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < cursor.getColumnCount(); i++){
                        stringBuilder
                                .append(cursor.getColumnName(i))
                                .append(":")
                                .append(cursor.getString(i))
                                .append(", ");

                }
                result += stringBuilder.toString()+ "\n";
            }
            Log.d("系统设置配置", result);
        }

        frameCallback = new Choreographer.FrameCallback() {

            long mLastFrameTime = -1;
            int mFrameCount = 0;
            @Override
            public void doFrame(long frameTimeNanos) {
                if (mLastFrameTime == 0) {
                    mLastFrameTime = frameTimeNanos;
                }
                float diff = (frameTimeNanos - mLastFrameTime) / 1000000.0f;//得到毫秒，正常是 16.66 ms
                if (diff > 500) {
                    refreshRate.setText(String.valueOf(Math.round((mFrameCount * 1000L) / diff * 100) / 100.00f) + "FPS");
                    mFrameCount = 0;
                    mLastFrameTime = 0;
                } else {
                    ++mFrameCount;
                }
                Choreographer.getInstance().postFrameCallback(this);
            }
        };

        miui60Hz.setOnClickListener((View v) -> {
            Utils.setRefresh(this.getApplicationContext(), "60", "MIUI的60Hz");
        });

        miui120Hz.setOnClickListener((View v) -> {
            Utils.setRefresh(this.getApplicationContext(), "120", "MIUI的120Hz");
        });

        miuiCustom90Hz.setOnClickListener((View v) -> {
            Utils.setRefresh(this.getApplicationContext(), "96", "拓展MIUI的90Hz");
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        //取消监听帧率
        Choreographer.getInstance().removeFrameCallback(frameCallback);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onResume() {
        super.onResume();
        boolean canDo =  Settings.System.canWrite(this.getApplicationContext());
        if (!canDo) {
            Intent grantIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(grantIntent);
        }
        //继续监听帧率
        Choreographer.getInstance().postFrameCallback(frameCallback);
    }
}