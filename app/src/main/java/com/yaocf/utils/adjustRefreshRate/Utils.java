package com.yaocf.utils.adjustRefreshRate;
/* * *
 * created by:   yaochunfeng
 * on:           2021/2/18 5:09 下午
 * Des:          Used to .
 * Email:        yaochunfeng@wondersgroup.com
 *
 * * */

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

public class Utils {
    public static void setRefresh(Context context, String refresh, String name) {
        String minValue = SpUtils.getPowerSaveModeEnabled(true) ? "60" : refresh;
        setRefresh(context, minValue, refresh, refresh, name);
    }

    public static void setRefresh(Context context, String min_refresh, String peak_refresh, String user_refresh, String name) {
        try {
            setParameter(context, "min_fresh_rate", min_refresh, name);
            setParameter(context, "peak_refresh_rate", peak_refresh, name);
            setParameter(context, "user_refresh_rate", user_refresh, name);
            Toast.makeText(context, "应用" + name + "成功", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context, "应用失败", Toast.LENGTH_SHORT).show();
        }
    }

    public static void setParameter(Context context, String key, String value, String name) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues(2);
        contentValues.put("name", key);
        contentValues.put("value", value);
        contentResolver.insert(Uri.parse("content://settings/system"), contentValues);
    }
}
