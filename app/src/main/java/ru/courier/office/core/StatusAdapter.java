package ru.courier.office.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import ru.courier.office.R;

/**
 * Created by rash on 29.08.2017.
 */

public class StatusAdapter extends ArrayAdapter<String> {

    Context context;
    List<Status> statusList;

    public StatusAdapter(Context context, List<Status> statusList) {
        super(context, R.layout.status_item);
        this.context = context;
        this.statusList = statusList;
    }

    @Override
    public int getCount() {
        return statusList.size();
    }

    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = inflater.inflate(R.layout.status_item, parent, false);
            holder.tvInfo = (TextView) view.findViewById(R.id.tvInfo);
            holder.tvCreated = (TextView) view.findViewById(R.id.tvCreated);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Status status = statusList.get(position);
        holder.tvInfo.setText(status.Info);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        holder.tvCreated.setText(dateFormat.format(status.Created));

        return view;
    }

    static class ViewHolder {
        TextView tvInfo;
        TextView tvCreated;
    }
}

