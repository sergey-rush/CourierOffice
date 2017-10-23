package ru.courier.office.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.Application;
import ru.courier.office.core.Document;
import ru.courier.office.core.DocumentAdapter;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.WebContext;

public class DocumentFragment extends Fragment {

    private static final String ARG_APPLICATION_ID = "applicationId";
    private int _applicationId;

    private WebContext _webContext;
    private ListView _listView;
    private Context _context;
    private View _view;

    public DocumentFragment() {
    }

    public static DocumentFragment newInstance(int applicationId) {
        DocumentFragment fragment = new DocumentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_APPLICATION_ID, applicationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _applicationId = getArguments().getInt(ARG_APPLICATION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_document, container, false);
        _context = getContext();

        _webContext = WebContext.getInstance();
        DocumentAsyncTask documentAsyncTask = new DocumentAsyncTask();
        documentAsyncTask.execute();
        return _view;
    }

    private void loadDataCallback() {
        if (_webContext.Application.DocumentList.size() > 0) {

            DocumentAdapter adapter = new DocumentAdapter(_context, _webContext.Application.DocumentList);

            _listView = (ListView) _view.findViewById(R.id.lvDocuments);
            _listView.setItemsCanFocus(false);
            _listView.setAdapter(adapter);

            _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                    int documentId = Integer.parseInt(tvTitle.getTag().toString());
                    TextView tvCount = (TextView) view.findViewById(R.id.tvCount);
                    int count = Integer.parseInt(tvCount.getText().toString());

                    if (count > 0) {
                        _webContext.SelectedDocumentId = documentId;
                        ScanListFragment scanListFragment = ScanListFragment.newInstance(documentId);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.container, scanListFragment).commit();
                    } else {
                        showNoScansDialog(documentId);
                    }
                }
            });
        }
    }

    private void showNoScansDialog(final int documentId) {
        AlertDialog dialog = new AlertDialog.Builder(_context, R.style.AlertDialogCustom)
                .setTitle(R.string.warning)
                .setMessage(R.string.document_has_no_scans)
                .setIcon(R.drawable.ic_question)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        TakePhotoFragment fragment = TakePhotoFragment.newInstance(_applicationId, documentId, 0);
                        showFragment(fragment);
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void showFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = ((AppCompatActivity) _context).getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    private class DocumentAsyncTask extends AsyncTask<Void, Void, Void> {
        private List<Document> _documents;

        private DocumentAsyncTask() {
        }

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
            _documents = dataAccess.getDocumentsByApplicationId(_applicationId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (_documents.size() > 0) {
                _webContext.Application.DocumentList = _documents;
                loadDataCallback();
            }
        }
    }
}