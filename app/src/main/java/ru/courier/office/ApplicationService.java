package ru.courier.office;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import ru.courier.office.services.PositionService;


public class ApplicationService extends Service {

    private PositionService _positionService;
    private Timer _timer;

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        _positionService = new PositionService(getApplicationContext());
        _positionService.startLocationManager(true);
        startTimer();
        return START_STICKY;
    }

    final Handler handler = new Handler();

    private void startTimer() {
        stopTimer();
        _timer = new Timer();
        _timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, 0, 15000);
    }

    private void stopTimer() {
        if (_timer != null) {
            _timer.cancel();
            _timer = null;
        }
    }

    final Runnable runnable = new Runnable() {
        public void run() {
            onTimerTick();
        }
    };

    private void onTimerTick() {
        Context context = getApplicationContext();
    }

    public void onDestroy() {
        _positionService.stopLocationManager();
        stopTimer();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
