package ru.courier.office.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _view = inflater.inflate(R.layout.scan_view_item, container, false);

        Button btnClose = (Button) _view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanListFragment scanListFragment = ScanListFragment.newInstance(_documentId);
                FragmentManager fragmentManager = ((AppCompatActivity) _context).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, scanListFragment).commit();
            }
        });

        Scan scan = _scanList.get(position);

        final TouchImageView imgDisplay = (TouchImageView) _view.findViewById(R.id.imgDisplay);
        ScanAsyncTask scanAsyncTask = new ScanAsyncTask(scan, imgDisplay);
        scanAsyncTask.execute();

        container.addView(_view);

        return _view;
    }

    private void loadDataCallback(Scan scan, TouchImageView imgDisplay) {

        if (scan.LargePhoto != null) {

            int reqWidth = imgDisplay.viewWidth;
            int reqHeight = imgDisplay.viewHeight;

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(scan.LargePhoto, 0, scan.LargePhoto.length, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;
            final Bitmap bitmap = BitmapFactory.decodeByteArray(scan.LargePhoto, 0, scan.LargePhoto.length, options);

            imgDisplay.setImageBitmap(bitmap);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    private class ScanAsyncTask extends AsyncTask<Void, Void, Void> {

        private Scan _scan;

        private TouchImageView _imgDisplay;

        private ScanAsyncTask(Scan scan, TouchImageView imgDisplay) {
            _scan = scan;
            _imgDisplay = imgDisplay;
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
                System.arraycopy(buffer, 0, imageBytes, offset - 1, buffer.length);
                offset = (offset + bufferLength);
            }
            _scan.LargePhoto = imageBytes;

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loadDataCallback(_scan, _imgDisplay);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}