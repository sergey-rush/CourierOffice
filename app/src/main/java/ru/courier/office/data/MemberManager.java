package ru.courier.office.data;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import ru.courier.office.R;
import ru.courier.office.core.Member;
import ru.courier.office.views.DrawerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class MemberManager extends AsyncTask<Void, Void, Void> {

    private View _view;

    String id;

    public MemberManager(View view, String memberId) {
        _view = view;
        id=memberId;
    }

    private ProgressDialog pDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog = new ProgressDialog(_view.getContext());
        pDialog.setMessage("Пожалуйста, подождите...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        MemberProvider memberProvider = new MemberProvider();
        int status = memberProvider.getMember(id);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // Dismiss the progress dialog
        if (pDialog.isShowing())
            pDialog.dismiss();

        Member member = DataContext.getInstance().Member;

        EditText editTextFirstName = (EditText) _view.findViewById(R.id.editTextFirstName);
        editTextFirstName.setText(member.FirstName);

        EditText editTextMiddleName = (EditText) _view.findViewById(R.id.editTextMiddleName);
        editTextMiddleName.setText(member.MiddleName);

        EditText editTextLastName = (EditText) _view.findViewById(R.id.editTextLastName);
        editTextLastName.setText(member.LastName);


        Toast.makeText(_view.getContext(), "Member LastName: " + member.LastName, Toast.LENGTH_SHORT).show();

        //Intent intent = new Intent(_view.getContext(), DrawerActivity.class);
        //_view.getContext().startActivity(intent);

    }

}