package ru.courier.office.views;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
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
import ru.courier.office.data.DataAccess;

public class ItemFragment extends Fragment {

    private static final String ARG_PARAM1 = "appId";
    private String applicationId;

    private OnFragmentInteractionListener mListener;

    public ItemFragment() {
    }

    public static ItemFragment newInstance(String appId) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, appId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            applicationId = getArguments().getString(ARG_PARAM1);
        }
    }

    TextView tvId;
    TextView tvMerchantId;
    TextView tvPersonId;
    TextView tvAmount;
    TextView tvDeliveryAddress;
    TextView tvCreated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aplication, container, false);

        DataAccess dataAccess = DataAccess.getInstance(view.getContext());
        Application application = dataAccess.getApplicationById(applicationId);

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

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
