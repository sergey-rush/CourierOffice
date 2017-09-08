package ru.courier.office.views;


import android.content.DialogInterface;
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
import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.Application;
import ru.courier.office.core.Document;
import ru.courier.office.core.Scan;
import ru.courier.office.core.ScanStatus;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.ScanManager;
import ru.courier.office.web.UploadManager;
import ru.courier.office.web.WebContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicationFragment extends Fragment {

    private Application application;
    private ImageView ivMenu;
    private TextView tvId;
    private TextView tvMerchantName;
    private TextView tvPersonName;
    private TextView tvAmount;
    private TextView tvDeliveryAddress;
    private TextView tvCreated;

    public ApplicationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_application, container, false);

        WebContext webContext = WebContext.getInstance();
        application = webContext.Application;

        tvId = (TextView) view.findViewById(R.id.tvId);
        tvId.setText(application.ApplicationGuid);
        tvMerchantName = (TextView) view.findViewById(R.id.tvMerchantName);
        tvMerchantName.setText(application.MerchantName);
        tvPersonName = (TextView) view.findViewById(R.id.tvPersonName);
        tvPersonName.setText(application.PersonName);
        tvAmount = (TextView) view.findViewById(R.id.tvAmount);
        tvAmount.setText(application.Amount);
        tvDeliveryAddress = (TextView) view.findViewById(R.id.tvDeliveryAddress);
        tvDeliveryAddress.setText(application.DeliveryAddress);
        tvCreated = (TextView) view.findViewById(R.id.tvCreated);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        tvCreated.setText(dateFormat.format(application.Created));

        ivMenu = (ImageView) view.findViewById(R.id.ivMenu);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        return view;
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(getContext(), view);
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
                case R.id.app_menu_submit:
                    submitApplication();
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
    private DataAccess dataAccess;

    private void submitApplication() {

        dataAccess = DataAccess.getInstance(getContext());

        application.DocumentList = dataAccess.getDocumentsByApplicationGuid(application.ApplicationGuid);

        for (Document document : application.DocumentList) {
            List<Scan> scans = dataAccess.getScansByDocumentId(document.Id);
            for (Scan scan : scans) {
                ScanStatus scanStatus = scan.ScanStatus;
                ScanManager scanManager = new ScanManager(this, document, scan);
                scanManager.execute();
                break;
            }
        }
    }

     public void onScanRetrieved(Scan scan)
     {
         dataAccess.updateScan(scan);
     }

    private AlertDialog onDeleteDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom)
                .setTitle("Удаление заявки")
                .setMessage("Вы действительно желаете удалить эту заявку?")
                .setIcon(R.drawable.ic_question)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DataAccess dataAccess = DataAccess.getInstance(getContext());
                        dataAccess.removeApplication(application.Id);
                        Toast.makeText(getContext(), "Удалено", Toast.LENGTH_SHORT).show();

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        HomeFragment homeFragment = new HomeFragment();
                        ft.replace(R.id.container, homeFragment);
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
}
