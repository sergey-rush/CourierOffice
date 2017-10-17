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
    }
}