package ru.courier.office.web;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.Application;
import ru.courier.office.core.Document;
import ru.courier.office.core.LocalSettings;
import ru.courier.office.core.Scan;
import ru.courier.office.core.ScanStatus;
import ru.courier.office.data.DataAccess;

public class ScanManager extends AsyncTask<Void, Void, Void> {

    private int responseCode;
    private Context _context;
    private Application _application;
    private WebContext _webContext;
    private String _deviceId;
    private DataAccess _dataAccess;

    public ScanManager(Context context, Application application) {
        _context = context;
        _application = application;
        _webContext = WebContext.getInstance();
        _deviceId = LocalSettings.getDeviceID(_context);
        _dataAccess = DataAccess.getInstance(_context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        _dataAccess.updateScansByApplicationGuid(_webContext.Application.ApplicationGuid, ScanStatus.Ready);

        List<Document> documents = _dataAccess.getDocumentsByApplicationGuid(_application.ApplicationGuid);

        for (Document document : documents) {
            List<Scan> scans = _dataAccess.getScansByDocumentId(document.Id);

            for (Scan scan : scans) {
                _webContext.Scan = scan;

                if (scan.ScanStatus == ScanStatus.Ready) {
                    String postData = FormatPayload(document, scan);
                    ScanProvider scanProvider = new ScanProvider();
                    responseCode = scanProvider.getInfo(postData);
                    if (responseCode == 200 || responseCode == 201) {
                        _webContext.Scan.ScanStatus = ScanStatus.Progress;
                        _dataAccess.updateScan(_webContext.Scan);
                    }
                    else{
                        break; // inner loop
                    }
                }

                if (scan.ScanStatus == ScanStatus.Progress) {
                    uploadScan(_webContext.Scan);
                    if (responseCode == 200) {
                        _webContext.Scan.ScanStatus = ScanStatus.Completed;
                        _dataAccess.updateScan(_webContext.Scan);
                    }
                    else{
                        break; // inner loop
                    }
                }
            }

            if (responseCode != 200) {
                break; // outer loop
            }
        }
        return null;
    }

    public void uploadScan(Scan scan) {

        int scanImageLength = scan.ImageLength;
        byte[] imageBytes = new byte[scanImageLength];

        // The bytes have already been read
        int totalBytes = 0;
        int bufferSize = 1 * 1024 * 1024;

        if (scanImageLength < bufferSize) {
            bufferSize = scanImageLength;
        }

        while (totalBytes < scanImageLength) {

            if (totalBytes + bufferSize > scanImageLength) {
                bufferSize = scanImageLength - totalBytes;
            }

            byte[] buffer = new byte[bufferSize];
            buffer = _dataAccess.getScanImage(scan.Id, totalBytes, bufferSize);

            System.arraycopy(buffer, 0, imageBytes, totalBytes, buffer.length);
            totalBytes = (totalBytes + bufferSize);
        }

        ScanProvider scanProvider = new ScanProvider();
        responseCode = scanProvider.doUpload(scan, imageBytes);
    }


    public void uploadScan1(Scan scan) {

        int imageLength = scan.ImageLength;
        int sendBytes = 0;
        int bufferLength = 1 * 1024 * 1024;

        while (sendBytes < imageLength) {

            if (bufferLength > imageLength - sendBytes) {
                bufferLength = imageLength - sendBytes;
            }

            byte[] imageBytes = _dataAccess.getScanImage(scan.Id, sendBytes, bufferLength);
            int imageBytesLength = imageBytes.length;
            ScanProvider scanProvider = new ScanProvider();
            responseCode = scanProvider.doUpload(scan, imageBytes);
            if (responseCode != 200) {
                return;
            }
            sendBytes = sendBytes + imageBytesLength;
        }
    }

    private String FormatPayload(Document document, Scan scan)
    {
        String photoId = scan.PhotoGuid;
        String appId = scan.ApplicationGuid;
        String fileName = document.Title;
        String appType = "CourierAppV2";
        String docId = document.DocumentGuid;
        String pageNum = Integer.toString(scan.PageNum);
        String postData = String.format("{\"PhotoId\":\"%s\", \"ApplicationId\":\"%s\", \"FileName\":\"%s\", \"AppType\":\"%s\", \"DocumentId\":\"%s\", \"PageNum\":\"%s\", \"Imei\":\"%s\"}", photoId, appId, fileName, appType, docId, pageNum, _deviceId);
        return postData;
    }

    @Override
    protected void onPostExecute(Void output) {
        super.onPostExecute(output);

        if (responseCode != 200) {
            loadFailedDialog().show();
        }
    }

    private AlertDialog loadFailedDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(_context, R.style.AlertDialogCustom)
                .setTitle("Ошибка загрузки")
                .setMessage("Сервер недоступен. Документы будут отправлены позже.")
                .setIcon(R.drawable.ic_error)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                }).create();
        return alertDialog;
    }
}