package ru.courier.office.web;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import ru.courier.office.R;
import ru.courier.office.core.Document;
import ru.courier.office.core.LocalSettings;
import ru.courier.office.core.Scan;
import ru.courier.office.views.ApplicationFragment;
import ru.courier.office.views.DrawerActivity;
import ru.courier.office.views.LoginActivity;

public class ScanManager extends AsyncTask<Void, Void, Void> {

    private ApplicationFragment _fragment;
    private Document _document;
    private Scan _scan;
    private String _postData;
    private int responseCode;

    private WebContext webContext = WebContext.getInstance();

    public ScanManager(ApplicationFragment fragment, Document document, Scan scan) {

        _fragment = fragment;
        _document = document;
        _scan = webContext.Scan = scan;

        String photoId = scan.PhotoGuid;
        String appId = scan.ApplicationGuid;
        String fileName = document.Title;
        String appType = "CourierAppV2";
        String docId = document.DocumentGuid;
        String pageNum = Integer.toString(scan.PageNum);
        String imei = LocalSettings.getDeviceID(_fragment.getContext());
        _postData = String.format("{\"PhotoId\":\"%s\", \"ApplicationId\":\"%s\", \"FileName\":\"%s\", \"AppType\":\"%s\", \"DocumentId\":\"%s\", \"PageNum\":\"%s\", \"Imei\":\"%s\"}", photoId, appId, fileName, appType, docId, pageNum, imei);
    }

    private ProgressDialog pDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        ScanProvider scanProvider = new ScanProvider();
        responseCode = scanProvider.getInfo(_postData);
        return null;
    }

    @Override
    protected void onPostExecute(Void output) {
        super.onPostExecute(output);

        if (responseCode == 200) {
            _fragment.onScanRetrieved(webContext.Scan);
            return;
        }
    }
}