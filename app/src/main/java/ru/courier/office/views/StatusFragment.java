package ru.courier.office.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.Status;
import ru.courier.office.core.StatusAdapter;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.WebContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        WebContext webContext = WebContext.getInstance();
        DataAccess dataAccess = DataAccess.getInstance(view.getContext());
        webContext.Application.StatusList = dataAccess.getStatusesByApplicationId(webContext.Application.Id);

        List<Status> statusList = webContext.Application.StatusList;
        StatusAdapter adapter = new StatusAdapter(this.getContext(), statusList);

        ListView listView = (ListView) view.findViewById(R.id.lvStatuses);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                RelativeLayout layout = (RelativeLayout)view;
                TextView tvInfo = (TextView)layout.findViewById(R.id.tvInfo);
                Toast.makeText(view.getContext(), tvInfo.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
