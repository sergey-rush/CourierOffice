package com.clientoffice.views;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clientoffice.R;
import com.clientoffice.data.LoginManager;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements PhoneFragment.OnFragmentInteractionListener,
        ConfirmFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            Toast.makeText(this, "savedInstanceState not null!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    public TextView tvMessage;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            //Log.d("CDA", "onKeyDown Called");
            Toast.makeText(this, "onKeyDown Called!", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        //Log.d("CDA", "onBackPressed Called");
        Toast.makeText(this, "onBackPressed Called!", Toast.LENGTH_SHORT).show();
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    public void onSignClick(View view) throws UnsupportedEncodingException {

        EditText etLogin = (EditText) findViewById(R.id.etLogin);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);

//        HashMap<String, String> postDataParams = new HashMap<>();
//        postDataParams.put("username", editTextPhone.getText().toString());
//        postDataParams.put("password", editTextPassword.getText().toString());

        String postData = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", "username", "password");

        //tvMessage = (TextView) findViewById(R.id.tvMessage);

        LoginManager loginManager = new LoginManager(this, postData);
        final AsyncTask<Void, Void, Void> execute = loginManager.execute();


        //LoginProvider loginProvider = new LoginProvider();
        //loginProvider.validateMember(requestURL, postDataParams);

        //Toast.makeText(this, "Сервис вызван!", Toast.LENGTH_SHORT).show();

//        Intent intent = new Intent(this, DrawerActivity.class);
//        startActivity(intent);

        //startActivity(new Intent(view.getContext(), DrawerActivity.class));
        //finish();
    }
    public void btnPinlockClick(View v)
    {
        //Toast.makeText(this, "btnRegisterClick", Toast.LENGTH_LONG).show();

        startActivity(new Intent(this, PinlockActivity.class));
        //finish();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
