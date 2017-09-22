package ru.courier.office.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.courier.office.ApplicationService;
import ru.courier.office.R;
import ru.courier.office.web.WebContext;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CompoundButton.OnCheckedChangeListener,
        HomeFragment.OnFragmentInteractionListener, DatabaseFragment.OnFragmentInteractionListener, AppListFragment.OnFragmentInteractionListener,
        TakePhotoFragment.OnFragmentInteractionListener, QrcodeFragment.OnFragmentInteractionListener, UploadFragment.OnFragmentInteractionListener,
        LocationFragment.OnFragmentInteractionListener, UpdateScansFragment.OnFragmentInteractionListener {

    private Switch swtOnline;
    private TextView tvSwitchOnline;
    private Toolbar _toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        _toolbar = (Toolbar) findViewById(R.id.tlbMain);
        setSupportActionBar(_toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, _toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        tvSwitchOnline = (TextView) headerLayout.findViewById(R.id.tvSwitchOnline);
        swtOnline = (Switch) headerLayout.findViewById(R.id.swtOnline);
        swtOnline.setOnCheckedChangeListener(this);

        showFragment(new HomeFragment());
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            tvSwitchOnline.setText("Онлайн");
            startService(new Intent(this, ApplicationService.class));
            Toast.makeText(this, "Приложение онлайн", Toast.LENGTH_SHORT).show();
        } else {
            tvSwitchOnline.setText("Оффлайн");
            stopService(new Intent(this, ApplicationService.class));
            Toast.makeText(this, "Приложение оффлайн", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {

        boolean handled = false;

        android.app.FragmentManager fragmentManager = getFragmentManager();
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

        Fragment fragment = getVisibleFragment();

        if (fragment instanceof AppViewFragment) {
            showFragment(new AppListFragment());
            handled = true;
        }

        if (fragment instanceof ScanListFragment) {
            WebContext webContext = WebContext.getInstance();
            showFragment(AppViewFragment.newInstance(webContext.Application.Id));
            handled = true;
        }

        if (fragment instanceof ScanViewFragment) {
            WebContext webContext = WebContext.getInstance();
            showFragment(ScanListFragment.newInstance(webContext.SelectedDocumentId));
            handled = true;
        }

        if (fragment instanceof TakePhotoFragment) {
            _toolbar.setVisibility(View.VISIBLE);
            showFragment(new HomeFragment());
            handled = true;
        }

        for (Fragment f : fragmentList) {

            if (f instanceof Fragment) {

            }
        }

        if (!handled) {
            super.onBackPressed();
        }
    }

    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    private void removeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragmentList = fragmentManager.getFragments();

        for (Fragment fragment : fragmentList) {
            if (fragment instanceof HomeFragment) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnCorner) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_scan) {
            fragment = new QrcodeFragment();
        } else if (id == R.id.nav_apps) {
            fragment = new AppListFragment();
        } else if (id == R.id.nav_manage) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_data) {
            fragment = new DatabaseFragment();
        } else if (id == R.id.nav_send) {
            fragment = new UploadFragment();
        } else if (id == R.id.nav_location) {
            fragment = new LocationFragment();
        } else if (id == R.id.nav_updatescans) {
            fragment = new UpdateScansFragment();
        }

        showFragment(fragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void showFragment(Fragment fragment)
    {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
