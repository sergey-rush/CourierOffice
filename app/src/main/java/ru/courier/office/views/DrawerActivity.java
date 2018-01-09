package ru.courier.office.views;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.LocalSettings;
import ru.courier.office.web.WebContext;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback,
        NoteFragment.OnFragmentInteractionListener, DatabaseFragment.OnFragmentInteractionListener, AppListFragment.OnFragmentInteractionListener,
        TakePhotoFragment.OnFragmentInteractionListener, QrcodeFragment.OnFragmentInteractionListener,
        LocationFragment.OnFragmentInteractionListener, HelpFragment.OnFragmentInteractionListener, AboutFragment.OnFragmentInteractionListener {


    private WebContext _webContext;
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
        _webContext = WebContext.getInstance();
        setDeviceInfo();

        showFragment(new HelpFragment());
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        boolean handled = false;


//        android.app.FragmentManager fragmentManager = getFragmentManager();
//        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
//        for (Fragment f : fragmentList) {
//
//            if (f instanceof Fragment) {
//
//            }
//        }

        Fragment fragment = getVisibleFragment();

        if (fragment instanceof AppViewFragment) {
            showFragment(new AppListFragment());
            handled = true;
        }

        if (fragment instanceof ScanListFragment) {
            showFragment(AppViewFragment.newInstance(_webContext.Application.Id, 1));
            handled = true;
        }

        if (fragment instanceof ScanViewFragment) {
            showFragment(ScanListFragment.newInstance(_webContext.SelectedDocumentId));
            handled = true;
        }

        if (fragment instanceof TakePhotoFragment) {
            _toolbar.setVisibility(View.VISIBLE);
            showFragment(AppViewFragment.newInstance(_webContext.Application.Id, 0));
            handled = true;
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

    private void removeQrcodeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragmentList = fragmentManager.getFragments();

        for (Fragment fragment : fragmentList) {
            if (fragment instanceof QrcodeFragment) {

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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;

        int id = item.getItemId();
        if (id == R.id.nav_scan) {
            fragment = new QrcodeFragment();
        } else if (id == R.id.nav_apps) {
            fragment = new AppListFragment();
        } else if (id == R.id.nav_notes) {
            fragment = new NoteFragment();
        } else if (id == R.id.nav_help) {
            fragment = new HelpFragment();
        } else if (id == R.id.nav_about) {
            fragment = new AboutFragment();
        } else if (id == R.id.nav_exit) {
            logOut();
            return true;
        }

        showFragment(fragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void logOut(){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }

    private void showFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    private void sendEmail(){

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + "support@mail.ru"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Курьер Офис сообщение");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Здравствуйте!");

        try {
            startActivity(Intent.createChooser(emailIntent, "Отправить письмо с помощью:"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Почтовый клиент не установлен.", Toast.LENGTH_SHORT).show();
        }
    }

    private final int REQUEST_PERMISSION_PHONE_STATE = 1;

    private boolean setDeviceInfo() {
        if (LocalSettings.getDeviceID(this).equals("")) {
            requestPhoneStatePermission();

//            String deviceId;
//            TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            if (mTelephony.getDeviceId() != null) {
//                deviceId = mTelephony.getDeviceId();
//            } else {
//                deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//            }
//            LocalSettings.saveDeviceID(this, deviceId);
        } else {
            _webContext.Imei = LocalSettings.getDeviceID(this);
        }

        return true;
    }

    private void requestPhoneStatePermission() {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            // shouldShowRequestPermissionRationale
            // This method returns true if the app has requested this permission previously and the user denied the request.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {

                AlertDialog dialog = onPermissionRequest(Manifest.permission.READ_PHONE_STATE, REQUEST_PERMISSION_PHONE_STATE);
                dialog.show();

            } else {
                requestPermission(Manifest.permission.READ_PHONE_STATE, REQUEST_PERMISSION_PHONE_STATE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Разрешение отказано", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private AlertDialog onPermissionRequest(final String permission, final int permissionRequestCode) {
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("Разрешение")
                .setMessage("Прошу разрешения")
                .setIcon(R.drawable.ic_error)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        requestPermission(permission, permissionRequestCode);
                   }

                }).create();
        return alertDialog;
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permissionName}, permissionRequestCode);
    }

//    private void showExplanation(String title, String message, final String permission, final int permissionRequestCode) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(title)
//                .setMessage(message)
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        requestPermission(permission, permissionRequestCode);
//                    }
//                });
//        builder.create().show();
//    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
