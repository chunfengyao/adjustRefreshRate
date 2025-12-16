# 调节刷新率（支持MIUI，Hyper OS）（无需root）
#### 说明：
- 修改刷新率需要两个权限，所以使用前请确认权限授予了：
  - `android.permission.WRITE_SECURE_SETTINGS`（使用adb进行授权）
  - `android.permission.WRITE_SETTINGS`（在权限管理页面允许，或者使用AppOps之类的工具）
#### 使用adb授权命令如下
```shell
adb shell pm grant ${当前应用的应用包名} android.permission.WRITE_SECURE_SETTINGS
```
#### 安卓6.0及以上的设备会显示屏幕硬件支持的刷新率可以直接从下拉列表中选



