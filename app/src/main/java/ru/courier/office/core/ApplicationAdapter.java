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
import ru.courier.office.data.DataProvider;

/**
 * Created by rash on 25.08.2017.
 */

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private List<Application> _applicationList;

    public class ApplicationViewHolder extends RecyclerView.ViewHolder {

        TextView tvPersonName;
        TextView tvMerchantName;
        TextView tvAmount;
        TextView tvCreated;
        ImageView ivStatus;
        TextView tvAddress;

        public ApplicationViewHolder(View view) {
            super(view);

            tvPersonName = (TextView) view.findViewById(R.id.tvPersonName);
            tvMerchantName = (TextView) view.findViewById(R.id.tvMerchantName);
            tvAmount = (TextView) view.findViewById(R.id.tvAmount);
            ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
            tvCreated = (TextView) view.findViewById(R.id.tvCreated);
            tvAddress = (TextView) view.findViewById(R.id.tvAddress);
        }
    }


    public ApplicationAdapter(List<Application> applicationList) {
        _applicationList = applicationList;
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
        Application application = _applicationList.get(position);

        holder.tvAmount.setText(application.Amount);
        holder.tvPersonName.setText(application.PersonName);
        holder.tvPersonName.setTag(application.Id);
        holder.tvMerchantName.setText(application.MerchantName);
        holder.tvAddress.setText(application.DeliveryAddress);

        Drawable statusIcon = parent.getResources().getDrawable(R.drawable.ic_application);
        holder.ivStatus.setImageDrawable(statusIcon);

        if(application.ApplicationStatus == ApplicationStatus.None){
            //statusIcon.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            holder.ivStatus.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        }
        if(application.ApplicationStatus == ApplicationStatus.Deliver){
            //statusIcon.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
            holder.ivStatus.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
        }
        if(application.ApplicationStatus == ApplicationStatus.Reject){
            //statusIcon.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            holder.ivStatus.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }

        holder.ivStatus.setTag(application.ApplicationStatus);

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        holder.tvCreated.setText(dateFormat.format(application.Created));
    }

    @Override
    public int getItemCount() {
        return _applicationList.size();
    }
}
