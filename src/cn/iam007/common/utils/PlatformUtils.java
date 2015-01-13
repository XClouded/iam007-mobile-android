package cn.iam007.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;
import cn.iam007.IAM007Application;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class PlatformUtils {
    private static int mScreenWidth = 0;
    private static int mScreenHeight = 0;
    private static float mDensity = 0;

    public static float getDensity(Context context) {
        if (mDensity > 0) {
            return mDensity;
        }

        if (context == null) {
            context = IAM007Application.getCurrentApplication();
        }

        DisplayMetrics dm = new DisplayMetrics();
        // 取得窗口属性
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(dm);

        // 窗口的宽度
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        mDensity = dm.density;

        return mDensity;
    }

    /**
     * 获取屏幕宽度，单位像素
     * 
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        if (mScreenWidth > 0) {
            return mScreenWidth;
        }

        if (context == null) {
            context = IAM007Application.getCurrentApplication();
        }

        DisplayMetrics dm = new DisplayMetrics();
        // 取得窗口属性
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(dm);

        // 窗口的宽度
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        mDensity = dm.density;

        return mScreenWidth;
    }

    /**
     * 获取屏幕宽度，单位像素
     * 
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (mScreenHeight > 0) {
            return mScreenHeight;
        }

        if (context == null) {
            context = IAM007Application.getCurrentApplication();
        }

        DisplayMetrics dm = new DisplayMetrics();
        // 取得窗口属性
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(dm);

        // 窗口的宽度
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        mDensity = dm.density;

        return mScreenHeight;
    }

    /**
     * 获取当前应用的version code
     * 
     * @param context
     * @return
     *         返回应用的version code, 如果为-1, 表示获取时发生异常
     */
    public static int getVersionCode(Context context) {
        //获取版本号(内部识别号)
        try {
            PackageInfo pi = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (NameNotFoundException e) {
            return -1;
        }
    }

    /**
     * 通过key值获取<mete-data /> 标签中的值
     * 
     * @param mContext
     *            运行上下文
     * @param key
     *            对应的key 值
     * @return data key对应的value 值
     * */
    public static String getMeteDataByKey(Context mContext, String key) {
        try {
            ApplicationInfo appInfo = mContext
                    .getApplicationContext()
                    .getPackageManager()
                    .getApplicationInfo(mContext.getPackageName(),
                            PackageManager.GET_META_DATA);

            if (appInfo.metaData != null) {
                Object tmp = appInfo.metaData.get(key);
                if (tmp != null) {
                    String tmpValue = tmp.toString();
                    if (tmpValue.startsWith("<a>")) {
                        return tmpValue.substring(3);
                    } else {
                        return tmpValue;
                    }
                }
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 检查当前应用是否有新的版本
     * 
     * @param context
     * @param callBack
     *            检查新版本的回调函数
     */
    public static void checkUpdate(
            Context context, RequestCallBack<String> callBack) {
        String action = "version";
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("type", "android");
        params.addQueryStringParameter("version", "" + getVersionCode(context));
        CommonHttpUtils.get(action,
                params,
                callBack,
                "cache.check.update",
                30 * 60);
    }

    /**************************** UUID框架 ***********************************/
    private static String sID = null;// 作为设备的唯一标识符
    private static final String INSTALLATION = "INSTALLATION";

    public synchronized static String id(Context context) {
        if (context == null) {
            context = IAM007Application.getCurrentApplication();
        }

        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists()) {
                    writeInstallationFile(context, installation);
                }
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    private static String readInstallationFile(File installation)
            throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static
            void writeInstallationFile(Context context, File installation)
                    throws IOException {
        FileOutputStream out = new FileOutputStream(installation);

        String id = null;
        final String androidId = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        if (androidId == null) {
            id = UUID.randomUUID().toString();
        } else {
            if (!"9774d56d682e549c".equals(androidId)) {
                id = androidId;
            } else {
                id = UUID.randomUUID().toString();
            }
        }
        out.write(id.getBytes());
        out.close();
    }

    /////////////////////////////////////////////////////////////////////////////

    private static String mUid = null;

    /**
     * 获取当前登录用户的uid
     */
    public static String getUid() {
        Context context = IAM007Application.getCurrentApplication();
        if (mUid == null) {
            // TODO: 提示显示需要登录
            Toast.makeText(context, "请登录", Toast.LENGTH_SHORT).show();
        }

        return mUid;
    }

}
