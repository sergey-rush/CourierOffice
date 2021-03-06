package ru.courier.office.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.Document;
import ru.courier.office.core.GridSpacingItemDecoration;
import ru.courier.office.core.Scan;
import ru.courier.office.core.ScanAdapter;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.WebContext;


public class ScanListFragment extends Fragment {
    
    private static final String ARG_DOCUMENT_ID = "documentId";
   
    private int _documentId;
    private Document _document;
    private Toolbar _toolbar;
    private WebContext _webContext;
    private DataAccess _dataAccess;
    private RecyclerView _listView;
    private ScanAdapter adapter;
    private List<Scan> _scanList;
    private Context _context;
    private View _view;

    public ScanListFragment() {}
   
    public static ScanListFragment newInstance(int documentId) {
        ScanListFragment fragment = new ScanListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DOCUMENT_ID, documentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _documentId = getArguments().getInt(ARG_DOCUMENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _view = inflater.inflate(R.layout.fragment_scan_list, container, false);

        _toolbar = (Toolbar) getActivity().findViewById(R.id.tlbMain);
        _toolbar.setVisibility(View.VISIBLE);


        _context = getContext();
        _webContext = WebContext.getInstance();
        _dataAccess = DataAccess.getInstance(_context);

        ScanAsyncTask scanAsyncTask = new ScanAsyncTask();
        scanAsyncTask.execute();

        return _view;
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    private void loadDataCallback() {

        _toolbar.setTitle(_document.Title);

        if (_scanList.size() > 0) {
            _listView = (RecyclerView) _view.findViewById(R.id.rvScans);
            adapter = new ScanAdapter(_context, _documentId,_scanList);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(_context, 2);
            _listView.setLayoutManager(mLayoutManager);
            _listView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
            _listView.setItemAnimator(new DefaultItemAnimator());
            _listView.setAdapter(adapter);
        }
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
            _document = _dataAccess.getDocumentById(_documentId);
            _scanList = _dataAccess.getScansByDocumentId(_documentId);
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
