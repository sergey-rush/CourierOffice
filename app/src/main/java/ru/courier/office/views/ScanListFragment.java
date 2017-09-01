package ru.courier.office.views;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.GridSpacingItemDecoration;
import ru.courier.office.core.Scan;
import ru.courier.office.core.ScanAdapter;
import ru.courier.office.data.DataAccess;


public class ScanListFragment extends Fragment {
    
    private static final String ARG_DOCUMENT_ID = "documentId";
    private static final String ARG_TITLE = "title";
   
    private int documentId;
    private String title;

    public ScanListFragment() {        
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param documentId DocumentId
     * @param title Title
     * @return A new instance of fragment ScanListFragment.
     */    
    public static ScanListFragment newInstance(int documentId, String title) {
        ScanListFragment fragment = new ScanListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DOCUMENT_ID, documentId);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            documentId = getArguments().getInt(ARG_DOCUMENT_ID);
            title = getArguments().getString(ARG_TITLE);
        }
    }

    private RecyclerView listView;
    private ScanAdapter adapter;
    private List<Scan> scanList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scan_list, container, false);

        listView = (RecyclerView) view.findViewById(R.id.rvScans);

        scanList = DataAccess.getInstance(getContext()).getScansByDocumentId(documentId);
        adapter = new ScanAdapter(getContext(), scanList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        listView.setLayoutManager(mLayoutManager);
        listView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(adapter);

        listView.setOnTouchListener(new AdapterView.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                String title = tvTitle.getText().toString();
                int scanId = Integer.parseInt(tvTitle.getTag().toString());

                Toast.makeText(getContext(), String.format("Title: %s ScanId: %s", title, scanId), Toast.LENGTH_SHORT).show();

                return false;

            }
        });

        return view;
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
