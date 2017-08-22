package ru.courier.office;

import android.app.Application;

import ru.courier.office.data.DataContext;

/**
 * Created by rash on 21.08.2017.
 */

public class CourierOfficeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataContext.getInstance();

    }
}
