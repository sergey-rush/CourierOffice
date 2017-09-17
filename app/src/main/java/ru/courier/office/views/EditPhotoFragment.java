package ru.courier.office.views;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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

import com.scanlibrary.PolygonView;
import com.scanlibrary.ProgressDialogFragment;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanFragment;
import com.scanlibrary.Utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.courier.office.R;
import ru.courier.office.core.LocalSettings;
import ru.courier.office.core.OrientationManager;
import ru.courier.office.core.Scan;
import ru.courier.office.data.DataAccess;

public class EditPhotoFragment extends Fragment {
   
    private static final String ARG_SCAN_ID = "scanId";
    private int _scanId;
    private DataAccess _dataAccess;
    private Scan _scan;
    private static final int IMAGE_NORMAL = 0;
    private static final int IMAGE_DARK = 1;
    private static final int IMAGE_BLUR = 2;
    private ProgressDialog progressDialog;
    private static final float BRIGHTNESS_THRESHOLD_PERCENT = .25f;
    public static final int BRIGHTNESS_THRESHOLD = 20;
    private View _view;
    private ImageView sourceImageView;
    private RelativeLayout scanButton;
    private FrameLayout sourceFrame;
    private PolygonView polygonView;
    private Bitmap _inputBitmap;

    public EditPhotoFragment() {}

    public static EditPhotoFragment newInstance(int scanId) {
        EditPhotoFragment fragment = new EditPhotoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCAN_ID, scanId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _scanId = getArguments().getInt(ARG_SCAN_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_edit_photo, container, false);
        _dataAccess = DataAccess.getInstance(getContext());
        _scan = _dataAccess.getScanById(_scanId);


        sourceImageView = (ImageView) _view.findViewById(com.scanlibrary.R.id.sourceImageView);
        scanButton = (RelativeLayout) _view.findViewById(com.scanlibrary.R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanButtonClick(view);
            }
        });

        sourceFrame = (FrameLayout) _view.findViewById(com.scanlibrary.R.id.sourceFrame);
        polygonView = (PolygonView) _view.findViewById(com.scanlibrary.R.id.polygonView);
        loadScan();
        checkBitmapBrightness(_inputBitmap);
        return _view;
    }

    private void loadScan(){
        int scanImageLength = _scan.ImageLength;
        byte[] imageBytes = new byte[scanImageLength];

        // The bytes have already been read
        int totalBytes = 0;
        int bufferSize = 1048576;

        if (scanImageLength < bufferSize) {
            bufferSize = scanImageLength;
        }

        while (totalBytes < scanImageLength) {

            if (totalBytes + bufferSize > scanImageLength) {
                bufferSize = scanImageLength - totalBytes;
            }

            byte[] buffer = new byte[bufferSize];
            buffer = _dataAccess.getScanImage(_scanId, totalBytes, bufferSize);

            System.arraycopy(buffer, 0, imageBytes, totalBytes, buffer.length);
            totalBytes = (totalBytes + bufferSize);
        }

        _scan.LargePhoto = imageBytes;

        _inputBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        sourceImageView.setImageBitmap(_inputBitmap);
    }

    private void scanButtonClick(View view) {

        Map<Integer, PointF> points = polygonView.getPoints();
        boolean addDate = ((ScanActivity) getActivity()).getAddDate();
        int angleToRotateBitmap = ((ScanActivity) getActivity()).getAngleToRotateBitmap();

        if (isScanPointsValid(points)) {
            new ScanAsyncTask(points, addDate, angleToRotateBitmap).execute();
        } else {
            //showErrorDialog();
        }
    }

    private boolean isScanPointsValid(Map<Integer, PointF> points) {
        return points.size() == 4;
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
            showProgressDialog(getString(com.scanlibrary.R.string.scanning));
        }



        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap result = getScannedBitmap(_inputBitmap, points);

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
            Toast.makeText(getActivity(), "Completed", Toast.LENGTH_SHORT).show();

        }
    }

    protected void dismissDialog() {
        //progressDialogFragment.dismissAllowingStateLoss();
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

    protected void showProgressDialog(String message) {
        //progressDialogFragment = new ProgressDialogFragment(message);
        //android.app.FragmentManager fm = getFragmentManager();
        //progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    public void checkBitmapBrightness(Bitmap bitmap) {
        BrightnessAsyncTask brightnessAsyncTask = new BrightnessAsyncTask(bitmap, BRIGHTNESS_THRESHOLD_PERCENT);
        brightnessAsyncTask.execute();
    }


    protected float getRatio() {
        return LocalSettings.getFrameRatio(getContext());
    }


    public void onScanFinish(Uri uri) {
        //Intent resultIntent = new Intent();
        //setResult(RESULT_OK, resultIntent);

        //Intent intent = new Intent(getContext(), AcceptPhotoActivity.class);
        //intent.putExtra(EDITED_PICTURE_URI, uri.toString());
        //startActivity(intent);

        //finish();
    }


    public int getCurrentPhotoNum() {
        return LocalSettings.getCurrentDocNumber(getContext());
    }


    public long getTotalDocuments() {
        //return DatabaseHelper.getTotalDocuments();
        return 8;
    }


    protected String getOriginalPictureName() {
        return LocalSettings.ORIGINAL_PICTURE_NAME;
    }


    public String getCurrentFileName() {
        return LocalSettings.getCurrentPictureFileName(getContext());
    }


    public boolean getAddDate() {
        //String docId = DatabaseHelper.getDocId(LocalSettings.getCurrentDocNumber(getContext()));
        //Log.d(TAG, "docId " + docId);
        //return (docId.toUpperCase().equals(SUtils.USER_PHOTO_DOC_ID));
        return true;
    }

    public int getAngleToRotateBitmap() {

        int result = 0;

        int deviceOrientation = LocalSettings.getDeviceOrientationDuringTakePhoto(getContext());

        switch (OrientationManager.ScreenOrientation.values()[deviceOrientation]) {
            case PORTRAIT:
                result = 0;
                break;
            case LANDSCAPE:
                result = 270;
                break;
            case REVERSED_PORTRAIT:
                result = 180;
                break;
            case REVERSED_LANDSCAPE:
                result = 90;
                break;
        }

        return result;

    }





    protected void showProgressBar(String messageText) {
        progressDialog = new ProgressDialog(getContext());
        //progressDialog.setMessage(getContext().getString(R.string.please_wait_get_brightness));
        progressDialog.setMessage(messageText);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    protected void hideProgressBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        hideProgressBar();
        super.onDestroy();
    }

    private class BrightnessAsyncTask extends AsyncTask<Void, Void, Integer> {

        private Bitmap _bitmap;
        private float _threshold;

        public BrightnessAsyncTask(Bitmap bitmap, float threshold) {
            _bitmap = bitmap;
            _threshold = threshold;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar("please_wait_get_brightness");
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int darkness = detectDarkPicture(_bitmap, _threshold);
            if (darkness == IMAGE_DARK) {
                return darkness;
            } else {
                return detectBlur(_bitmap);
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            hideProgressBar();
            if (result == IMAGE_DARK) {
                showDarkPictureMessage();
            } else if (result == IMAGE_BLUR) {
                showBlurPictureMessage();
            } else {
                setPoligonView();
            }
        }
    }

    private void setPoligonView() {
        Bitmap tempBitmap = ((BitmapDrawable) sourceImageView.getDrawable()).getBitmap();
        Map<Integer, PointF> pointFs = getEdgePoints(tempBitmap);
        polygonView.setPoints(pointFs);
        polygonView.setVisibility(View.VISIBLE);
        int padding = (int) getResources().getDimension(com.scanlibrary.R.dimen.scanPadding);
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


    private int detectDarkPicture(Bitmap bitmap, float threshold) {
        int result;

        //Log.d(TAG, "checkBitmapBrightness start ");
        //Log.d(TAG, "-----------------------------");

        int histogram[] = new int[256];

        for (int i = 0; i < 256; i++) {
            histogram[i] = 0;
        }

        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                int pixel = bitmap.getPixel(x, y);

                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);

                int brightness = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
                histogram[brightness]++;

                //Log.d(TAG, "brightness " + brightness);
            }
        }

        int allPixelsCount = bitmap.getWidth() * bitmap.getHeight();

        // Count pixels with brightness less then 20 //default 10
        int darkPixelCount = 0;
        for (int i = 0; i < BRIGHTNESS_THRESHOLD; i++) {
            darkPixelCount += histogram[i];
        }

        if (darkPixelCount > allPixelsCount * threshold) {
            //Log.d(TAG, "Dark picture");
            result = IMAGE_DARK;
        } else {
            //Log.d(TAG, "Light picture");
            result = IMAGE_NORMAL;
        }

        //Log.d(TAG, "darkPixelCount/allPixelsCount " + (float)((float)darkPixelCount/(float)allPixelsCount));

        //Log.d(TAG, "darkPixelCount " + darkPixelCount);
        //Log.d(TAG, "allPixelsCount " + allPixelsCount);
        //Log.d(TAG, "checkBitmapBrightness end ");

        return result;
    }

    private Integer detectBlur(Bitmap image) {

        //Log.d(TAG, "start blur detect");

        /*
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inDither = true;
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap image = BitmapFactory.decodeByteArray(im, 0, im.length);
        */

        int l = CvType.CV_8UC1; //8-bit grey scale image
        Mat matImage = new Mat();
        org.opencv.android.Utils.bitmapToMat(image, matImage);
        Mat matImageGrey = new Mat();
        Imgproc.cvtColor(matImage, matImageGrey, Imgproc.COLOR_BGR2GRAY);

        Bitmap destImage;
        destImage = Bitmap.createBitmap(image);
        Mat dst2 = new Mat();
        org.opencv.android.Utils.bitmapToMat(destImage, dst2);
        Mat laplacianImage = new Mat();
        dst2.convertTo(laplacianImage, l);
        Imgproc.Laplacian(matImageGrey, laplacianImage, CvType.CV_8U);
        Mat laplacianImage8bit = new Mat();
        laplacianImage.convertTo(laplacianImage8bit, l);

        Bitmap bmp = Bitmap.createBitmap(laplacianImage8bit.cols(), laplacianImage8bit.rows(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(laplacianImage8bit, bmp);
        int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        int maxLap = -16777216;

        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] > maxLap)
                maxLap = pixels[i];
        }

        //int soglia = -6118750; //default

        //int soglia = -9218750; //v1

        //int soglia = -2418750; //v2

        int soglia = -6118750; //v3


        //Log.d(TAG, "maxLap " + maxLap);
        //Log.d(TAG, "soglia " + soglia);

        if (maxLap < soglia || maxLap == soglia) {
            //Log.d(TAG, "blur image");
            return IMAGE_BLUR;
        } else {
            //Log.d(TAG, "not blur image");
            return IMAGE_NORMAL;
        }
    }

    private void showDarkPictureMessage() {

        android.app.AlertDialog.Builder darkPictureDialog = new android.app.AlertDialog.Builder(getContext());
        darkPictureDialog.setMessage("dark_picture_message");
        darkPictureDialog.setNeutralButton("retake", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                retakePhoto();
            }
        });
        darkPictureDialog.setCancelable(false);
        darkPictureDialog.show();
    }

    private void showBlurPictureMessage() {

        android.app.AlertDialog.Builder blurPictureDialog = new android.app.AlertDialog.Builder(getContext());
        blurPictureDialog.setMessage("blur_picture_message");
        blurPictureDialog.setNeutralButton("retake", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                retakePhoto();
            }
        });
        blurPictureDialog.setCancelable(false);
        blurPictureDialog.show();
    }

    private void retakePhoto() {
        launchTakePhotoActivity();
        //finish();
    }

//        private void nextScreen() {
//            if (LocalSettings.getResumePageReached(EditPhotoActivity.getContext())) {
//                finish();
//            } else {
//
//                int currentPhoto = LocalSettings.getCurrentDocNumber(EditPhotoActivity.getContext());
//
//                if (currentPhoto < DatabaseHelper.getTotalDocuments()) {
//                    currentPhoto++;
//                    LocalSettings.setCurrentDocNumber(EditPhotoActivity.getContext(), currentPhoto);
//                    launchTakePhotoActivity();
//                } else {
//                    launchResumeActivity();
//                }
//                finish();
//            }
//        }

    private void launchTakePhotoActivity() {
        //Intent intent = new Intent(EditPhotoActivity.getContext(), TakePhotoActivity.class);
        //startActivity(intent);
    }

    private void launchResumeActivity() {
        //Intent intent = new Intent(EditPhotoActivity.getContext(), ResumeActivity.class);
        //startActivity(intent);
    }


    public void callOperator() {
        //SUtils.callOperator(getContext());
    }
}
