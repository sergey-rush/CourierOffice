package com.clientoffice.views;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.clientoffice.R;
import com.clientoffice.core.Member;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SurveyFragment.OnFragmentInteractionListener, ProductFragment.OnFragmentInteractionListener,
        CameraFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void onbuttonSaveClick(View view) {
        EditText editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        EditText editTextMiddleName = (EditText) findViewById(R.id.editTextMiddleName);
        EditText editTextLastName = (EditText) findViewById(R.id.editTextLastName);

        Spinner spinnerGender = (Spinner) findViewById(R.id.spinnerGender);

        EditText editTextBirthDate = (EditText) findViewById(R.id.editTextBirthDate);
        EditText editTextBirthPlace = (EditText) findViewById(R.id.editTextBirthPlace);
        EditText editTextPasportNum = (EditText) findViewById(R.id.editTextPasportNum);
        EditText editTextPasportSerial = (EditText) findViewById(R.id.editTextPasportSerial);

        EditText editTextAuthority = (EditText) findViewById(R.id.editTextAuthority);
        EditText editTextSnils = (EditText) findViewById(R.id.editTextSnils);
        EditText editTextInn = (EditText) findViewById(R.id.editTextInn);

        Spinner spinnerMarital = (Spinner) findViewById(R.id.spinnerMarital);

        Spinner spinnerChildren = (Spinner) findViewById(R.id.spinnerChildren);

        EditText editTextAddress = (EditText) findViewById(R.id.editTextAddress);

        Member member = new Member();
        member.FirstName = editTextFirstName.getText().toString();
        member.MiddleName = editTextMiddleName.getText().toString();
        member.LastName = editTextLastName.getText().toString();

        member.BirthDate = editTextBirthDate.getText().toString();
        member.BirthPlace = editTextBirthPlace.getText().toString();
        member.PasportNum = editTextPasportNum.getText().toString();
        member.PasportSerial = editTextPasportSerial.getText().toString();

        member.Authority = editTextAuthority.getText().toString();
        member.Snils = editTextSnils.getText().toString();
        member.Inn = editTextInn.getText().toString();

        member.Address = editTextAddress.getText().toString();

        Toast.makeText(this, member.FirstName + " вы опять нажали на кнопку \"Сохранить\"?", Toast.LENGTH_SHORT).show();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Toast.makeText(this, "Id: " + id, Toast.LENGTH_LONG).show();

        Fragment fragment = null;

        if (id == R.id.nav_camera) {

            fragment = new SurveyFragment();
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            fragment = new ProductFragment();
        } else if (id == R.id.nav_slideshow) {
            fragment = new CameraFragment();
        } else if (id == R.id.nav_manage) {
            fragment = new SurveyFragment();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        }

        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
