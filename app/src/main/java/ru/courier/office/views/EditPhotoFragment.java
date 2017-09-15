package ru.courier.office.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scanlibrary.ScanFragment;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import ru.courier.office.R;
import ru.courier.office.core.LocalSettings;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditPhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditPhotoFragment extends ScanFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EditPhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditPhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditPhotoFragment newInstance(String param1, String param2) {
        EditPhotoFragment fragment = new EditPhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_photo, container, false);
        return view;
    }


        private static final String TAG = "rlf_app";

        private static final int IMAGE_NORMAL = 0;
        private static final int IMAGE_DARK = 1;
        private static final int IMAGE_BLUR = 2;

        private ProgressDialog progressDialog;

        private static final float BRIGHTNESS_THRESHOLD_PERCENT = .25f;
        public static final int BRIGHTNESS_THRESHOLD = 20;

        @Override
        protected float getRatio() {
            return LocalSettings.getFrameRatio(this);
        }

        @Override
        public void onScanFinish(Uri uri) {
            //Intent resultIntent = new Intent();
            //setResult(RESULT_OK, resultIntent);

            Intent intent = new Intent(this, AcceptPhotoActivity.class);
            //intent.putExtra(EDITED_PICTURE_URI, uri.toString());
            startActivity(intent);

            finish();
        }

        @Override
        public int getCurrentPhotoNum() {
            return LocalSettings.getCurrentDocNumber(this);
        }

        @Override
        public long getTotalDocuments() {
            return DatabaseHelper.getTotalDocuments();
        }

        @Override
        protected String getOriginalPictureName() {
            return LocalSettings.ORIGINAL_PICTURE_NAME;
        }

        @Override
        public String getCurrentFileName() {
            return LocalSettings.getCurrentPictureFileName(this);
        }

        @Override
        public boolean getAddDate() {
            String docId = DatabaseHelper.getDocId(LocalSettings.getCurrentDocNumber(this));
            //Log.d(TAG, "docId " + docId);
            return (docId.toUpperCase().equals(SUtils.USER_PHOTO_DOC_ID));
        }

        @Override
        public int getAngleToRotateBitmap() {
            return SUtils.angleToRotateBitmap(EditPhotoActivity.this);
        }


        @Override
        public void cancelOrder() {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
            dialog.setMessage(this.getString(R.string.cancel_order_qw));
            dialog.setPositiveButton(this.getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    SUtils.clearSettings(EditPhotoActivity.this);
                    //SUtils.removeAllTemporaryFiles(EditPhotoActivity.this);
                    //SUtils.removeOrderEncodedFiles(EditPhotoActivity.this, LocalSettings.getOrderId(EditPhotoActivity.this));
                    ImageLoader.getInstance().clearMemoryCache();

                    //RequestUtils.sendCancelOrderStatus(EditPhotoActivity.this);
                    RequestUtils.sendFinalStatus(EditPhotoActivity.this.getApplicationContext(), LocalSettings.getOrderId(EditPhotoActivity.this), LocalSettings.getToken(EditPhotoActivity.this), SUtils.DEFAULT_CANCEL_ORDER_STATUS);

                    Intent intent = new Intent(EditPhotoActivity.this, QRActivity.class);
                    startActivity(intent);

                    finish();
                }
            });
            dialog.setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    ScanFragment fragment = ((ScanFragment) getFragmentManager().findFragmentByTag(ScanActivity.FRAGMENT_TAG));
                    fragment.hideMenuScreen();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        public void checkBitmapBrightness(Bitmap bitmap) {
            BrightnessAsyncTask brightnessAsyncTask = new BrightnessAsyncTask(bitmap, BRIGHTNESS_THRESHOLD_PERCENT);
            brightnessAsyncTask.execute();
        }

        @Override
        protected void showProgressBar(String messageText) {
            progressDialog = new ProgressDialog(this);
            //progressDialog.setMessage(this.getString(R.string.please_wait_get_brightness));
            progressDialog.setMessage(messageText);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void hideProgressBar() {
            if(progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        protected void onDestroy() {
            hideProgressBar();
            super.onDestroy();
        }

        private class BrightnessAsyncTask extends AsyncTask<Void, Void, Integer> {

            private Bitmap bitmap;
            private float threshold;

            public BrightnessAsyncTask(Bitmap bitmap, float threshold) {
                this.bitmap = bitmap;
                this.threshold = threshold;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressBar(EditPhotoActivity.this.getString(R.string.please_wait_get_brightness));
            }

            @Override
            protected Integer doInBackground(Void... params) {
                int darkness =  detectDarkPicture(bitmap, threshold);
                if(darkness == IMAGE_DARK) {
                    return darkness;
                }else {
                    return detectBlur(bitmap);
                }
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                hideProgressBar();
                if(result == IMAGE_DARK){
                    showDarkPictureMessage();
                }else if(result==IMAGE_BLUR){
                    showBlurPictureMessage();
                }else{
                    ScanFragment fragment = ((ScanFragment) getFragmentManager().findFragmentByTag(ScanActivity.FRAGMENT_TAG));
                    fragment.onImageCheckSuccess();
                }
            }
        }

        private int detectDarkPicture(Bitmap bitmap, float threshold){
            int result;

            //Log.d(TAG, "checkBitmapBrightness start ");
            //Log.d(TAG, "-----------------------------");

            int histogram[] = new int[256];

            for (int i=0;i<256;i++) {
                histogram[i] = 0;
            }

            for (int x = 0; x < bitmap.getWidth(); x++) {
                for(int y = 0; y < bitmap.getHeight(); y++) {
                    int pixel = bitmap.getPixel(x, y);

                    int r = Color.red(pixel);
                    int g = Color.green(pixel);
                    int b = Color.blue(pixel);

                    int brightness = (int) (0.2126*r + 0.7152*g + 0.0722*b);
                    histogram[brightness]++;

                    //Log.d(TAG, "brightness " + brightness);
                }
            }

            int allPixelsCount = bitmap.getWidth() * bitmap.getHeight();

            // Count pixels with brightness less then 20 //default 10
            int darkPixelCount = 0;
            for (int i=0; i<EditPhotoActivity.BRIGHTNESS_THRESHOLD; i++) {
                darkPixelCount += histogram[i];
            }

            if (darkPixelCount > allPixelsCount * threshold){
                //Log.d(TAG, "Dark picture");
                result = IMAGE_DARK;
            }else{
                //Log.d(TAG, "Light picture");
                result = IMAGE_NORMAL;
            }

            //Log.d(TAG, "darkPixelCount/allPixelsCount " + (float)((float)darkPixelCount/(float)allPixelsCount));

            //Log.d(TAG, "darkPixelCount " + darkPixelCount);
            //Log.d(TAG, "allPixelsCount " + allPixelsCount);
            //Log.d(TAG, "checkBitmapBrightness end ");

            return result;
        }

        private Integer detectBlur(Bitmap image){

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


            Log.d(TAG, "maxLap " + maxLap);
            Log.d(TAG, "soglia " + soglia);

            if (maxLap < soglia || maxLap == soglia) {
                //Log.d(TAG, "blur image");
                return IMAGE_BLUR;
            }else{
                //Log.d(TAG, "not blur image");
                return IMAGE_NORMAL;
            }
        }

        private void showDarkPictureMessage() {

            android.app.AlertDialog.Builder darkPictureDialog = new android.app.AlertDialog.Builder(this);
            darkPictureDialog.setMessage(this.getString(R.string.dark_picture_message));
            darkPictureDialog.setNeutralButton(this.getString(R.string.retake), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    retakePhoto();
                }
            });
            darkPictureDialog.setCancelable(false);
            darkPictureDialog.show();
        }

        private void showBlurPictureMessage() {

            android.app.AlertDialog.Builder blurPictureDialog = new android.app.AlertDialog.Builder(this);
            blurPictureDialog.setMessage(this.getString(R.string.blur_picture_message));
            blurPictureDialog.setNeutralButton(this.getString(R.string.retake), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    retakePhoto();
                }
            });
            blurPictureDialog.setCancelable(false);
            blurPictureDialog.show();
        }

        private void retakePhoto() {
            launchTakePhotoActivity();
            finish();
        }

        private void nextScreen() {
            if (LocalSettings.getResumePageReached(EditPhotoActivity.this)) {
                finish();
            } else {

                int currentPhoto = LocalSettings.getCurrentDocNumber(EditPhotoActivity.this);

                if (currentPhoto < DatabaseHelper.getTotalDocuments()) {
                    currentPhoto++;
                    LocalSettings.setCurrentDocNumber(EditPhotoActivity.this, currentPhoto);
                    launchTakePhotoActivity();
                } else {
                    launchResumeActivity();
                }
                finish();
            }
        }

        private void launchTakePhotoActivity() {
            Intent intent = new Intent(EditPhotoActivity.this, TakePhotoActivity.class);
            startActivity(intent);
        }

        private void launchResumeActivity() {
            Intent intent = new Intent(EditPhotoActivity.this, ResumeActivity.class);
            startActivity(intent);
        }

        @Override
        public void callOperator() {
            SUtils.callOperator(this);
        }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
