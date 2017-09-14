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

import ru.courier.office.R;
import ru.courier.office.core.Application;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.ScanManager;
import ru.courier.office.web.WebContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicationFragment extends Fragment {

    private Application _application;
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
        _application = webContext.Application;

        tvId = (TextView) view.findViewById(R.id.tvId);
        tvId.setText(_application.ApplicationGuid);
        tvMerchantName = (TextView) view.findViewById(R.id.tvMerchantName);
        tvMerchantName.setText(_application.MerchantName);
        tvPersonName = (TextView) view.findViewById(R.id.tvPersonName);
        tvPersonName.setText(_application.PersonName);
        tvAmount = (TextView) view.findViewById(R.id.tvAmount);
        tvAmount.setText(_application.Amount);
        tvDeliveryAddress = (TextView) view.findViewById(R.id.tvDeliveryAddress);
        tvDeliveryAddress.setText(_application.DeliveryAddress);
        tvCreated = (TextView) view.findViewById(R.id.tvCreated);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        tvCreated.setText(dateFormat.format(_application.Created));

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


    private void submitApplication() {
        ScanManager scanManager = new ScanManager(getContext(), _application);
        scanManager.execute();
    }


    private AlertDialog onDeleteDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom)
                .setTitle("Удаление заявки")
                .setMessage("Вы действительно желаете удалить эту заявку?")
                .setIcon(R.drawable.ic_question)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DataAccess dataAccess = DataAccess.getInstance(getContext());
                        dataAccess.removeApplication(_application.Id);
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
