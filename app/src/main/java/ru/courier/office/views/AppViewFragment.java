package ru.courier.office.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.courier.office.ApplicationService;
import ru.courier.office.R;
import ru.courier.office.core.ApplicationStatus;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.WebContext;

public class AppViewFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static final String ARG_APPLICATION_ID = "applicationId";
    private static final String ARG_TAB_INDEX = "tabIndex";
    private int _applicationId;
    private int _tabIndex;
    private Context _context;
    private WebContext _webContext;
    private DataAccess _dataAccess;
    private View _view;

    public AppViewFragment() {}

    public static AppViewFragment newInstance(int applicationId, int tabIndex) {
        AppViewFragment fragment = new AppViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_APPLICATION_ID, applicationId);
        args.putInt(ARG_TAB_INDEX, tabIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _applicationId = getArguments().getInt(ARG_APPLICATION_ID);
            _tabIndex = getArguments().getInt(ARG_TAB_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_appview, container, false);

        _context = getContext();
        _webContext = WebContext.getInstance();
        _dataAccess = DataAccess.getInstance(_context);

        FragmentManager fragmentManager = getFragmentManager();
        List<Fragment> fragmentList = fragmentManager.getFragments();

        for (Fragment fragment : fragmentList) {

            if (fragment instanceof ApplicationFragment) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
            }
            if (fragment instanceof DocumentFragment) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
            }

            if (fragment instanceof StatusFragment) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
            }
        }

        BottomNavigationView bottomNavigationAppView = (BottomNavigationView)_view.findViewById(R.id.bottomNavigationAppView);
        bottomNavigationAppView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.action_add:
                        onScanDocuments();
                        break;
                    case R.id.action_send:
                        onSendApplication();
                        break;
                    case R.id.action_delete:
                        onDeleteApplication();
                        break;
                }

                return true;
            }
        });

        viewPager = (ViewPager) _view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) _view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        TabLayout.Tab tab = tabLayout.getTabAt(_tabIndex);
        tab.select();

        return _view;
    }

    private void onScanDocuments() {

        TakePhotoFragment fragment = TakePhotoFragment.newInstance(_applicationId, 0, 0);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    private void onSendApplication() {
        int scanCount = _dataAccess.countScansByApplicationId(_applicationId);
        if (scanCount == 0) {
            _dataAccess.updateApplicationByApplicationStatus(_applicationId, ApplicationStatus.Deliver);
            _context.startService(new Intent(_context, ApplicationService.class));
            setFragment(new AppListFragment());
        } else {
            Toast.makeText(_context, R.string.some_document_has_no_scan, Toast.LENGTH_SHORT).show();
        }
    }

    private void onDeleteApplication() {
        AlertDialog dialog = onDeleteDialog();
        dialog.show();
    }

    private AlertDialog onDeleteDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(_context, R.style.AlertDialogCustom)
                .setTitle("Удаление заявки")
                .setMessage("Вы действительно желаете удалить эту заявку?")
                .setIcon(R.drawable.ic_question)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        _dataAccess.updateApplicationByApplicationStatus(_applicationId, ApplicationStatus.Reject);
                        _context.startService(new Intent(_context, ApplicationService.class));
                        setFragment(new AppListFragment());
                        dialog.dismiss();
                    }

                }).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).create();
        return alertDialog;
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFrag(ApplicationFragment.newInstance(_applicationId), "ЗАЯВКА");
        adapter.addFrag(DocumentFragment.newInstance(_applicationId), "ДОКУМЕНТЫ");
        adapter.addFrag(StatusFragment.newInstance(_applicationId), "СТАТУСЫ");
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

}
