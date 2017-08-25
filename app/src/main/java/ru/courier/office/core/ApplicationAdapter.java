package ru.courier.office.core;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.courier.office.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ApplicationAdapter extends ArrayAdapter<String> {

    Context context;
    List<Application> Applications;

    public ApplicationAdapter(Context context, List<Application> Applications) {
        super(context, R.layout.application_item);
        this.context = context;
        this.Applications = Applications;
    }

    @Override
    public int getCount() {
        return Applications.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.application_item, parent, false);
            holder.tvId = (TextView) convertView.findViewById(R.id.tvId);
            holder.tvAmount = (TextView) convertView.findViewById(R.id.tvAmount);
            holder.ivStatus = (ImageView) convertView.findViewById(R.id.ivStatus);
            holder.tvCreated = (TextView) convertView.findViewById(R.id.tvCreated);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Application Application = Applications.get(position);
        holder.tvId.setText(Application.Id);
        holder.tvAmount.setText(Application.Id);

        Drawable myIcon = parent.getResources().getDrawable(R.drawable.ic_application);
        myIcon.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
        holder.ivStatus.setImageDrawable(myIcon);

        //holder.ivStatus.setImageResource(R.drawable.ic_application);

        holder.tvCreated.setText(dateFormat.format(Application.Created));
        return convertView;
    }

    static class ViewHolder {
        TextView tvId;
        TextView tvAmount;
        TextView tvCreated;
        ImageView ivStatus;
    }
}


