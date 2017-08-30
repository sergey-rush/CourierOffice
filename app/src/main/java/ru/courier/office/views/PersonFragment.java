package ru.courier.office.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ru.courier.office.R;
import ru.courier.office.core.Application;
import ru.courier.office.core.Person;
import ru.courier.office.web.WebContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends Fragment {

    TextView tvPersonId;
    TextView tvFirstName;
    TextView tvMiddleName;
    TextView tvLastName;
    TextView tvGender;
    TextView tvBirthDate;

    public PersonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_person, container, false);

        WebContext webContext = WebContext.getInstance();
        Person person = webContext.Application.Person;

        tvPersonId = (TextView) view.findViewById(R.id.tvPersonId);
        tvPersonId.setText(person.PersonId);

        tvFirstName = (TextView) view.findViewById(R.id.tvFirstName);
        tvFirstName.setText(person.FirstName);

        tvMiddleName = (TextView) view.findViewById(R.id.tvMiddleName);
        tvMiddleName.setText(person.MiddleName);

        tvLastName = (TextView) view.findViewById(R.id.tvLastName);
        tvLastName.setText(person.LastName);

        tvGender = (TextView) view.findViewById(R.id.tvGender);
        tvGender.setText(person.Gender);

        tvBirthDate = (TextView) view.findViewById(R.id.tvBirthDate);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        tvBirthDate.setText(dateFormat.format(person.BirthDate));

        return view;
    }

}
