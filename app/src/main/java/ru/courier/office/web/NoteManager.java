package ru.courier.office.web;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import ru.courier.office.data.DataAccess;

public class NoteManager extends AsyncTask<Void, Void, Void> {

    private View _view;

    int id;

    public NoteManager(View view, int id) {
        _view = view;
        id = id;
    }

    private ProgressDialog pDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog = new ProgressDialog(_view.getContext());
        pDialog.setMessage("Пожалуйста, подождите...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        NoteProvider noteProvider = new NoteProvider();
        int status = noteProvider.getNotes(id);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        WebContext webContext = WebContext.getInstance();
        DataAccess dataAccess = DataAccess.getInstance(_view.getContext());
        dataAccess.addNotes(webContext.Notes);

        if (pDialog.isShowing())
            pDialog.dismiss();


    }

}