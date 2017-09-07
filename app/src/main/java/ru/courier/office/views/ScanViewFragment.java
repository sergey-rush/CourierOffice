package ru.courier.office.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.Scan;
import ru.courier.office.core.ScanViewAdapter;
import ru.courier.office.data.DataAccess;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScanViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScanViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanViewFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DOCUMRNT_ID = "documentId";
    private static final String ARG_SCAN_ID = "scanId";

    private int documentId;
    private int scanId;

    private OnFragmentInteractionListener mListener;

    public ScanViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param documentId Parameter ScanId.
     * @param scanId Parameter Title.
     * @return A new instance of fragment ScanViewFragment.
     */
    public static ScanViewFragment newInstance(int documentId, int scanId) {
        ScanViewFragment fragment = new ScanViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DOCUMRNT_ID, documentId);
        args.putInt(ARG_SCAN_ID, scanId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            documentId = getArguments().getInt(ARG_DOCUMRNT_ID);
            scanId = getArguments().getInt(ARG_SCAN_ID);
        }
    }

    private ScanViewAdapter adapter;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scan_view, container, false);
        List<Scan> scanList = null;
        try {
            scanList = getCurrentScan();
        } catch (IOException e) {
            e.printStackTrace();
        }

        viewPager = (ViewPager) view.findViewById(R.id.pager);

        Intent i = getActivity().getIntent();
        int position = i.getIntExtra("position", 0);

        adapter = new ScanViewAdapter(getContext(), scanList);

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(position);

        return view;
    }

    private List<Scan> getCurrentScan() throws IOException {
        DataAccess dataAccess = DataAccess.getInstance(getContext());
        List<Scan> scanList = dataAccess.getScansByDocumentId(documentId);

        //Scan scan = dataAccess.getScanById(scanId);

        for (Scan scan : scanList) {

            int scanImageLength = scan.ImageLength;
            byte[] imageBytes = new byte[scanImageLength];

            // The bytes have already been read
            int totalBytes = 0;
            int bufferSize = 1048576;

            if (scanImageLength < bufferSize) {
                bufferSize = scanImageLength;
            }

            while (totalBytes < scanImageLength) {

                if (totalBytes + bufferSize > scanImageLength) {
                    bufferSize = scanImageLength - totalBytes;
                }

                byte[] buffer = new byte[bufferSize];
                buffer = dataAccess.getScanImage(scanId, totalBytes, bufferSize);

                System.arraycopy(buffer, 0, imageBytes, totalBytes, buffer.length);
                totalBytes = (totalBytes + bufferSize);
            }

            scan.LargePhoto = imageBytes;
        }

        return  scanList;
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
