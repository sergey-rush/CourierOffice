package ru.courier.office.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.Note;
import ru.courier.office.core.NoteAdapter;
import ru.courier.office.core.Status;
import ru.courier.office.core.StatusAdapter;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.NoteManager;
import ru.courier.office.web.WebContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    private Toolbar toolbar;
    private WebContext webContext;
    private DataAccess dataAccess;
    private View view;
    private ListView listView;
    private NoteAdapter adapter;
    public SwipeRefreshLayout srlMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_home_fragment));

        webContext = WebContext.getInstance();
        dataAccess = DataAccess.getInstance(view.getContext());
        webContext.Notes = dataAccess.getNotesByLimit(10);

        if (webContext.Notes != null) {

            listView = (ListView) view.findViewById(R.id.lvNotes);
            adapter = new NoteAdapter(getContext(), webContext.Notes);
            listView.setAdapter(adapter);

            srlMain = (SwipeRefreshLayout) view.findViewById(R.id.srlMain);
            srlMain.setColorSchemeResources(R.color.colorPrimaryDark);

            srlMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    onRefreshingNotes();
                }
            });
        }

        return view;
    }


private void onRefreshingNotes() {
    int maxId = dataAccess.getNoteMaxId();
    NoteManager noteManager = new NoteManager(this, maxId);
    noteManager.execute();
}

public void onRefreshedNotes(boolean toBeUpdated)
{
    if(toBeUpdated)
    {
        webContext.Notes = dataAccess.getNotesByLimit(10);
        adapter = new NoteAdapter(getContext(), webContext.Notes);
        listView.setAdapter(adapter);
    }

    srlMain.setRefreshing(false);
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
