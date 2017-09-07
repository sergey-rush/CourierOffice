package ru.courier.office.web;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import ru.courier.office.R;
import ru.courier.office.core.Scan;


public class ImageManager extends AsyncTask<Void, Void, Void> {

    private Context _context;
    private byte[] _imageBytes;
    private int responseCode;
    private Scan _scan;

    private WebContext webContext = WebContext.getInstance();

    public ImageManager(Context context, Scan scan, byte[] imageBytes) {
        _context = context;
        _scan = scan;
        _imageBytes = imageBytes;
    }

    private ProgressDialog pDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog = new ProgressDialog(_context);
        pDialog.setMessage("Пожалуйста, подождите...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        String postData = String.format("id=%s&name=%s", "342", "App name");
//        String applicationGuid
//        String documentGuid
//        int page

        UploadProvider uploadProvider = new UploadProvider();
        responseCode = uploadProvider.doUpload(_scan, _imageBytes);

        //ImageProvider imageProvider = new ImageProvider();
        //responseCode = imageProvider.doUpload(_scan, _imageBytes);
        return null;
    }

    @Override
    protected void onPostExecute(Void output) {
        super.onPostExecute(output);
        if (pDialog.isShowing())
            pDialog.dismiss();

        if (responseCode == 200) {
            //Intent intent = new Intent(_context, DrawerActivity.class);
            //_context.startActivity(intent);
            return;
        }

        if (responseCode == 403) {
            ImageFailed().show();
        }
        else {
            ConnectionFailed().show();
        }
    }

    private AlertDialog ImageFailed() {
        AlertDialog alertDialog = new AlertDialog.Builder(_context, R.style.AlertDialogCustom)
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