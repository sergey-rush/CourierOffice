package ru.courier.office.views;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import ru.courier.office.R;
import ru.courier.office.core.Scan;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.ImageManager;
import ru.courier.office.web.LoginManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_upload, container, false);

        Button btnUpload = (Button) view.findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnUpload:
                onButtonUploadClick(view);
                break;
        }
    }

    public void onButtonUploadClick(View view) {

        //Toast.makeText(getContext(), "onButtonUploadClick", Toast.LENGTH_SHORT).show();

        DataAccess dataAccess = DataAccess.getInstance(getContext());
        Scan scan = dataAccess.getScanById(23);

        int imageLength = scan.ImageLength;
        int sendBytes = 0;
        int bundle = 1024 * 1024;
        int counter = 1;

        while(sendBytes < imageLength) {

            int bufferLength = counter * bundle;
            if (bufferLength > imageLength - sendBytes) {
                bufferLength = imageLength - sendBytes;
            }

            byte[] imageBytes = dataAccess.getScanImage(scan.Id, sendBytes, bufferLength);
            int imageBytesLength = imageBytes.length;
            ImageManager imageManager = new ImageManager(getContext(), scan, imageBytes);
            final AsyncTask<Void, Void, Void> execute = imageManager.execute();
            sendBytes = sendBytes + bufferLength;
            Log.d("UPD", String.format("Counter: %s SendBytes: %s BufferLength: %s", Integer.toString(counter), Integer.toString(sendBytes), Integer.toString(bufferLength)));
            counter++;
        }

        Log.d("UPD", String.format("SendBytes: %s", Integer.toString(sendBytes)));
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
