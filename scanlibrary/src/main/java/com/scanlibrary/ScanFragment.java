package com.scanlibrary;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jhansi on 29/03/15.
 */
public class ScanFragment extends Fragment implements IScanner{

    public static final String OUTPUT_IMAGE_NAME = "tmp_scanned_picture.jpg";

    private RelativeLayout scanButton;
    private ImageView sourceImageView;
    private FrameLayout sourceFrame;
    private PolygonView polygonView;
    private View view;
    private ProgressDialogFragment progressDialogFragment;
    private IScanner scanner;
    private Bitmap original;

    RelativeLayout menuScreen;

//    @Override
//    public void onAttach(Activity activity) {
//
//        //Log.d("rlf_app", "ScanFragment onAttach");
//
//        super.onAttach(activity);
//        if (!(activity instanceof IScanner)) {
//            throw new ClassCastException("Activity must implement IScanner");
//        }
//        this.scanner = (IScanner) activity;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.scan_fragment_layout, null);
        init();
        activityPrepareBitmap();
        return view;
    }

    public ScanFragment() {

    }

    private void init() {
        sourceImageView = (ImageView) view.findViewById(R.id.sourceImageView);
        scanButton = (RelativeLayout) view.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new ScanButtonClickListener());

        scanButton.setClickable(false);
        scanButton.setFocusable(false);
        scanButton.setEnabled(false);


        sourceFrame = (FrameLayout) view.findViewById(R.id.sourceFrame);
        polygonView = (PolygonView) view.findViewById(R.id.polygonView);

        TextView currentPhotoNum = (TextView) view.findViewById(R.id.currentPhotoNum);
        TextView totalNum = (TextView) view.findViewById(R.id.totalNum);
        TextView pageTitle = (TextView) view.findViewById(R.id.pageTitle);

        if(getActivity() != null){
            int currentPhoto = ((ScanActivity) getActivity()).getCurrentPhotoNum();
            currentPhotoNum.setText(Integer.toString(currentPhoto));

            long totalDoc = ((ScanActivity) getActivity()).getTotalDocuments();
            totalNum.setText("/ " + Long.toString(totalDoc));


            String titleText = "";
            switch (currentPhoto){
                case 1:
                    titleText = this.getString(R.string.scan_fragment_title1);
                    break;
                default:
                    titleText = this.getString(R.string.scan_fragment_title);
            }
            pageTitle.setText(titleText);
        }


        menuScreen = (RelativeLayout) view.findViewById(R.id.menuScreen);
        menuScreen.setVisibility(View.INVISIBLE);

        ImageView infoIcon =  (ImageView) view.findViewById(R.id.infoIcon);
        infoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenuScreen();
            }
        });

        ImageView infoIconClose = (ImageView) view.findViewById(R.id.infoIconClose);
        infoIconClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideMenuScreen();
            }
        });

        RelativeLayout rlSkip = (RelativeLayout) view.findViewById(R.id.rlSkip);
        rlSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hideMenuScreen();
                cancelOrder();
            }
        });


        RelativeLayout rlCall = (RelativeLayout) view.findViewById(R.id.rlCall);
        rlCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideMenuScreen();
                callOperator();
            }
        });

    }

    private void activityPrepareBitmap() {
        ((ScanActivity) getActivity()).prepareBitmap();
    }

    /*
    public void onBitmapReady(final Uri uri){
        sourceFrame.post(new Runnable() {
            @Override
            public void run() {
                original = getBitmap(uri);
                if (original != null) {
                    setBitmap(original);
                }
            }
        });
    }
    */

    protected float getRatio() {
        return 0;
    }

    public void onBitmapReady(Bitmap bitmap){
        original = bitmap;
        if (original != null) {
            setBitmap(original);
        }
    }

    private void cancelOrder() {
        ((ScanActivity) getActivity()).cancelOrder();
    }

    private void callOperator() {
        ((ScanActivity) getActivity()).callOperator();
    }

    private void showMenuScreen() {
        ((ScanActivity) getActivity()).setMenuShown(true);
        menuScreen.setVisibility(View.VISIBLE);
    }

    public void hideMenuScreen() {
        ((ScanActivity) getActivity()).setMenuShown(false);
        menuScreen.setVisibility(View.INVISIBLE);
    }

    private Bitmap getBitmap(Uri uri) {
        //Log.d("rlf_app", "ScanFragment getBitmap");
        //Uri uri = getUri();
        //Log.d("rlf_app", "ScanFragment getBitmap uri " + uri);
        try {
            Bitmap bitmap = Utils.getBitmap(getActivity(), uri);
            //Log.d("rlf_app", "ScanFragment getBitmap bitmap " + bitmap);
            //Log.d("rlf_app", "ScanFragment getBitmap bitmap bitmap.getHeight() " + bitmap.getHeight());

            //getActivity().getContentResolver().delete(uri, null, null);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            //Log.d("rlf_app", "ScanFragment getBitmap IOException " + e);
        }
        return null;
    }

    private Uri getUri() {
        Uri uri = getArguments().getParcelable(ScanConstants.SELECTED_BITMAP);
        return uri;
    }

    private void setBitmap(Bitmap original) {
        //Log.d("rlf_app", "ScanFragment setBitmap " + original.getHeight());
        Bitmap scaledBitmap = scaledBitmap(original, sourceFrame.getWidth(), sourceFrame.getHeight());
        sourceImageView.setImageBitmap(scaledBitmap);

        ((ScanActivity) getActivity()).checkBitmapBrightness(original);
    }

    public void onImageCheckSuccess() {

        scanButton.setClickable(true);
        scanButton.setFocusable(true);
        scanButton.setEnabled(true);

        setPoligonView();
    }

    private void setPoligonView() {
        Bitmap tempBitmap = ((BitmapDrawable) sourceImageView.getDrawable()).getBitmap();
        Map<Integer, PointF> pointFs = getEdgePoints(tempBitmap);
        polygonView.setPoints(pointFs);
        polygonView.setVisibility(View.VISIBLE);
        int padding = (int) getResources().getDimension(R.dimen.scanPadding);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;
        polygonView.setLayoutParams(layoutParams);
    }

    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) {
        //Log.d("rlf_app", "ScanFragment getEdgePoints ");
        List<PointF> pointFs = getContourEdgePoints(tempBitmap);
        Map<Integer, PointF> orderedPoints = orderedValidEdgePoints(tempBitmap, pointFs);
        return orderedPoints;
    }

    private List<PointF> getContourEdgePoints(Bitmap tempBitmap) {
        float[] points = ((ScanActivity) getActivity()).getPoints(tempBitmap);
        float x1 = points[0];
        float x2 = points[1];
        float x3 = points[2];
        float x4 = points[3];

        float y1 = points[4];
        float y2 = points[5];
        float y3 = points[6];
        float y4 = points[7];

        List<PointF> pointFs = new ArrayList<>();
        pointFs.add(new PointF(x1, y1));
        pointFs.add(new PointF(x2, y2));
        pointFs.add(new PointF(x3, y3));
        pointFs.add(new PointF(x4, y4));
        return pointFs;
    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {

        //Log.d("rlf_app", "ScanFragment getOutlinePoints ");
        //Log.d("rlf_app", "ScanFragment tempBitmap.getWidth() " + tempBitmap.getWidth());
        //Log.d("rlf_app", "ScanFragment tempBitmap.getHeight() " + tempBitmap.getHeight());

        int marginV = Math.round(tempBitmap.getHeight()/8);
        int marginH = Math.round(tempBitmap.getWidth()/8);

        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(marginH, marginV));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth() - marginH, marginV));
        outlinePoints.put(2, new PointF(marginH, tempBitmap.getHeight()- marginV));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth() - marginH, tempBitmap.getHeight() - marginV));
        return outlinePoints;
    }

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        //Log.d("rlf_app", "ScanFragment orderedValidEdgePoints ");
        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        if (!polygonView.isValidShape(orderedPoints) || (pointFs.get(0).x == 0 && pointFs.get(0).y == 0)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }

        return orderedPoints;
    }

    @Override
    public void onBitmapSelect(Uri uri) {

    }

    @Override
    public void onScanFinish(Uri uri) {

    }

    private class ScanButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Map<Integer, PointF> points = polygonView.getPoints();
            boolean addDate = ((ScanActivity) getActivity()).getAddDate();
            int angleToRotateBitmap = ((ScanActivity) getActivity()).getAngleToRotateBitmap();

            if (isScanPointsValid(points)) {
                new ScanAsyncTask(points, addDate, angleToRotateBitmap).execute();
            } else {
                showErrorDialog();
            }
        }
    }

    private void showErrorDialog() {
        //SingleButtonDialogFragment fragment = new SingleButtonDialogFragment(R.string.ok, getString(R.string.cantCrop), "Error", true);
        //FragmentManager fm = getActivity().getFragmentManager();
        //fragment.show(fm, SingleButtonDialogFragment.class.toString());
    }

    private boolean isScanPointsValid(Map<Integer, PointF> points) {
        return points.size() == 4;
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private Bitmap getScannedBitmap(Bitmap original, Map<Integer, PointF> points) {
        int width = original.getWidth();
        int height = original.getHeight();
        float xRatio = (float) original.getWidth() / sourceImageView.getWidth();
        float yRatio = (float) original.getHeight() / sourceImageView.getHeight();

        float x1 = (points.get(0).x) * xRatio;
        float x2 = (points.get(1).x) * xRatio;
        float x3 = (points.get(2).x) * xRatio;
        float x4 = (points.get(3).x) * xRatio;
        float y1 = (points.get(0).y) * yRatio;
        float y2 = (points.get(1).y) * yRatio;
        float y3 = (points.get(2).y) * yRatio;
        float y4 = (points.get(3).y) * yRatio;
        Log.d("", "POints(" + x1 + "," + y1 + ")(" + x2 + "," + y2 + ")(" + x3 + "," + y3 + ")(" + x4 + "," + y4 + ")");
        Bitmap _bitmap = ((ScanActivity) getActivity()).getScannedBitmap(original, x1, y1, x2, y2, x3, y3, x4, y4);
        return _bitmap;
    }

    private class ScanAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        private Map<Integer, PointF> points;
        boolean addDate;
        int angleToRotateBitmap;

        public ScanAsyncTask(Map<Integer, PointF> points, boolean addDate, int angleToRotateBitmap) {
            this.points = points;
            this.addDate = addDate;
            this.angleToRotateBitmap = angleToRotateBitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(getString(R.string.scanning));
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap result = getScannedBitmap(original, points);

            //Log.d("rlf_app", "angleToRotateBitmap " + angleToRotateBitmap);
            if(angleToRotateBitmap != 0) result = rotateBitmap(result, angleToRotateBitmap);
            if(addDate) result = addDate(result);

            return result;
        }

        private Bitmap rotateBitmap(Bitmap bitmap, int rotation) {

            Bitmap resBitmap;

            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            resBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();

            return resBitmap;
        }

        private Bitmap addDate(Bitmap bmp) {

            String dateStr = new SimpleDateFormat("dd.MM.yyyy", Locale.US).format(new Date());
            String timeStr = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());

            int lineHeight = (int) Math.round(bmp.getWidth()*.14);
            long textSize = Math.round(0.5 * lineHeight);
            long textMargin = Math.round(bmp.getWidth()*.08);



            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap dateBmp = Bitmap.createBitmap(bmp.getWidth(), lineHeight, conf);

            Canvas canvas = new Canvas(dateBmp);

            Paint paint = new Paint();
            paint.setColor(Color.WHITE); // back Color

            canvas.drawRect(0, 0, bmp.getWidth(), lineHeight, paint);

            paint.setColor(Color.BLACK); // Text Color
            paint.setTextSize(textSize); // Text Size
            paint.setFakeBoldText(true);

            long dateTextWidth = Math.round(paint.measureText(dateStr));
            long timeTextWidth = Math.round(paint.measureText(timeStr));
            long timeStrX = bmp.getWidth() - timeTextWidth - textMargin;
            if(timeStrX < (textMargin *2 + dateTextWidth)) timeStrX = (textMargin *2 + dateTextWidth);

            canvas.drawText(dateStr, textMargin, textSize + (lineHeight - textSize)/2, paint);
            canvas.drawText(timeStr, timeStrX, textSize + (lineHeight - textSize)/2, paint);




            Bitmap bmOverlay = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight() + lineHeight, bmp.getConfig());
            Canvas canvas1 = new Canvas(bmOverlay);

            canvas1.drawBitmap(bmp, null, new RectF(0, 0, bmp.getWidth(), bmp.getHeight()), null);
            canvas1.drawBitmap(dateBmp, null, new RectF(0, bmp.getHeight(), bmp.getWidth(), bmp.getHeight() + lineHeight), null);

            bmp.recycle();
            dateBmp.recycle();

            return bmOverlay;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            dismissDialog();
            //Uri uri = Utils.getUri(getActivity(), bitmap);
            try {
                //String fileName = ((ScanActivity) getActivity()).getCurrentFileName();
                String fileName = OUTPUT_IMAGE_NAME;
                //Log.d("rlf_app", "fileName " + fileName);
                Uri uri = Utils.saveBitmap(getActivity(), bitmap, fileName, 100);
                bitmap.recycle();
                scanner.onScanFinish(uri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), getActivity().getString(R.string.scanned_image_not_saved), Toast.LENGTH_SHORT).show();
            }

        }
    }

    protected void showProgressDialog(String message) {
        //progressDialogFragment = new ProgressDialogFragment(message);
        //FragmentManager fm = getFragmentManager();
        //progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    protected void dismissDialog() {
        progressDialogFragment.dismissAllowingStateLoss();
    }

}