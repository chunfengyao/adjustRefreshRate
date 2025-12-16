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
import android.util.Log;
import android.widget.Toast;

public class Utils {
    public static void setRefresh(int refresh) {
        int minValue = SpUtils.getPowerSaveModeEnabled(true) ? 0 : refresh;
        setRefresh(minValue, refresh);
    }

    public static void setRefresh(int minValue, int userPreferedValue) {
        try {
            setParameter("content://settings/system","min_fresh_rate", minValue);
            setParameter("content://settings/system","peak_refresh_rate", userPreferedValue);
            setParameter("content://settings/secure","user_refresh_rate", userPreferedValue);
            setParameter("content://settings/secure","miui_refresh_rate", userPreferedValue);
            Toast.makeText(ContextProvider.getHoldContext(), "刷新率写入完成", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.e("刷新率应用失败",e.toString());
            Toast.makeText(ContextProvider.getHoldContext(), "刷新率使用失败\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static void setParameter(String uri, String key, int value) {
        ContentResolver contentResolver = ContextProvider.getHoldContext().getContentResolver();
        ContentValues contentValues = new ContentValues(2);
        contentValues.put("name", key);
        contentValues.put("value", String.valueOf(value));
        contentResolver.insert(Uri.parse(uri), contentValues);
    }
}
