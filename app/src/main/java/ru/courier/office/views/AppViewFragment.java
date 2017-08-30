package ru.courier.office.views;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.Application;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.WebContext;

public class AppViewFragment extends Fragment {

    private static final String ARG_PARAM1 = "appId";
    private int id;

    private OnFragmentInteractionListener mListener;

    public AppViewFragment() {
    }

    public static AppViewFragment newInstance(int id) {
        AppViewFragment fragment = new AppViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ARG_PARAM1);
        }
    }

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appview, container, false);

        DataAccess dataAccess = DataAccess.getInstance(view.getContext());
        WebContext webContext = WebContext.getInstance();
        webContext.Application = dataAccess.getApplicationById(id);
        webContext.Application.Merchant = dataAccess.getMerchantById(webContext.Application.MerchantId);
        webContext.Application.Person = dataAccess.getPersonById(webContext.Application.PersonId);
        webContext.Application.StatusList = dataAccess.getStatuses(webContext.Application.ApplicationId);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(view, viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(View view, ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFrag(new ApplicationFragment(), "ЗАЯВКА");
        adapter.addFrag(new MerchantFragment(), "МАГАЗИН");
        adapter.addFrag(new PersonFragment(), "ЗАЯВИТЕЛЬ");
        adapter.addFrag(new StatusFragment(), "СТАТУСЫ");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
