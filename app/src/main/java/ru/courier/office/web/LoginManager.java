package ru.courier.office.web;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ru.courier.office.R;
import ru.courier.office.views.DrawerActivity;
import ru.courier.office.views.LoginActivity;


public class LoginManager extends AsyncTask<Void, Void, Void> {

    private LoginActivity _view;
    private String _postData;
    private int responseCode;

    private WebContext webContext = WebContext.getInstance();

    public LoginManager(LoginActivity view, String postData) {
        _view = view;
        _postData = postData;
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
        LoginProvider loginProvider = new LoginProvider();
        responseCode = loginProvider.doSign(_postData);
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
            LoginFailed().show();
        }
        else {
            ConnectionFailed().show();
        }
    }

    private AlertDialog LoginFailed() {
        AlertDialog alertDialog = new AlertDialog.Builder(_view, R.style.AlertDialogCustom)
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

    private AlertDialog ConnectionFailed() {
        AlertDialog alertDialog = new AlertDialog.Builder(_view, R.style.AlertDialogCustom)
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