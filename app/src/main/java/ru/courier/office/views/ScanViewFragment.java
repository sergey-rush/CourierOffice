package ru.courier.office.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.Scan;
import ru.courier.office.core.ScanViewAdapter;
import ru.courier.office.data.DataAccess;

public class ScanViewFragment extends Fragment {

    private static final String ARG_DOCUMRNT_ID = "documentId";
    private static final String ARG_SCAN_ID = "scanId";

    private int _documentId;
    private int _scanId;
    private Context _context;
    private ScanViewAdapter adapter;
    private ViewPager viewPager;
    private List<Scan> _scanList;
    private View _view;

    public ScanViewFragment() { }

    public static ScanViewFragment newInstance(int documentId, int scanId) {
        ScanViewFragment fragment = new ScanViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DOCUMRNT_ID, documentId);
        args.putInt(ARG_SCAN_ID, scanId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _documentId = getArguments().getInt(ARG_DOCUMRNT_ID);
            _scanId = getArguments().getInt(ARG_SCAN_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _view = inflater.inflate(R.layout.fragment_scan_view, container, false);
        _context = getContext();
        ScanAsyncTask scanAsyncTask = new ScanAsyncTask();
        scanAsyncTask.execute();
        return _view;
    }

    private void loadDataCallback() {
        if (_scanList.size() > 0) {
            viewPager = (ViewPager) _view.findViewById(R.id.pager);
            //Intent i = getActivity().getIntent();
            //int position = i.getIntExtra("position", 0);
            adapter = new ScanViewAdapter(_context, _documentId, _scanList);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(getIndex());
        }
    }

    private int getIndex()
    {
        int index = 0;
        for(Scan scan: _scanList){
            if(scan.Id == _scanId)
            {
                break;
            }
            index++;
        }
        return index;
    }

    private class ScanAsyncTask extends AsyncTask<Void, Void, Void> {
        private ScanAsyncTask() {}
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(_context);
            pDialog.setMessage("Пожалуйста, подождите...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            DataAccess dataAccess = DataAccess.getInstance(_context);
            _scanList = dataAccess.getScansByDocumentId(_documentId);

            for (Scan scan : _scanList) {

                int scanImageLength = scan.ImageLength;
                byte[] imageBytes = new byte[scanImageLength];

                // The bytes have already been read
                int totalBytes = 0;
                int bufferSize = 1048576;

                if (scanImageLength < bufferSize) {
                    bufferSize = scanImageLength;
                }

                while (totalBytes < scanImageLength) {

                    if (totalBytes + bufferSize > scanImageLength) {
                        bufferSize = scanImageLength - totalBytes;
                    }

                    byte[] buffer = new byte[bufferSize];
                    buffer = dataAccess.getScanImage(scan.Id, totalBytes, bufferSize);

                    System.arraycopy(buffer, 0, imageBytes, totalBytes, buffer.length);
                    totalBytes = (totalBytes + bufferSize);
                }

                scan.LargePhoto = imageBytes;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (_scanList != null) {
                loadDataCallback();
            }
        }
    }
}
