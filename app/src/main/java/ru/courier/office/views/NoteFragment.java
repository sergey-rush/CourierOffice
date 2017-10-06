package ru.courier.office.views;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.Note;
import ru.courier.office.core.NoteAdapter;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.NoteProvider;
import ru.courier.office.web.WebContext;

public class NoteFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public NoteFragment() { }

    private Context _context;
    private Toolbar toolbar;
    private WebContext _webContext;
    private DataAccess _dataAccess;
    private View view;
    private ListView _listView;
    private NoteAdapter adapter;
    public SwipeRefreshLayout _srlMain;
    public List<Note> _noteList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_note, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.tlbMain);
        toolbar.setTitle(getString(R.string.title_home_fragment));

        _context = getContext();
        _webContext = WebContext.getInstance();
        _dataAccess = DataAccess.getInstance(_context);
        _listView = (ListView) view.findViewById(R.id.lvNotes);
        _srlMain = (SwipeRefreshLayout) view.findViewById(R.id.srlMain);
        _srlMain.setColorSchemeResources(R.color.colorPrimaryDark);

        loadDataCallback();

        _srlMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNotes();
            }
        });

        refreshNotes();

        return view;
    }

    private void refreshNotes()
    {
        NoteAsyncTask noteAsyncTask = new NoteAsyncTask();
        noteAsyncTask.execute();
    }


    private void loadDataCallback() {
        _noteList = _dataAccess.getNotesByLimit(100);
        adapter = new NoteAdapter(_context, _noteList);
        _listView.setAdapter(adapter);
    }

    private class NoteAsyncTask extends AsyncTask<Void, Void, Void> {

        private int responseCode;

        public NoteAsyncTask() {  }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            int maxId = _dataAccess.getNoteMaxId();
            String postData = String.format("{\"Id\":\"%d\", \"Imei\":\"%s\"}", maxId, _webContext.Imei);
            NoteProvider noteProvider = new NoteProvider();
            responseCode = noteProvider.getNotes(postData);
            if (responseCode == 200) {
                _dataAccess.addNotes(_webContext.NoteList);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            _srlMain.setRefreshing(false);

            if (responseCode == 200) {
                loadDataCallback();
            }
        }
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
        void onFragmentInteraction(Uri uri);
    }
}
