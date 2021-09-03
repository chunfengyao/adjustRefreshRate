package com.yaocf.utils.adjustRefreshRate;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * <pre>
 * Used to
 * <pre/>
 * created by:   yaochunfeng
 * on:           2021/8/17 12:28 下午
 * Email:        yaochunfeng@wondersgroup.com
 */
public class SpUtils {
    private static final SharedPreferences sharedPreferences = ContextProvider.getHoldContext().getSharedPreferences("adjustRate", Context.MODE_PRIVATE);
    private static final SharedPreferences.Editor editor = sharedPreferences.edit();
    private final static String CUSTOM_VALUE_KEY = "customValue";
    private final static String POWER_SAVE_MODE_KEY = "PowerSaveMode";

    public static String getCustomValue(String defaultValue){
        return sharedPreferences.getString(CUSTOM_VALUE_KEY, defaultValue);
    }

    public static void setCustomValue(String customValue){
        editor.putString(CUSTOM_VALUE_KEY, customValue)
                .apply();
    }

    public static boolean getPowerSaveModeEnabled(boolean defaultValue){
        return sharedPreferences.getBoolean(POWER_SAVE_MODE_KEY, defaultValue);
    }

    public static void setPowerSaveModeEnabled(boolean customValue){
        editor.putBoolean(POWER_SAVE_MODE_KEY, customValue)
                .apply();
    }
}
