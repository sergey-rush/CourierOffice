package ru.courier.office.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ru.courier.office.R;
import ru.courier.office.core.Application;
import ru.courier.office.core.ApplicationAdapter;
import ru.courier.office.core.ApplicationStatus;
import ru.courier.office.core.DividerItemDecoration;
import ru.courier.office.core.RecyclerTouchListener;
import ru.courier.office.data.DataAccess;

import java.util.List;

public class AppListFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;
    private Toolbar _toolbar;
    private Context _context;

    public AppListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private RecyclerView recyclerView;
    private ApplicationAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_applist, container, false);

        _context = getContext();
        _toolbar = (Toolbar) getActivity().findViewById(R.id.tlbMain);
        _toolbar.setTitle(getString(R.string.title_applist_fragment));

        List<Application> applications = DataAccess.getInstance(_context).getApplications(100);

        adapter = new ApplicationAdapter(applications);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(_context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(_context, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(_context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                ImageView ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
                ApplicationStatus status = (ApplicationStatus) ivStatus.getTag();
                if (status == ApplicationStatus.None) {

                    TextView tvPersonName = (TextView) view.findViewById(R.id.tvPersonName);
                    int applicationId = (int) tvPersonName.getTag();

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    AppViewFragment appViewFragment = AppViewFragment.newInstance(applicationId, 0);
                    ft.replace(R.id.container, appViewFragment);
                    ft.commit();
                } else {
                    Toast.makeText(_context, R.string.application_in_progress, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return view;
    }

    @Override
    public void onClick(View view) {

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
