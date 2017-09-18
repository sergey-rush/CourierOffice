package ru.courier.office.web;

import android.content.Context;
import android.os.AsyncTask;

import ru.courier.office.data.DataAccess;
import ru.courier.office.views.HomeFragment;

public class NoteManager extends AsyncTask<Void, Void, Void> {

    private HomeFragment _fragment;
    private int responseCode;

    public NoteManager(HomeFragment fragment) {
        _fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        DataAccess dataAccess = DataAccess.getInstance(_fragment.getContext());
        int maxId = dataAccess.getNoteMaxId();
        NoteProvider noteProvider = new NoteProvider();
        responseCode = noteProvider.getNotes(maxId);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        boolean toBeUpdated = false;

        if (responseCode == 200) {

            WebContext webContext = WebContext.getInstance();
            DataAccess dataAccess = DataAccess.getInstance(_fragment.getContext());
            dataAccess.addNotes(webContext.Notes);
            toBeUpdated = true;
        }

        _fragment.onRefreshedNotes(toBeUpdated);
    }
}