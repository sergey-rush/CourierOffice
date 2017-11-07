package ru.courier.office.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import ru.courier.office.R;
import ru.courier.office.data.DataAccess;
import ru.courier.office.views.ScanListFragment;

/**
 * Created by rash on 04.09.2017.
 */

public class ScanViewAdapter extends PagerAdapter {

    private Context _context;
    private byte[] _imageBytes;
    private List<Scan> _scanList;
    private LayoutInflater _inflater;
    private int _documentId;
    private View _view;

    public ScanViewAdapter(Context context, int documentId, List<Scan> scanList) {
        _context = context;
        _documentId = documentId;
        _scanList = scanList;
    }

    @Override
    public int getCount() {
        return _scanList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _view = _inflater.inflate(R.layout.scan_view_item, container, false);

        Button btnClose = (Button) _view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanListFragment scanListFragment = ScanListFragment.newInstance(_documentId);
                FragmentManager fragmentManager = ((AppCompatActivity)_context).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, scanListFragment).commit();
            }
        });

        Scan scan = _scanList.get(position);
        ScanAsyncTask scanAsyncTask = new ScanAsyncTask(scan);
        scanAsyncTask.execute();

        container.addView(_view);

        return _view;
    }

    private void loadDataCallback() {
        if (_imageBytes != null) {

            TouchImageView imgDisplay = (TouchImageView) _view.findViewById(R.id.imgDisplay);
            int reqWidth = imgDisplay.viewWidth;
            int reqHeight = imgDisplay.viewHeight;

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(_imageBytes, 0, _imageBytes.length, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            //options.inSampleSize = 10;
            options.inJustDecodeBounds = false;
            final Bitmap bitmap = BitmapFactory.decodeByteArray(_imageBytes, 0, _imageBytes.length, options);

            imgDisplay.setImageBitmap(bitmap);
        }
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height/2;
            final int halfWidth = width/2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    private class ScanAsyncTask extends AsyncTask<Void, Void, Void> {

        private Scan _scan;
        private ScanAsyncTask(Scan scan) {

            _scan = scan;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            DataAccess dataAccess = DataAccess.getInstance(_context);

            int scanImageLength = _scan.ImageLength;
            byte[] imageBytes = new byte[scanImageLength];
            int offset = 1;
            int bufferSize = 1 * 1024 * 1024;

            if (scanImageLength < bufferSize) {
                bufferSize = scanImageLength;
            }

            while (offset < scanImageLength) {

                if (offset + bufferSize > scanImageLength) {
                    bufferSize = (scanImageLength - offset);
                }

                byte[] buffer = dataAccess.getScanImage(_scan.Id, offset, bufferSize);
                int bufferLength = buffer.length;
                System.arraycopy(buffer, 0, imageBytes, offset -1, buffer.length);
                offset = (offset + bufferLength);
            }

            _imageBytes = imageBytes;

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loadDataCallback();
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}