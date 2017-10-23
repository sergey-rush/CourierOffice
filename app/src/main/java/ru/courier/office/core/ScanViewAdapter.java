package ru.courier.office.core;

import android.app.ProgressDialog;
import android.content.Context;
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
    private List<Scan> _scanList;
    private LayoutInflater _inflater;
    private int _documentId;

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
        View view = _inflater.inflate(R.layout.scan_view_item, container, false);

        Button btnClose = (Button) view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanListFragment scanListFragment = ScanListFragment.newInstance(_documentId);
                FragmentManager fragmentManager = ((AppCompatActivity)_context).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, scanListFragment).commit();
            }
        });

        Scan scan = _scanList.get(position);
        ScanAsyncTask scanAsyncTask = new ScanAsyncTask(view, scan);
        scanAsyncTask.execute();

        //byte[] imageBytes = scan.SmallPhoto;

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        options.inSampleSize = 1;
//
//        final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
//        TouchImageView imgDisplay = (TouchImageView) view.findViewById(R.id.imgDisplay);
//        imgDisplay.setImageBitmap(bitmap);

        container.addView(view);

        return view;
    }

    private class ScanAsyncTask extends AsyncTask<Void, Void, Void> {

        private View _view;
        private Scan _scan;
        private byte[] _imageBytes;

        private ScanAsyncTask(View view, Scan scan) {
            _view = view;
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
            int bufferSize = 1048576;

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

            if (_imageBytes != null) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inSampleSize = 1;

                final Bitmap bitmap = BitmapFactory.decodeByteArray(_imageBytes, 0, _imageBytes.length, options);
                TouchImageView imgDisplay = (TouchImageView) _view.findViewById(R.id.imgDisplay);
                imgDisplay.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}