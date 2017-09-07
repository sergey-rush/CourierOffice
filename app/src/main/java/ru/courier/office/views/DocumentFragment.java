package ru.courier.office.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.Document;
import ru.courier.office.core.DocumentAdapter;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.WebContext;

public class DocumentFragment extends Fragment {

    public DocumentFragment() {
        // Required empty public constructor
    }
    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document, container, false);

        WebContext webContext = WebContext.getInstance();
        List<Document> documentList = webContext.Application.DocumentList;

        if (documentList == null) {
            documentList = DataAccess.getInstance(getContext()).getDocumentsByApplicationGuid(webContext.Application.ApplicationGuid);
        }

        if (documentList != null) {

            DocumentAdapter adapter = new DocumentAdapter(getContext(), documentList);

            listView = (ListView) view.findViewById(R.id.lvDocuments);
            listView.setItemsCanFocus(false);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                    String title = tvTitle.getText().toString();
                    int documentId = Integer.parseInt(tvTitle.getTag().toString());


                    ScanListFragment scanListFragment = ScanListFragment.newInstance(documentId, title);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, scanListFragment).commit();

                }
            });
        }

        return view;
    }

}