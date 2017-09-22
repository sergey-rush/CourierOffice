package ru.courier.office.views;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.Status;
import ru.courier.office.core.StatusAdapter;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.WebContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    private static final String ARG_APPLICATION_ID = "applicationId";
    private int _applicationId;

    private WebContext _webContext;
    private ListView _listView;
    private Context _context;
    private View _view;

    public StatusFragment() {}

    public static StatusFragment newInstance(int applicationId) {
        StatusFragment fragment = new StatusFragment();
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
        _view = inflater.inflate(R.layout.fragment_status, container, false);
        _context = getContext();
        _webContext = WebContext.getInstance();

        StatusAsyncTask statusAsyncTask = new StatusAsyncTask();
        statusAsyncTask.execute();

        return _view;
    }

    private void loadDataCallback() {

        if (_webContext.Application.StatusList.size() > 0) {

            List<Status> statusList = _webContext.Application.StatusList;
            StatusAdapter adapter = new StatusAdapter(_context, statusList);

            _listView = (ListView) _view.findViewById(R.id.lvStatuses);
            _listView.setAdapter(adapter);

            _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    RelativeLayout layout = (RelativeLayout) view;
                    TextView tvInfo = (TextView) layout.findViewById(R.id.tvInfo);
                    Toast.makeText(_context, tvInfo.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class StatusAsyncTask extends AsyncTask<Void, Void, Void> {
        private StatusAsyncTask() {
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
            _webContext.Application.StatusList = dataAccess.getStatusesByApplicationId(_applicationId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (_webContext.Application.StatusList != null) {
                loadDataCallback();
            }
        }
    }
}
