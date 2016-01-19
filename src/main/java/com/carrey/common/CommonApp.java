package com.carrey.common;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 类描述：应用全局上下文
 * 创建人：carrey
 * 创建时间：2016/1/19 14:53
 */

public abstract class CommonApp extends Application {

    private static final String META_DEBUGGABLE = "ISDEBUG";///这个字段可以考虑在build。gradle里面配置
    private static CommonApp sApp;   //保证全局上下文唯一
    private boolean mIsStarted;

    public static CommonApp getApp() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        init();
    }

    private final void init() {
        if (shouldInit()) {
            Thread.setDefaultUncaughtExceptionHandler(new ExHandler(Thread.getDefaultUncaughtExceptionHandler()));
//            initMetaData();
            onInit();
        }
    }

    /**
     * 每次启动程序是需要调用
     * start
     */
    public final void start() {
        if (!mIsStarted) {
            mIsStarted = true;
            onStart();
        }
    }

    /**
     * 程序退出时候需要调用该方法，释放一些无用资源
     * stop
     */
    public final void stop() {
        if (mIsStarted) {
            mIsStarted = false;
            onStop();
        }
    }

    // 捕获程序崩溃的异常,记录log(可以考虑将异常信息发回服务器)
    private class ExHandler implements Thread.UncaughtExceptionHandler {
        private Thread.UncaughtExceptionHandler internal;

        private ExHandler(Thread.UncaughtExceptionHandler eh) {
            internal = eh;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            File file = new File(getLogPath());
            if (!file.exists()) {
                file.mkdirs();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String fname = sdf.format(new Date());
            try {
                PrintStream ps = new PrintStream(file.getAbsolutePath() + "/" + fname);
                ps.println(ex.getMessage());
                ex.printStackTrace(ps);
                ps.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            internal.uncaughtException(thread, ex);
        }
    }

    /**
     * 设置项目的初始log路径
     *
     * @return 路径
     */
    protected abstract String getLogPath();

    // 判断是否主进程启动App
    private boolean shouldInit() {
        android.app.ActivityManager am = ((android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

//    private void initMetaData() {
//        try {
//            ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
//            boolean isDebuggable = info.metaData.getBoolean(META_DEBUGGABLE, false);
//            BaseConfig.setDebuggable(isDebuggable);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    protected abstract void onStart();

    protected abstract void onStop();

    protected abstract void onInit();
}
