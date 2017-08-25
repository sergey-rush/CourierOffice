package ru.courier.office.core;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import ru.courier.office.R;

/**
 * Created by rash on 25.08.2017.
 */

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private List<Application> applicationList;

    public class ApplicationViewHolder extends RecyclerView.ViewHolder {

        TextView tvId;
        TextView tvAmount;
        TextView tvCreated;
        ImageView ivStatus;

        public ApplicationViewHolder(View view) {
            super(view);

            tvId = (TextView) view.findViewById(R.id.tvId);
            tvAmount = (TextView) view.findViewById(R.id.tvAmount);
            ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
            tvCreated = (TextView) view.findViewById(R.id.tvCreated);
        }
    }


    public ApplicationAdapter(List<Application> applicationList) {
        this.applicationList = applicationList;
    }

    ViewGroup parent;

    @Override
    public ApplicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.application_item, parent, false);

        return new ApplicationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ApplicationViewHolder holder, int position) {
        Application application = applicationList.get(position);

        holder.tvId.setText(application.Id);
        holder.tvAmount.setText(application.Amount);

        Drawable myIcon = parent.getResources().getDrawable(R.drawable.ic_application);
        myIcon.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        holder.ivStatus.setImageDrawable(myIcon);

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        holder.tvCreated.setText(dateFormat.format(application.Created));
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }
}
