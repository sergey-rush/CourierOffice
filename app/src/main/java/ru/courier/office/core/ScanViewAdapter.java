package ru.courier.office.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.courier.office.R;
import ru.courier.office.views.ScanListFragment;
import ru.courier.office.views.ScanViewFragment;

/**
 * Created by rash on 04.09.2017.
 */

public class ScanViewAdapter extends PagerAdapter {

    private Context _context;
    private List<Scan> _scanList;
    private LayoutInflater _inflater;
    private int _documentId;

    public ScanViewAdapter(Context context, int documentId, List<Scan> scanList) {
        _context = context;
        _documentId = documentId;
        _scanList = scanList;
    }

    @Override
    public int getCount() {
        return _scanList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = _inflater.inflate(R.layout.scan_view_item, container, false);

        TouchImageView imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        Button btnClose = (Button) viewLayout.findViewById(R.id.btnClose);

        Scan scan = _scanList.get(position);
        byte[] imageBytes = scan.SmallPhoto;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 1;

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
        imgDisplay.setImageBitmap(bitmap);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanListFragment scanListFragment = ScanListFragment.newInstance(_documentId);
                FragmentManager fragmentManager = ((AppCompatActivity)_context).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, scanListFragment).commit();
            }
        });

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}