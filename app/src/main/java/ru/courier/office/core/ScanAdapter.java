package ru.courier.office.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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
import ru.courier.office.data.DataAccess;
import ru.courier.office.views.ScanListFragment;
import ru.courier.office.views.ScanViewFragment;
import ru.courier.office.views.TakePhotoFragment;

/**
 * Created by rash on 31.08.2017.
 */

public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ScanViewHolder> {

    private Context _context;
    private List<Scan> _scanList;
    private int _documentId;

    public class ScanViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvStatus;
        public ImageView ivScan;
        public ImageView ivMenu;

        public ScanViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            ivScan = (ImageView) view.findViewById(R.id.ivScan);
            ivMenu = (ImageView) view.findViewById(R.id.ivMenu);
        }
    }

    public ScanAdapter(Context context, int documentId, List<Scan> scanList) {
        _context = context;
        _documentId = documentId;
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
        final int scanId = scan.Id;
        holder.tvTitle.setText(String.format("Страница: %s", scan.PageNum));
        holder.tvTitle.setTag(scan.Id);
        holder.tvStatus.setText(toString(scan.ScanStatus));

        byte[] imageBytes = scan.SmallPhoto;
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.ivScan.setImageBitmap(bitmap);

        holder.ivScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showScanView(holder.ivMenu, scanId);
            }
        });

        holder.ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.ivMenu, scanId);
            }
        });
    }

    private void showScanView(View view, int scanId)
    {
        ScanViewFragment scanListFragment = ScanViewFragment.newInstance(_documentId, scanId);
        FragmentManager fragmentManager = ((AppCompatActivity)_context).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, scanListFragment).commit();
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, int scanId) {

        PopupMenu popup = new PopupMenu(_context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.scan_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new ScanMenuItemClickListener(scanId));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class ScanMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int _scanId;
        public ScanMenuItemClickListener(int scanId) {
            _scanId = scanId;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            FragmentManager fragmentManager = ((AppCompatActivity)_context).getSupportFragmentManager();

            switch (menuItem.getItemId()) {
                case R.id.scan_menu_show:
                    ScanViewFragment scanListFragment = ScanViewFragment.newInstance(_documentId, _scanId);
                    fragmentManager.beginTransaction().replace(R.id.container, scanListFragment).commit();
                    return true;
                case R.id.scan_menu_reshoot:
                    TakePhotoFragment takePhotoFragment = TakePhotoFragment.newInstance(0, 0, _scanId);
                    fragmentManager.beginTransaction().replace(R.id.container, takePhotoFragment).commit();
                    return true;
                case R.id.scan_menu_delete:
                    DataAccess dataAccess = DataAccess.getInstance(_context);
                    dataAccess.deleteScanById(_scanId);
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

    public String toString(ScanStatus status) {

        String output = "Неопределенный";
        switch (status) {
            case None:
                output = "Неопределенный";
                break;
            case Ready:
                output = "Готовый к отправке";
                break;
            case Progress:
                output = "Обрабатывается";
                break;
            case Completed:
                output = "Отправлен";
        }
        return output;
    }
}