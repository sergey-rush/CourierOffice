package com.scanlibrary;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by jhansi on 28/03/15.
 */
public class ScanActivity extends AppCompatActivity implements IScanner {

    //debug
    private static final int IMAGE_MAX_SIZE = Integer.MAX_VALUE;
    //private static final int IMAGE_MAX_SIZE = 2048;

    protected static final String FRAGMENT_TAG = "ScanFragment";

    private static final String TAG = "rlf_app";

    Uri pictUri;

    boolean menuScreenShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_layout);
        showScanFragment();
    }

    private void showScanFragment() {
//        ScanFragment fragment = new ScanFragment();
//        FragmentManager fm = getFragmentManager();
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.content, fragment, FRAGMENT_TAG);
//        fragmentTransaction.commit();
    }

    public void prepareBitmap() {
        PreparePhotoAsyncTask preparePhotoAsyncTask = new PreparePhotoAsyncTask();
        preparePhotoAsyncTask.execute();
    }

    protected String getOriginalPictureName() {
        return "filename.jpg";
    }

    private Bitmap cropBitmapIfNeeded(Bitmap bitmap, float ratio) {
        //Log.d(TAG, "ratio " + ratio);

        Bitmap result;

        int newHeight = Math.round(ratio * (float) bitmap.getWidth());
        if(newHeight > bitmap.getHeight()) newHeight = bitmap.getHeight();
        result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), newHeight);
        bitmap.recycle();

        return result;
    }

    private Bitmap rotateBitmapIfNeeded(Bitmap bitmap, Uri uri) {
        ExifInterface ei = null;
        float rotation = 0;
        try {
            ei = new ExifInterface(uri.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap resultBitmap;

        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
        } else {
            resultBitmap = bitmap;
        }

        return resultBitmap;
    }

    private Bitmap getResizedBitmap(Uri selectedimg) throws IOException {

        AssetFileDescriptor fileDescriptor = null;
        fileDescriptor = getContentResolver().openAssetFileDescriptor(selectedimg, "r");
        BitmapFactory.Options opts;
        // This bit determines only the width/height of the bitmap without loading the contents
        opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, opts);

        int resizeScale = calculateInSampleSize(opts, IMAGE_MAX_SIZE);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = resizeScale;
        fileDescriptor = getContentResolver().openAssetFileDescriptor(selectedimg, "r");
        Bitmap original = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

        return original;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int maxSize) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        while (height / inSampleSize >= maxSize || width / inSampleSize >= maxSize) {
            inSampleSize *= 2;
        }

        return inSampleSize;
    }

    public int getCurrentPhotoNum() {
        return 0;
    }

    public long getTotalDocuments() {
        return 0;
    }

    public void onBitmapPrepared(Bitmap bitmap) {
        //ScanFragment fragment = ((ScanFragment) getFragmentManager().findFragmentByTag(FRAGMENT_TAG));
        //if(fragment != null) fragment.onBitmapReady(bitmap);
    }

    //methods to override

    @Override
    public void onBitmapSelect(Uri uri) {

    }

    @Override
    public void onScanFinish(Uri uri) {
    }

    public String getCurrentFileName() {
        return "";
    }



    public void cancelOrder() {}

    public void callOperator() {}

    public void checkBitmapBrightness(Bitmap bitmap) {}

    public void setMenuShown(boolean menuShown) {
        menuScreenShown = menuShown;
    }

    @Override
    public void onBackPressed() {
        if(menuScreenShown){
            //ScanFragment fragment = ((ScanFragment) getFragmentManager().findFragmentByTag(FRAGMENT_TAG));
            //fragment.hideMenuScreen();
        }else {
            super.onBackPressed();
        }
    }

    public boolean getAddDate() {
        return false;
    }

    public int getAngleToRotateBitmap() {
        return 0;
    }

    protected float getRatio() {
        return 0;
    }

    private class PreparePhotoAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar(ScanActivity.this.getString(R.string.please_wait_prepare_image));
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), getOriginalPictureName());
            Uri pictUri = Uri.parse(file.toURI().toString());

            Bitmap bitmap = null;
            try {
                Bitmap resizedBitmap = getResizedBitmap(pictUri);
                Bitmap rotatedBitmap = rotateBitmapIfNeeded(resizedBitmap, pictUri);
                bitmap = cropBitmapIfNeeded(rotatedBitmap, getRatio());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            hideProgressBar();

            if(result == null) {
                Toast.makeText(ScanActivity.this, getString(R.string.original_image_not_editet_or_saved), Toast.LENGTH_SHORT).show();
            }else{
                onBitmapPrepared(result);
            }
        }
    }

    protected void hideProgressBar() {
    }

    protected void showProgressBar(String messageText) {
    }


    public native Bitmap getScannedBitmap(Bitmap bitmap, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4);

    public native Bitmap getGrayBitmap(Bitmap bitmap);

    public native Bitmap getMagicColorBitmap(Bitmap bitmap);

    public native Bitmap getBWBitmap(Bitmap bitmap);

    public native float[] getPoints(Bitmap bitmap);

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("Scanner");
    }


}