package ru.courier.office.views;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ru.courier.office.R;
import ru.courier.office.core.Application;
import ru.courier.office.core.ScanStatus;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.ScanManager;
import ru.courier.office.web.WebContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicationFragment extends Fragment {

    private static final String ARG_APPLICATION_ID = "applicationId";
    private int _applicationId;

    private ImageView ivMenu;
    private TextView tvId;
    private TextView tvMerchantName;
    private TextView tvPersonName;
    private TextView tvAmount;
    private TextView tvDeliveryAddress;
    private TextView tvCreated;
    private View _view;
    private Context _context;
    private WebContext _webContext;
    private DataAccess _dataAccess;

    public ApplicationFragment() {}

    public static ApplicationFragment newInstance(int applicationId) {
        ApplicationFragment fragment = new ApplicationFragment();
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

        _view = inflater.inflate(R.layout.fragment_application, container, false);
        _context = getContext();
        _webContext = WebContext.getInstance();
        _dataAccess = DataAccess.getInstance(_context);

        ApplicationAsyncTask applicationAsyncTask = new ApplicationAsyncTask();
        applicationAsyncTask.execute();

        return _view;
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(_context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.app_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new AppMenuItemClickListener());
        popup.show();
    }

    class AppMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public AppMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.app_menu_scan:
                    scanDocuments();
                    return true;
                case R.id.app_menu_send:
                    sendApplication();
                    return true;
                case R.id.app_menu_delete:
                    AlertDialog dialog = onDeleteDialog();
                    dialog.show();
                    return true;
                default:
            }
            return false;
        }
    }

    private void scanDocuments() {

        TakePhotoFragment fragment = TakePhotoFragment.newInstance(_applicationId, 0, 0);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    private void sendApplication() {

        ScanManager scanManager = new ScanManager(_context, _webContext.Application);
        scanManager.execute();
    }

    private AlertDialog onDeleteDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(_context, R.style.AlertDialogCustom)
                .setTitle("Удаление заявки")
                .setMessage("Вы действительно желаете удалить эту заявку?")
                .setIcon(R.drawable.ic_question)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        _dataAccess.removeApplication(_applicationId);
                        Toast.makeText(_context, "Удалено", Toast.LENGTH_SHORT).show();

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        AppListFragment appListFragment = new AppListFragment();
                        ft.replace(R.id.container, appListFragment);
                        ft.commit();

                        dialog.dismiss();
                    }

                }).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).create();
        return alertDialog;
    }

    private void loadDataCallback()
    {
        Application application = _webContext.Application;

        tvId = (TextView) _view.findViewById(R.id.tvId);
        tvId.setText(application.ApplicationGuid);
        tvMerchantName = (TextView) _view.findViewById(R.id.tvMerchantName);
        tvMerchantName.setText(application.MerchantName);
        tvPersonName = (TextView) _view.findViewById(R.id.tvPersonName);
        tvPersonName.setText(application.PersonName);
        tvAmount = (TextView) _view.findViewById(R.id.tvAmount);
        tvAmount.setText(application.Amount);
        tvDeliveryAddress = (TextView) _view.findViewById(R.id.tvDeliveryAddress);
        tvDeliveryAddress.setText(application.DeliveryAddress);
        tvCreated = (TextView) _view.findViewById(R.id.tvCreated);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        tvCreated.setText(dateFormat.format(application.Created));

        ivMenu = (ImageView) _view.findViewById(R.id.ivMenu);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
    }

    private class ApplicationAsyncTask extends AsyncTask<Void, Void, Void> {

        private ApplicationAsyncTask() {}

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
            _webContext.Application = _dataAccess.getApplicationById(_applicationId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (_webContext.Application != null) {
                loadDataCallback();
            }
        }
    }
}
