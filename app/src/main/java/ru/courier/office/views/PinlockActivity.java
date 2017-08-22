package ru.courier.office.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import ru.courier.office.R;
import ru.courier.office.core.CustomDialog;
import com.pinlock.IndicatorDots;
import com.pinlock.PinLockListener;
import com.pinlock.PinLockView;

public class PinlockActivity extends AppCompatActivity  {

        public static final String TAG = "PinLockView";
    private String pinCode = "1111";
    private int failedAttempts = 3;

        private PinLockView mPinLockView;
        private IndicatorDots mIndicatorDots;

        private PinLockListener mPinLockListener = new PinLockListener() {

            @Override
            public void onComplete(String pin) {

                if(pin.equals(pinCode))
                {
                    Intent intent = new Intent(getBaseContext(), DrawerActivity.class);
                    startActivity(intent);
                }
                else {
                    mIndicatorDots.resetPinLock();

                    failedAttempts--;
                    if(failedAttempts==0)
                    {
                        AskOption().show();
                    }
                }
            }

            @Override
            public void onEmpty() {
                Log.d(TAG, "Pin empty");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                //Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
                //mPinLockView.resetPinLockView();

            }
        };

    private AlertDialog AskOption() {
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("Ошибка пинкода")
                .setMessage("Количество попыток ввода пин-кода превысило максимальное")
                .setIcon(R.drawable.ic_error)
                .setPositiveButton("Понятно", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }

                }).create();
        return alertDialog;
    }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_pinlock);

            mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
            mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

            mPinLockView.attachIndicatorDots(mIndicatorDots);
            mPinLockView.setPinLockListener(mPinLockListener);
            //mPinLockView.setCustomKeySet(new int[]{2, 3, 1, 5, 9, 6, 7, 0, 8, 4});
            //mPinLockView.enableLayoutShuffling();

            mPinLockView.setPinLength(4);
            mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

            //mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);

            //mPinLockView.setPinLockListener();
        }

    public void onPinLostClick(View view)
    {
        ShowMessage().show();
    }

    private AlertDialog ShowMessage() {
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("Пинкод заблокирован")
                .setMessage("Войдите в приложение используя свой логин и пароль")
                .setIcon(R.drawable.ic_error)
                .setPositiveButton("Понятно", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }

                }).create();
        return alertDialog;
    }

}
