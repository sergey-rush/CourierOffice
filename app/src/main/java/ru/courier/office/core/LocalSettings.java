package ru.courier.office.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rash on 22.08.2017.
 */

public class LocalSettings {

    public static final String OPERATOR_PHONE_NUMBER = "+7123456789";
    //public static final int TOTAL_PHOTOS = 8;
    public static final String ORIGINAL_PICTURE_NAME = "orig_picture.jpg";
    public static String SCANNED_FILE_NAME_PREFIX = "final_pict_";
    public static String SCANNED_FILE_NAME_POSTFIX = ".jpg";

    private static final String APP_PREFERENCES = "app_settings";

    private static final String DEVICE_ID = "device_id";
    private static final String APP_ID = "app_id";
    private static final String NOTIFICATION_ID = "notification_id";
    private static final String ACTIVATED = "activated";
    private static final String FRAME_RATIO = "frame_ratio";
    private static final String CURRENT_DOC = "current_doc";
    private static final String CURRENT_PAGE = "current_page";
    private static final String DEVICE_ORIENTATION = "device_orientation";
    private static final String ORDER_ID = "order_id";
    private static final String RESUME_PAGE_REACHED = "resume_page_reached";
    private static final String TOKEN = "token";
    private static final String TDES_KEY = "tdes_key";
    private static final String TDES_IV = "tdes_IV";
    private static final String ENTER = "enter_";
    private static final String EXIT = "exit";
    private static final String PICTURES_FOR_DOCUMENT_NUM = "pictures_for_document_num";


    public static void saveDeviceID(Context context, String id) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(DEVICE_ID, id);
        editor.apply();
    }

    public static String getDeviceID(Context context) {
        String result = "";
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(DEVICE_ID)) {
            result = appSettings.getString(DEVICE_ID, "");
        }
        return result;
    }

    public static void saveNotificationID(Context context, String id) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(NOTIFICATION_ID, id);
        editor.apply();
    }

    public static String getNotificationID(Context context) {
        String result = "";
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(NOTIFICATION_ID)) {
            result = appSettings.getString(NOTIFICATION_ID, "");
        }
        return result;
    }


    public static void setActivated(Context context, boolean activated) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean(ACTIVATED, activated);
        editor.apply();
    }

    public static boolean getActivated(Context context) {
        boolean result = false;
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(ACTIVATED)) {
            result = appSettings.getBoolean(ACTIVATED, false);
        }
        return result;
    }


    public static void setFrameRatio(Context context, float ratio) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putFloat(FRAME_RATIO, ratio);
        editor.apply();
    }

    public static float getFrameRatio(Context context) {
        float result = 0;
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(FRAME_RATIO)) {
            result = appSettings.getFloat(FRAME_RATIO, 0);
        }
        return result;
    }

    public static void setOrderId(Context context, String orderId) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(ORDER_ID, orderId);
        editor.apply();
    }

    public static String getOrderId(Context context) {
        String result = "";
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(ORDER_ID)) {
            result = appSettings.getString(ORDER_ID, "");
        }
        return result;
    }

    public static void setToken(Context context, String token) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context) {
        String result = "";
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(TOKEN)) {
            result = appSettings.getString(TOKEN, "");
        }
        return result;
    }


    public static void setTdesKey(Context context, String tdesKey) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(TDES_KEY, tdesKey);
        editor.apply();
    }

    public static String getTdesKey(Context context) {
        String result = "";
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(TDES_KEY)) {
            result = appSettings.getString(TDES_KEY, "");
        }
        return result;
    }

    public static void setTdesIV(Context context, String tdesIV) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(TDES_IV, tdesIV);
        editor.apply();
    }

    public static String getTdesIV(Context context) {
        String result = "";
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(TDES_IV)) {
            result = appSettings.getString(TDES_IV, "");
        }
        return result;
    }

    public static void setCurrentDocNumber(Context context, int currentPhoto) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putInt(CURRENT_DOC, currentPhoto);
        editor.apply();
    }

    public static int getCurrentDocNumber(Context context) {
        int result = 0;
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(CURRENT_DOC)) {
            result = appSettings.getInt(CURRENT_DOC, 0);
        }
        return result;
    }


    public static void setCurrentPage(Context context, int currentPage) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putInt(CURRENT_PAGE, currentPage);
        editor.apply();
    }

    public static int getCurrentPage(Context context) {
        int result = 0;
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(CURRENT_PAGE)) {
            result = appSettings.getInt(CURRENT_PAGE, 0);
        }
        return result;
    }

    public static void setDeviceOrientationDuringTakePhoto(Context context, int currentPhoto) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putInt(DEVICE_ORIENTATION, currentPhoto);
        editor.apply();
    }

    public static int getDeviceOrientationDuringTakePhoto(Context context) {
        int result = OrientationManager.ScreenOrientation.PORTRAIT.ordinal();
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(DEVICE_ORIENTATION)) {
            result = appSettings.getInt(DEVICE_ORIENTATION, OrientationManager.ScreenOrientation.PORTRAIT.ordinal());
        }
        return result;
    }

    public static void setResumePageReached(Context context, boolean activated) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean(RESUME_PAGE_REACHED, activated);
        editor.apply();
    }

    public static boolean getResumePageReached(Context context) {
        boolean result = false;
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(RESUME_PAGE_REACHED)) {
            result = appSettings.getBoolean(RESUME_PAGE_REACHED, false);
        }
        return result;
    }


    public static void setEnterTime(Context context, int photoNum, String dateStr) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(ENTER + photoNum, dateStr);
        editor.apply();
    }

    public static String getEnterTime(Context context, int photoNum) {
        String result = "";
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(ENTER + photoNum)) {
            result = appSettings.getString(ENTER + photoNum, "");
        }
        return result;
    }

    public static void setExitTime(Context context, int photoNum, String dateStr) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(EXIT + photoNum, dateStr);
        editor.apply();
    }

    public static String getExitTime(Context context, int photoNum) {
        String result = "";
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(EXIT + photoNum)) {
            result = appSettings.getString(EXIT + photoNum, "");
        }
        return result;
    }


    public static void setPicturesForDocument(Context context, int docNum) {
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putInt(PICTURES_FOR_DOCUMENT_NUM, docNum);
        editor.apply();
    }

    public static int getPicturesForDocument(Context context) {
        int result = 0;
        SharedPreferences appSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (appSettings.contains(PICTURES_FOR_DOCUMENT_NUM)) {
            result = appSettings.getInt(PICTURES_FOR_DOCUMENT_NUM, 0);
        }
        return result;
    }


    public static String getCurrentPictureFileName(Context context) {
        return SCANNED_FILE_NAME_PREFIX + getOrderId(context).replaceAll("[^A-Za-z0-9]+", "") + "_" + getCurrentDocNumber(context) + SCANNED_FILE_NAME_POSTFIX;
    }

    public static String getPictureFileName(Context context, int pictNum) {
        return SCANNED_FILE_NAME_PREFIX + getOrderId(context).replaceAll("[^A-Za-z0-9]+", "") + "_" + Integer.toString(pictNum) + SCANNED_FILE_NAME_POSTFIX;
    }

    //public static String getEncodedPictureFileName(String orderId, int pictNum) {
     //   return PhotoEncodeIntentService.ENCODED_PREFIX + SCANNED_FILE_NAME_PREFIX + orderId.replaceAll("[^A-Za-z0-9]+", "") + "_" + Integer.toString(pictNum) + SCANNED_FILE_NAME_POSTFIX;
    //}

}
