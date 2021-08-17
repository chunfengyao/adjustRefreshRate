package com.yaocf.utils.adjustRefreshRate;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * <pre>
 * Used to
 * <pre/>
 * created by:   yaochunfeng
 * on:           2021/8/17 12:34 下午
 * Email:        yaocf@189.cn
 */
public class ContextProvider extends ContentProvider {
    private static ContextProvider INSTANCE = null;

    @Override
    public boolean onCreate() {
        INSTANCE = this;
        //TODO 注册AppManager

        return false;
    }

    /**
     * 获取实例
     * @return 不为空，因为common库无法在启动之前执行代码所以，
     * 这边默认认为返回值不可能为空，但是，请不要在attachBaseContext里面使用！！！
     */
    public @NonNull  static ContextProvider getInstance() {
        return INSTANCE;
    }

    public @NonNull static Context getHoldContext() {
        return Objects.requireNonNull(INSTANCE.getContext());
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

