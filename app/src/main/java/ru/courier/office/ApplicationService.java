package ru.courier.office;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.courier.office.core.Application;
import ru.courier.office.core.ApplicationStatus;
import ru.courier.office.core.Document;
import ru.courier.office.core.LocalSettings;
import ru.courier.office.core.Scan;
import ru.courier.office.core.ScanStatus;
import ru.courier.office.data.DataAccess;
import ru.courier.office.services.PositionService;
import ru.courier.office.web.StatusProvider;
import ru.courier.office.web.UploadProvider;
import ru.courier.office.web.WebContext;


public class ApplicationService extends Service {

    private PositionService _positionService;
    private boolean _running = false;
    private Context _context;
    private static Timer _timer;
    private final Handler _handler = new Handler();
    List<Application> _applications;


    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        _context = getApplicationContext();

        _positionService = new PositionService(_context);
        _positionService.startLocationManager(true);

        startTimer();
        return START_STICKY;
    }

    private void startTimer() {

        if (_timer == null) {

            _timer = new Timer();
            _timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    _handler.post(_runnable);
                }
            }, 0, 600000);
        }
    }

    private void stopTimer() {

        if (_timer != null) {
            _timer.cancel();
            stopSelf();
        }
    }

    final Runnable _runnable = new Runnable() {
        public void run() {
            if (!_running) {
                startUploading();
            }
        }
    };

    private void startUploading() {
        _running = true;
        Toast.makeText(_context, "Uploading started", Toast.LENGTH_SHORT).show();
        UploadManager uploadManager = new UploadManager();
        uploadManager.execute();
    }

    private class UploadManager extends AsyncTask<Void, Void, Void> {

        private int _responseCode;
        private WebContext _webContext;
        private String _deviceId;
        private DataAccess _dataAccess;

        public UploadManager() {

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

            _applications = _dataAccess.getApplicationsByApplicationStatus(ApplicationStatus.Deliver);

            for (Application application : _applications) {
                deliverApplication(application);
                if (_responseCode != 201) { // we wait untill application status has been send, otherwise operation completed with error
                    break;
                }
            }

            _applications = _dataAccess.getApplicationsByApplicationStatus(ApplicationStatus.Reject);

            for (Application application : _applications) {
                rejectApplication(application);
                if (_responseCode != 201) { // we wait untill application status has been send, otherwise operation completed with error
                    break;
                }
            }

            return null;
        }

        private void rejectApplication(Application application) {
            sendStatus(application);
        }

        private void deliverApplication(Application application) {
            sendDocuments(application);
            if (_responseCode == 200) {
                sendStatus(application);
            }
        }

        private void sendDocuments(Application application) {

            List<Document> documents = _dataAccess.getDocumentsByApplicationGuid(application.ApplicationGuid);

            for (Document document : documents) {
                List<Scan> scans = _dataAccess.getScansByDocumentId(document.Id);

                for (Scan scan : scans) {

                    _webContext.Scan = scan;

                    if (scan.ScanStatus == ScanStatus.Ready) {
                        String postData = FormatPayload(document, scan);
                        UploadProvider uploadProvider = new UploadProvider();
                        _responseCode = uploadProvider.getInfo(postData);// we register every scan image on the remote server
                        if (_responseCode == 200 || _responseCode == 201) {
                            _webContext.Scan.ScanStatus = ScanStatus.Progress;
                            _dataAccess.updateScan(_webContext.Scan);
                        } else {
                            break; // inner loop
                        }
                    }

                    if (scan.ScanStatus == ScanStatus.Progress) {
                        uploadScan(_webContext.Scan);// when scan image is registered on remote server we send image itself
                        if (_responseCode == 200) {
                            _webContext.Scan.ScanStatus = ScanStatus.Completed;
                            _dataAccess.updateScan(_webContext.Scan);
                        } else {
                            break; // inner loop
                        }
                    }

                    if (scan.ScanStatus == ScanStatus.Completed) {
                        _responseCode = 200;
                    }
                }

                if (_responseCode != 200) {// if server is inaccessible we do exit the loop and terminate
                    break; // outer loop
                }
            }
        }

        private String FormatPayload(Document document, Scan scan) {
            String photoId = scan.PhotoGuid;
            String appId = scan.ApplicationGuid;
            String fileName = document.Title;
            String appType = "CourierAppV2";
            String docId = document.DocumentGuid;
            String pageNum = Integer.toString(scan.PageNum);
            String postData = String.format("{\"PhotoId\":\"%s\", \"ApplicationId\":\"%s\", \"FileName\":\"%s\", \"AppType\":\"%s\", \"DocumentId\":\"%s\", \"PageNum\":\"%s\", \"Imei\":\"%s\"}", photoId, appId, fileName, appType, docId, pageNum, _deviceId);
            return postData;
        }

        private void uploadScan(Scan scan) {

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

            UploadProvider uploadProvider = new UploadProvider();
            _responseCode = uploadProvider.doUpload(scan, imageBytes);
        }

        private void sendStatus(Application application) {
            String postData = String.format("{\"ApplicationId\":\"%s\", \"DeliveryStatus\":\"%s\"}", application.ApplicationGuid, application.ApplicationStatus.ordinal());
            StatusProvider statusProvider = new StatusProvider();
            _responseCode = statusProvider.putStatus(postData);
            if (_responseCode == 201) {
                _dataAccess.removeApplication(application.Id);
            }
        }

        public void uploadScanByPartition(Scan scan) {

            int imageLength = scan.ImageLength;
            int sendBytes = 0;
            int bufferLength = 1 * 1024 * 1024;

            while (sendBytes < imageLength) {

                if (bufferLength > imageLength - sendBytes) {
                    bufferLength = imageLength - sendBytes;
                }

                byte[] imageBytes = _dataAccess.getScanImage(scan.Id, sendBytes, bufferLength);
                int imageBytesLength = imageBytes.length;
                UploadProvider uploadProvider = new UploadProvider();
                _responseCode = uploadProvider.doUpload(scan, imageBytes);
                if (_responseCode != 200) {
                    return;
                }
                sendBytes = sendBytes + imageBytesLength;
            }
        }

        @Override
        protected void onPostExecute(Void output) {
            super.onPostExecute(output);
            _running = false;
            Toast.makeText(_context, "Uploading completed: " + _responseCode, Toast.LENGTH_SHORT).show();
            int count = _dataAccess.countApplicationsExceptApplicationStatus(ApplicationStatus.None);
            if (count == 0) {
                stopTimer();
            }
        }
    }

    @Override
    public void onDestroy() {
        _positionService.stopLocationManager();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
