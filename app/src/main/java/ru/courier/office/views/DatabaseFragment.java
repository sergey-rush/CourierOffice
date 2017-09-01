package ru.courier.office.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ru.courier.office.R;
import ru.courier.office.core.Status;
import ru.courier.office.data.DataAccess;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DatabaseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DatabaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatabaseFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DatabaseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DatabaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DatabaseFragment newInstance(String param1, String param2) {
        DatabaseFragment fragment = new DatabaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private DataAccess dataAccess;
    private Button btnCreateDatabase;
    private Button btnSetupDatabase;
    private Button btnDropDatabase;
    private TextView tvInfo;
    private TextView tvApps;
    private TextView tvDocuments;
    private TextView tvScans;
    private TextView tvStatuses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_database, container, false);

        btnCreateDatabase = (Button) view.findViewById(R.id.btnCreateDatabase);
        btnCreateDatabase.setOnClickListener(this);

        btnSetupDatabase = (Button) view.findViewById(R.id.btnSetupDatabase);
        btnSetupDatabase.setOnClickListener(this);

        btnDropDatabase = (Button) view.findViewById(R.id.btnDropDatabase);
        btnDropDatabase.setOnClickListener(this);

        tvInfo = (TextView) view.findViewById(R.id.tvInfo);
        tvApps = (TextView) view.findViewById(R.id.tvApps);

        tvDocuments = (TextView) view.findViewById(R.id.tvDocuments);
        tvScans = (TextView) view.findViewById(R.id.tvScans);
        tvStatuses = (TextView) view.findViewById(R.id.tvStatuses);

        dataAccess = DataAccess.getInstance(getContext());
        getStatus ();

        return view;
    }

    private void getStatus () {
        Status status = dataAccess.getVersion();
        tvInfo.setText(String.format("%s \nVersion: %s \nCreated: %s", status.Info, status.Id, status.Created));
        long countApplications = dataAccess.countApplications();
        tvApps.setText(String.format("Applications count: %s", countApplications));
        long countDocuments = dataAccess.countDocuments();
        tvDocuments.setText(String.format("Documents count: %s", countDocuments));
        long countScans = dataAccess.countScans();
        tvScans.setText(String.format("Scans count: %s", countScans));
        long countStatuses = dataAccess.countStatuses();
        tvStatuses.setText(String.format("Statuses count: %s", countStatuses));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreateDatabase:
                onButtonCreateDatabaseClick(view);
                break;
            case R.id.btnSetupDatabase:
                onButtonSetupDatabaseClick(view);
                break;
            case R.id.btnDropDatabase:
                onButtonDropDatabaseClick(view);
                break;
        }
    }

    public void onButtonCreateDatabaseClick(View view) {
        dataAccess.onInit();
        getStatus ();
    }

    public void onButtonSetupDatabaseClick(View view) {
        dataAccess.onSetup();
        getStatus ();
    }

    public void onButtonDropDatabaseClick(View view) {
        dataAccess.onDrop();
        getStatus ();
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
