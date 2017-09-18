package ru.courier.office.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import ru.courier.office.web.MemberProvider;
import ru.courier.office.web.WebContext;

/**
 * Created by rash on 18.09.2017.
 */

public class AppViewManager extends AsyncTask<Void, Void, Void> {

    private Context _context;

    int _applicationId;

    public AppViewManager(Context context, int applicationId) {
        _context = context;
        _applicationId = applicationId;
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
        DataAccess dataAccess = DataAccess.getInstance(_context);
        WebContext webContext = WebContext.getInstance();
        webContext.Application = dataAccess.getApplicationById(_applicationId);
        webContext.Application.DocumentList = dataAccess.getDocumentsByApplicationGuid(webContext.Application.ApplicationGuid);
        webContext.Application.StatusList = dataAccess.getStatusesByApplicationId(webContext.Application.Id);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // Dismiss the progress dialog
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}