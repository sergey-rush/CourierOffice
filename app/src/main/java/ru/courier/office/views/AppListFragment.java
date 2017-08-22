package ru.courier.office.views;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ru.courier.office.R;
import ru.courier.office.core.Product;
import ru.courier.office.core.ProductAdapter;
import ru.courier.office.data.DataContext;
import ru.courier.office.data.MemberManager;
import ru.courier.office.data.ProductManager;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AppListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AppListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AppListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppListFragment newInstance(String param1, String param2) {
        AppListFragment fragment = new AppListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_applist, container, false);

        ProductManager productManager = new ProductManager(view, "BC2533EF-A298-4BAA-9728-8A8C26DF4A1D");
        productManager.execute();

        List<Product> products = DataContext.getInstance().Products;

        ProductAdapter adapter = new ProductAdapter(this.getContext(), products);

        ListView listViewSection = (ListView) view.findViewById(R.id.listViewSection);
        listViewSection.setAdapter(adapter);

        listViewSection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                LinearLayout layout = (LinearLayout)view;
                TextView tvId = (TextView)layout.findViewById(R.id.tvId);
                TextView tvName = (TextView)layout.findViewById(R.id.tvAmount);
                Toast.makeText(view.getContext(), "Item: " + tvName.getText(), Toast.LENGTH_SHORT).show();
                int itemId = Integer.parseInt(tvId.getText().toString());
                AskOption(itemId, view).show();
            }
        });

        return view;
    }

    private AlertDialog AskOption(final int itemId, final View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(view.getContext())
                //set message, title, and icon
                .setTitle("Удаление")
                .setMessage("Вы действительно желаете удалить?")
                .setIcon(R.drawable.ic_menu_send)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //DataManager dataManager = new DataManager(view.getContext());
                        //dataManager.deleteSection(itemId);
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return alertDialog;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
