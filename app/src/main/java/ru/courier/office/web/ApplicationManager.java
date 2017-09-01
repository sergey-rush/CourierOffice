package ru.courier.office.web;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import ru.courier.office.R;
import ru.courier.office.data.DataAccess;
import ru.courier.office.views.DrawerActivity;
import ru.courier.office.views.MainActivity;
import ru.courier.office.views.QrcodeFragment;
import ru.courier.office.views.TakePhotoFragment;

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
        _fragment = fragment;
        _qrCodeValue = qrCodeValue;
    }

    private ProgressDialog pDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        _fragment.releaseCamera();
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
        return null;
    }

    @Override
    protected void onPostExecute(Void output) {
        super.onPostExecute(output);
        if (pDialog.isShowing())
            pDialog.dismiss();

        Toast.makeText(_view, "ApplicationManager.onPostExecute", Toast.LENGTH_SHORT).show();

        if (responseCode == 200) {
            SaveData();
            onBeginScanDialog().show();
            return;
        } else if (responseCode == 403) {
            AuthorizationFailed().show();
        } else if (responseCode == 400) {
            ApplicationFailed().show();
        } else {
            ConnectionFailed().show();
        }
    }

    private void SaveData()
    {
        DataAccess dataAccess = DataAccess.getInstance(_view);
        WebContext current = WebContext.getInstance();
        dataAccess.addApplication(current.Application);
    }

    private AlertDialog onBeginScanDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(_view, R.style.AlertDialogCustom)
                .setTitle("Сканирование заявки")
                .setMessage("Вы желаете продолжить сканирование заявки сейчас?")
                .setIcon(R.drawable.ic_question)
                .setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        _fragment.setFragment(new TakePhotoFragment());
                        dialog.dismiss();
                    }
                }).setNegativeButton("Отложить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Toast.makeText(_view, "Вы выбрали отложить сканирование заявки", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(_view, DrawerActivity.class);
                        _view.startActivity(intent);

                        dialog.dismiss();
                    }
                }).create();
        return alertDialog;
    }

    private AlertDialog AuthorizationFailed() {
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

    private AlertDialog ApplicationFailed() {
        AlertDialog alertDialog = new AlertDialog.Builder(_view, R.style.AlertDialogCustom)
                .setTitle("Заявка недействительна")
                .setMessage("Кредитная заявка недействительна")
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
