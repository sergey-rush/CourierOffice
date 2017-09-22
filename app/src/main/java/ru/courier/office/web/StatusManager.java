package ru.courier.office.web;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import ru.courier.office.R;
import ru.courier.office.core.Application;
import ru.courier.office.data.DataAccess;

public class StatusManager extends AsyncTask<Void, Void, Void> {

    private Context _context;
    Application _application;
    private int responseCode;

    public StatusManager(Context context, Application application) {
        _context = context;
        _application = application;
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

        String postData = String.format("{\"ApplicationId\":\"%s\", \"DeliveryStatus\":\"%s\"}", _application.ApplicationGuid, _application.ApplicationStatus.ordinal());
        StatusProvider statusProvider = new StatusProvider();
        responseCode = statusProvider.postStatus(postData);
        if (responseCode == 201) {
            DataAccess dataAccess = DataAccess.getInstance(_context);
            WebContext current = WebContext.getInstance();
            dataAccess.updateApplicationByApplicationStatus(_application.Id, _application.ApplicationStatus);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void output) {
        super.onPostExecute(output);
        if (pDialog.isShowing())
            pDialog.dismiss();
        if (responseCode == 201) {

            return;
        } else {
            ConnectionFailed().show();
        }
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
