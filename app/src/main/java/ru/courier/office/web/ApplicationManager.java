package ru.courier.office.web;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import ru.courier.office.R;
import ru.courier.office.data.DataAccess;
import ru.courier.office.views.AppListFragment;
import ru.courier.office.views.QrcodeFragment;
import ru.courier.office.views.TakePhotoFragment;

public class ApplicationManager extends AsyncTask<Void, Void, Void> {

    QrcodeFragment _fragment;
    private Context _context;
    private String _qrCodeValue;
    private int applicationId;
    private int responseCode;

    private WebContext webContext = WebContext.getInstance();

    public ApplicationManager(Context context, QrcodeFragment fragment, String qrCodeValue) {
        _context = context;
        _fragment = fragment;
        _qrCodeValue = qrCodeValue;
    }

    private ProgressDialog pDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(_context);
        pDialog.setMessage("Пожалуйста, подождите...");
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    protected Void doInBackground(Void... arg0) {

        String postData = String.format("{\"ApplicationId\":\"%s\"}", _qrCodeValue);
        ApplicationProvider applicationProvider = new ApplicationProvider();
        responseCode = applicationProvider.postApplication(postData);
        if (responseCode == 200) {
            DataAccess dataAccess = DataAccess.getInstance(_context);
            WebContext current = WebContext.getInstance();
            applicationId = dataAccess.addApplication(current.Application);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void output) {
        super.onPostExecute(output);
        if (pDialog.isShowing())
            pDialog.dismiss();
        if (responseCode == 200) {
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

    private AlertDialog onBeginScanDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(_context, R.style.AlertDialogCustom)
                .setTitle("Сканирование заявки")
                .setMessage("Вы желаете продолжить сканирование заявки сейчас?")
                .setIcon(R.drawable.ic_question)
                .setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        TakePhotoFragment fragment = TakePhotoFragment.newInstance(applicationId, 0, 0);
                        FragmentManager fm = ((AppCompatActivity) _context).getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.container, fragment);
                        ft.commit();
                        dialog.dismiss();
                    }
                }).setNegativeButton("Отложить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        AppListFragment fragment = new AppListFragment();
                        FragmentManager fm = ((AppCompatActivity) _context).getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.container, fragment);
                        ft.commit();
                        dialog.dismiss();
                    }
                }).create();
        return alertDialog;
    }

    private AlertDialog AuthorizationFailed() {
        AlertDialog alertDialog = new AlertDialog.Builder(_context, R.style.AlertDialogCustom)
                .setTitle("Ошибка входа")
                .setMessage("Неверный телефон или пароль")
                .setIcon(R.drawable.ic_error)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                }).create();
        return alertDialog;
    }

    private AlertDialog ApplicationFailed() {
        AlertDialog alertDialog = new AlertDialog.Builder(_context, R.style.AlertDialogCustom)
                .setTitle("Заявка недействительна")
                .setMessage("Кредитная заявка недействительна")
                .setIcon(R.drawable.ic_error)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                }).create();
        return alertDialog;
    }

    private AlertDialog ConnectionFailed() {
        AlertDialog alertDialog = new AlertDialog.Builder(_context, R.style.AlertDialogCustom)
                .setTitle(R.string.connection_failed)
                .setMessage(R.string.connection_not_available)
                .setIcon(R.drawable.ic_error)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                }).create();
        return alertDialog;
    }


}
