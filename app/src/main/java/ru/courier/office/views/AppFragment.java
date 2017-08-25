package ru.courier.office.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ru.courier.office.R;
import ru.courier.office.core.Application;
import ru.courier.office.web.WebContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppFragment extends Fragment {

    TextView tvId;
    TextView tvMerchantId;
    TextView tvPersonId;
    TextView tvAmount;
    TextView tvDeliveryAddress;
    TextView tvCreated;

    public AppFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app, container, false);

        WebContext webContext = WebContext.getInstance();
        Application application = webContext.Application;

        tvId = (TextView) view.findViewById(R.id.tvId);
        tvId.setText(application.Id);
        tvMerchantId = (TextView) view.findViewById(R.id.tvMerchantId);
        tvMerchantId.setText(application.MerchantId);
        tvPersonId = (TextView) view.findViewById(R.id.tvPersonId);
        tvPersonId.setText(application.PersonId);
        tvAmount = (TextView) view.findViewById(R.id.tvAmount);
        tvAmount.setText(application.Amount);
        tvDeliveryAddress = (TextView) view.findViewById(R.id.tvDeliveryAddress);
        tvDeliveryAddress.setText(application.DeliveryAddress);
        tvCreated = (TextView) view.findViewById(R.id.tvCreated);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        tvCreated.setText(dateFormat.format(application.Created));

        return view;
    }

}
