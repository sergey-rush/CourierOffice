package ru.courier.office.data;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import ru.courier.office.core.Member;
import ru.courier.office.views.DrawerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ProductManager extends AsyncTask<Void, Void, Void> {

    private View _view;
    String id;

    public ProductManager(View view, String memberId) {
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
        ProductProvider productProvider = new ProductProvider();
        int status = productProvider.getProducts(id);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // Dismiss the progress dialog
        if (pDialog.isShowing())
            pDialog.dismiss();

        //Toast.makeText(_view.getContext(), "Сервис вызван!", Toast.LENGTH_SHORT).show();

//        View view = inflater.inflate(R.layout.fragment_section, container, false);
//
//        DataAccess dataAccess = DataAccess.getInstance(this.getContext());
//        List<Section> sections = dataAccess.Sections().getSections(1613);
//
//        SectionAdapter adapter = new SectionAdapter(this.getContext(), sections);
//
//        ListView listViewSection = (ListView) view.findViewById(R.id.listViewSection);
//        listViewSection.setAdapter(adapter);
//
//        listViewSection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//                LinearLayout layout = (LinearLayout)view;
//                TextView tvId = (TextView)layout.findViewById(R.id.tvId);
//                TextView tvName = (TextView)layout.findViewById(R.id.tvName);
//                Toast.makeText(view.getContext(), "Item: " + tvName.getText(), Toast.LENGTH_SHORT).show();
//                int itemId = Integer.parseInt(tvId.getText().toString());
//                AskOption(itemId, view).show();
//            }
//        });

//        return view;

    }

}