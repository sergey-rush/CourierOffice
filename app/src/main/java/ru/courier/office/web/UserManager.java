package ru.courier.office.web;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import ru.courier.office.R;
import ru.courier.office.views.DrawerActivity;
import ru.courier.office.views.MainActivity;

/**
 * Created by rash on 21.08.2017.
 */

public class UserManager extends AsyncTask<Void, Void, Void> {

    private MainActivity _view;
    private int responseCode;

    private WebContext dataContext = WebContext.getInstance();

    public UserManager(MainActivity view) {
        _view = view;
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
        UserProvider userProvider = new UserProvider();
        responseCode = userProvider.getUser();
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
            UserFailed().show();
        } else {
            ConnectionFailed().show();
        }
    }

    private AlertDialog UserFailed() {
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
