package com.yaocf.utils.adjustRefreshRate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Choreographer;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    Choreographer.FrameCallback frameCallback = null;
    int userRefreshRate = SpUtils.getCustomValue(-1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView refreshRate = findViewById(R.id.refreshRate);
        TextView permissionGrandPrompt = findViewById(R.id.permissionGrandPrompt);
        MaterialButton miui60Hz = findViewById(R.id.miui60Hz);
        MaterialButton miui120Hz = findViewById(R.id.miui120Hz);
        MaterialButton miuiCustom90Hz = findViewById(R.id.miuiCustom90Hz);
        TextInputEditText customValueText = findViewById(R.id.miuiCustomValue);
        MaterialButton customValueCommit = findViewById(R.id.miuiCustomValueCommit);
        SwitchMaterial powerSaveModeSwitch = findViewById(R.id.powerSaveMode);
        LinearLayoutCompat userPreferedInput = findViewById(R.id.userPreferedInput);
        AppCompatSpinner userPreferedSpinner = findViewById(R.id.userPreferedSpinner);

        permissionGrandPrompt.setText(
                "adb shell pm grant "
                        + getPackageName()
                        + " android.permission.WRITE_SECURE_SETTINGS"
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            userPreferedSpinner.setVisibility(View.VISIBLE);
//        读取屏幕硬件支持的刷新率模式
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            Display.Mode[] supportedModes = display.getSupportedModes();
            ArrayList<Float> supportedRefreshRate = new ArrayList<>();
            for (int i = 0 ;i<supportedModes.length ;i++) {
                supportedRefreshRate.add(supportedModes[i].getRefreshRate());
            }

            //排序，从大到小
            Collections.sort(supportedRefreshRate, Comparator.reverseOrder());
            //写入到视图列表
            ArrayAdapter<Float> adapter = new ArrayAdapter<>(
                    this
                    , android.R.layout.simple_spinner_item
                    , supportedRefreshRate);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            userPreferedSpinner.setAdapter(adapter);
            userPreferedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    userRefreshRate = Math.round(supportedRefreshRate.get(position));
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //如果用户没选，就用默认值
                    userRefreshRate = Math.round(supportedRefreshRate.get(0));
                }
            });
        }else {
            userPreferedInput.setVisibility(View.VISIBLE);
        }

        powerSaveModeSwitch.setChecked(SpUtils.getPowerSaveModeEnabled(true));

        powerSaveModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpUtils.setPowerSaveModeEnabled(isChecked);
            }
        });

        customValueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                userRefreshRate = Integer.parseInt(s.toString());
            }
        });

        if (BuildConfig.DEBUG){
            try (Cursor cursor = getContentResolver().query(Uri.parse("content://settings/system"), null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    StringBuilder result = new StringBuilder();
                    while (cursor.moveToNext()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < cursor.getColumnCount(); i++) {
                                stringBuilder
                                        .append(cursor.getColumnName(i))
                                        .append(":")
                                        .append(cursor.getString(i))
                                        .append(", ");

                        }
                        result.append(stringBuilder).append("\n");
                    }
                    Log.w("系统设置配置", result.toString());
                }
            }
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
                    refreshRate.setText(Math.round((mFrameCount * 1000L) / diff * 100) / 100.00f + "FPS");
                    mFrameCount = 0;
                    mLastFrameTime = 0;
                } else {
                    ++mFrameCount;
                }
                Choreographer.getInstance().postFrameCallback(this);
            }
        };

        miui60Hz.setOnClickListener((View v) -> {
            Utils.setRefresh(60);
        });

        miui120Hz.setOnClickListener((View v) -> {
            Utils.setRefresh(120);
        });

        miuiCustom90Hz.setOnClickListener((View v) -> {
            Utils.setRefresh(90);
        });

        customValueCommit.setOnClickListener((View v) -> {
            //判非
            if(userRefreshRate < 0){
                Toast.makeText(this,"请输入一个合理值再试！", Toast.LENGTH_LONG).show();
                return;
            }
            //存
            SpUtils.setCustomValue(userRefreshRate);
            Utils.setRefresh(userRefreshRate);
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