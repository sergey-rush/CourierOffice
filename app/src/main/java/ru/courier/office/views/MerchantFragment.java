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
import ru.courier.office.core.Merchant;
import ru.courier.office.web.WebContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class MerchantFragment extends Fragment {

    TextView tvMerchantId;
    TextView tvMerchantName;
    TextView tvInn;
    TextView tvEmail;
    TextView tvSite;
    TextView tvManagerName;
    TextView tvManagerPhone;

    public MerchantFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_merchant, container, false);

        WebContext webContext = WebContext.getInstance();
        Merchant merchant = webContext.Application.Merchant;

        tvMerchantId = (TextView) view.findViewById(R.id.tvMerchantId);
        tvMerchantId.setText(merchant.MerchantId);

        tvMerchantName = (TextView) view.findViewById(R.id.tvMerchantName);
        tvMerchantName.setText(merchant.Name);

        tvInn = (TextView) view.findViewById(R.id.tvInn);
        tvInn.setText(merchant.Inn);

        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        tvEmail.setText(merchant.Email);

        tvSite = (TextView) view.findViewById(R.id.tvSite);
        tvSite.setText(merchant.Site);

        tvManagerName = (TextView) view.findViewById(R.id.tvManagerName);
        tvManagerName.setText(merchant.ManagerName);

        tvManagerPhone = (TextView) view.findViewById(R.id.tvManagerPhone);
        tvManagerPhone.setText(merchant.ManagerPhone);

        return view;
    }

}
