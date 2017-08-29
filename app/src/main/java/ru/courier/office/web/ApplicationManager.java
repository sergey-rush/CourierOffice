package ru.courier.office.web;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.View;

import ru.courier.office.R;
import ru.courier.office.data.DataAccess;
import ru.courier.office.views.DrawerActivity;
import ru.courier.office.views.MainActivity;
import ru.courier.office.views.QrcodeFragment;

/**
 * Created by rash on 22.08.2017.
 */

public class ApplicationManager extends AsyncTask<Void, Void, Void> {

    QrcodeFragment _fragment;
    private Context _view;
    private String _qrCodeValue;
    private int responseCode;

    private WebContext webContext = WebContext.getInstance();

    public ApplicationManager(Context context, QrcodeFragment fragment, String qrCodeValue) {
        _view = context;
        _fragment=fragment;
        _qrCodeValue = qrCodeValue;
    }

    private ProgressDialog pDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog = new ProgressDialog(_view);
        pDialog.setMessage("Пожалуйста, подождите...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        String postData = String.format("{\"ApplicationId\":\"%s\"}", _qrCodeValue);
        ApplicationProvider applicationProvider = new ApplicationProvider();
        responseCode = applicationProvider.postApplication(postData);
        _fragment.releaseCamera();

        DataAccess dataAccess = DataAccess.getInstance(_view);
        WebContext current = WebContext.getInstance();

        long merchantId = dataAccess.insertMerchant(current.Application.Merchant);

        for(ru.courier.office.core.Status status:current.Application.StatusList) {
            dataAccess.insertStatus(status);
        }
        current.Application.MerchantId = (int)merchantId;

        long personId = 0;
        if(current.Application.Person!=null)
        {
            personId = dataAccess.insertPerson(current.Application.Person);
        }
        current.Application.PersonId = (int)personId;

        dataAccess.insertApplication(current.Application);
        
        return null;
    }

    @Override
    protected void onPostExecute(Void output) {
        super.onPostExecute(output);
        if (pDialog.isShowing())
            pDialog.dismiss();

        if (responseCode == 200) {
            Intent intent = new Intent(_view, DrawerActivity.class);
            _view.startActivity(intent);
            return;
        }

        if (responseCode == 403) {
            ApplicationFailed().show();
        } else {
            ConnectionFailed().show();
        }


    }

    private AlertDialog ApplicationFailed() {
        AlertDialog alertDialog = new AlertDialog.Builder(_view, R.style.AlertDialogCustom)
                .setTitle("Ошибка входа")
                .setMessage("Неверный телефон или пароль")
                .setIcon(R.drawable.ic_error)
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                }).create();
        return alertDialog;
    }

    private AlertDialog ConnectionFailed() {
        AlertDialog alertDialog = new AlertDialog.Builder(_view, R.style.AlertDialogCustom)
                .setTitle("Ошибка соединения")
                .setMessage("Проверьте подключение к интернету")
                .setIcon(R.drawable.ic_error)
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                }).create();
        return alertDialog;
    }
}
