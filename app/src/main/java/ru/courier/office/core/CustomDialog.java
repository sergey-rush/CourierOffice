package com.clientoffice.core;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

public class CustomDialog extends AlertDialog {

    public CustomDialog(final Context context) {
        super(context);
        setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button negativeButton = getButton(DialogInterface.BUTTON_NEGATIVE);
                Button positiveButton = getButton(DialogInterface.BUTTON_POSITIVE);

                negativeButton.setBackgroundColor(Color.GREEN);
                positiveButton.setBackgroundColor(Color.RED);
            }
        });
    }
}