package ru.courier.office.views;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import ru.courier.office.R;
import ru.courier.office.web.LoginManager;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements PhoneFragment.OnFragmentInteractionListener,
        ConfirmFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
//        if (savedInstanceState != null) {
//            return;
//        }

        PhoneFragment fragment = new PhoneFragment();

        SetFragment(fragment);
    }

    private void SetFragment(Fragment fragment)
    {
        FrameLayout loginFrame = (FrameLayout) findViewById(R.id.loginFrame);
        // Check that the activity is using the layout version with the fragment_container FrameLayout
        if (loginFrame != null) {

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            fragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().replace(R.id.loginFrame, fragment).commit();
        }
    }

    public void btnNextClick(View view) {

        ConfirmFragment fragment = new ConfirmFragment();
        SetFragment(fragment);
    }

    public void btnConfirmClick(View view) {
        EditText etCode = (EditText) findViewById(R.id.etCode);
        Toast.makeText(view.getContext(), etCode.getText(), Toast.LENGTH_SHORT).show();
    }

    public void onbuttonSignClick(View view) throws UnsupportedEncodingException {

        EditText editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        HashMap<String, String> postDataParams = new HashMap<>();
        postDataParams.put("username", editTextPhone.getText().toString());
        postDataParams.put("password", editTextPassword.getText().toString());

        String postData = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", "username", "password");

        TextView textViewMessage = (TextView) findViewById(R.id.textViewMessage);

        //LoginManager loginManager = new LoginManager(this, postData);
        //final AsyncTask<Void, Void, Void> execute = loginManager.execute();


        //LoginProvider loginProvider = new LoginProvider();
        //loginProvider.validateMember(requestURL, postDataParams);

        Toast.makeText(this, "Сервис вызван!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, DrawerActivity.class);
        startActivity(intent);

        //startActivity(new Intent(view.getContext(), DrawerActivity.class));
        //finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
