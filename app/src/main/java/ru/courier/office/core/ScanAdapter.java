package ru.courier.office.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.courier.office.R;

/**
 * Created by rash on 31.08.2017.
 */

public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ScanViewHolder> {

    private Context _context;
    private List<Scan> _scanList;

    public class ScanViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvStatus;
        public ImageView ivScan, ivMenu;

        public ScanViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            ivScan = (ImageView) view.findViewById(R.id.ivScan);
            ivMenu = (ImageView) view.findViewById(R.id.ivMenu);
        }
    }

    public ScanAdapter(Context context, List<Scan> scanList) {
        _context = context;
        _scanList = scanList;
    }

    @Override
    public ScanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_item, parent, false);
        return new ScanViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ScanViewHolder holder, int position) {
        Scan scan = _scanList.get(position);
        holder.tvTitle.setText(String.format("Страница: %s", scan.PageNum));
        holder.tvTitle.setTag(scan.Id);
        holder.tvStatus.setText(toString(scan.ScanStatus));
        byte[] imageBytes = scan.SmallPhoto;
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.ivScan.setImageBitmap(bitmap);

        // loading scan cover using Glide library
        //Glide.with(_context).load(scan.getThumbnail()).into(holder.ivScan);

        holder.ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.ivMenu);
            }
        });
    }

    public String toString(ScanStatus status) {

        String output = "Неопределено";
        switch (status) {
            case None:
                output = "Неопределеный";
                break;
            case Created:
                output = "Создан";
                break;
            case Progress:
                output = "Загружается";
                break;
            case Downloaded:
                output = "Загружен";
        }
        return output;
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(_context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.scan_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new ScanMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class ScanMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public ScanMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.scan_menu_show:
                    Toast.makeText(_context, "Показать", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.scan_menu_delete:
                    Toast.makeText(_context, "Удалить", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return _scanList.size();
    }
}