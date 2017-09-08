package ru.courier.office.web;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import ru.courier.office.R;
import ru.courier.office.views.DrawerActivity;
import ru.courier.office.views.LoginActivity;

public class PositionManager extends AsyncTask<Void, Void, Void> {

    private String _postData;
    private int responseCode;

    private WebContext webContext = WebContext.getInstance();

    public PositionManager(Location location) {
        String dateStr = webContext.getCurrentDateFormatted();
        String userId = Integer.toString(webContext.User.Id);
        _postData = String.format("{\"UserId\":\"%s\", \"Latitude\":\"%s\", \"Longitude\":\"%s\", \"Created\":\"%s\"}", userId, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), dateStr);
    }

    private ProgressDialog pDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        PositionProvider positionProvider = new PositionProvider();
        responseCode = positionProvider.sendPosition(_postData);
        return null;
    }

    @Override
    protected void onPostExecute(Void output) {
        super.onPostExecute(output);

        if (responseCode == 200) {
        }
    }
}