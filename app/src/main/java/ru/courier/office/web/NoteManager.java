package ru.courier.office.web;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import ru.courier.office.data.DataAccess;
import ru.courier.office.views.HomeFragment;

public class NoteManager extends AsyncTask<Void, Void, Void> {

    private HomeFragment _view;
    private int responseCode;
    private int _maxId;

    public NoteManager(HomeFragment view, int maxId) {
        _view = view;
        _maxId = maxId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        NoteProvider noteProvider = new NoteProvider();
        responseCode = noteProvider.getNotes(_maxId);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        boolean toBeUpdated = false;

        if (responseCode == 200) {

            WebContext webContext = WebContext.getInstance();
            DataAccess dataAccess = DataAccess.getInstance(_view.getContext());
            dataAccess.addNotes(webContext.Notes);
            toBeUpdated = true;
        }

        _view.onRefreshedNotes(toBeUpdated);
    }
}