package ru.courier.office;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import ru.courier.office.core.LocalSettings;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.WebContext;

public class CourierOfficeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataAccess dataAccess = DataAccess.getInstance(getApplicationContext());
        WebContext webContext = WebContext.getInstance();
        webContext.Imei = setDeviceInfo();

    }

    private String setDeviceInfo() {
        String deviceId = getDeviceId();
        LocalSettings.saveDeviceID(this, deviceId);

        if (LocalSettings.getNotificationID(this).equals("")) {
            LocalSettings.saveNotificationID(this, "00000000000");
        }
        return deviceId;
    }

    private String getDeviceId() {
        String deviceId;
        TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            deviceId = mTelephony.getDeviceId();
        } else {
            deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }
}
