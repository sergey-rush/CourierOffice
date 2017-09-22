package ru.courier.office;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

import ru.courier.office.core.LocalSettings;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.WebContext;

public class CourierOfficeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        setDeviceInfo();
        WebContext.getInstance();
        DataAccess dataAccess = DataAccess.getInstance(getApplicationContext());
        //dataAccess.createDatabase();
    }

    private boolean setDeviceInfo() {
        if(LocalSettings.getDeviceID(this).equals("")){
            //String deviceUniqueId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String deviceUniqueId = telephonyManager.getDeviceId();

            if(deviceUniqueId == null){
                return false;
            }

            LocalSettings.saveDeviceID(this, deviceUniqueId);
        }

        if(LocalSettings.getNotificationID(this).equals("")){
            LocalSettings.saveNotificationID(this, "00000000000");
        }

        return true;

    }
}
