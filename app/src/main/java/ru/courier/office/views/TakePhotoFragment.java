package ru.courier.office.views;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;

import java.io.ByteArrayOutputStream;
import java.util.List;

import ru.courier.office.R;
import ru.courier.office.core.Document;
import ru.courier.office.core.LocalSettings;
import ru.courier.office.core.OrientationManager;
import ru.courier.office.core.Scan;
import ru.courier.office.core.ScanStatus;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.WebContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TakePhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TakePhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TakePhotoFragment extends Fragment {

    private static final String TAG = "rlf_app";
    private View view;
    CameraView mCameraView;
    RelativeLayout rlTakePhoto;
    ImageView rlTakePhotoMask;
    //ImageView grid;
    RelativeLayout menuScreen;
    RelativeLayout rlFlash;
    ImageView flashIcon;
    boolean pictureTaken = false;
    private ProgressDialog progressDialog;
    OrientationManager orientationManager;
    int currentOrientation = OrientationManager.ScreenOrientation.PORTRAIT.ordinal();

    private static final String ARG_DOCUMENT_ID = "documentId";
    private static final String ARG_APPLICATION_ID = "applicationId";

    private String applicationId;
    private int documentId;

    public TakePhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param documentId DocumentId
     * @param applicationId ApplicationId
     * @return A new instance of fragment TakePhotoFragment.
     */
    public static TakePhotoFragment newInstance(int documentId, String applicationId) {
        TakePhotoFragment fragment = new TakePhotoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DOCUMENT_ID, documentId);
        args.putString(ARG_APPLICATION_ID, applicationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            documentId = getArguments().getInt(ARG_DOCUMENT_ID);
            applicationId = getArguments().getString(ARG_APPLICATION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_take_photo, container, false);

        orientationManager = new OrientationManager(getContext(), SensorManager.SENSOR_DELAY_NORMAL, new OrientationManager.OrientationListener() {
            @Override
            public void onOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {
                currentOrientation = screenOrientation.ordinal();
            }
        });

        orientationManager.enable();

        initViews();

        return view;
    }


 private void initViews() {

     mCameraView = (CameraView) view.findViewById(R.id.camera);
     if (mCameraView != null) {
         //mCameraView.setFlash(CameraView.FLASH_AUTO);
         //mCameraView.setAspectRatio(AspectRatio.of(4,3));
         mCameraView.addCallback(mCallback);
         mCameraView.start();
     }

     //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(300, 150);
     //mCameraView.setLayoutParams(layoutParams);

     rlTakePhoto = (RelativeLayout) view.findViewById(R.id.rlTakePhoto);
     rlTakePhoto.setOnClickListener(clickListener);

     rlTakePhotoMask = (ImageView) view.findViewById(R.id.rlTakePhotoMask);

     flashIcon = (ImageView) view.findViewById(R.id.flashIcon);

     rlFlash = (RelativeLayout) view.findViewById(R.id.rlFlash);

     rlFlash.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             switch (mCameraView.getFlash()) {
                 case CameraView.FLASH_AUTO:
                     mCameraView.setFlash(CameraView.FLASH_ON);
                     flashIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on));
                     break;
                 case CameraView.FLASH_ON:
                     mCameraView.setFlash(CameraView.FLASH_OFF);
                     flashIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off));
                     break;
                 case CameraView.FLASH_OFF:
                     mCameraView.setFlash(CameraView.FLASH_AUTO);
                     flashIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_auto));
                     break;
             }
         }
     });


     setInfo();


     //TODO move menuscreen to separate activity or fragment
//     menuScreen = (RelativeLayout) view.findViewById(R.id.menuScreen);
//     menuScreen.setVisibility(View.INVISIBLE);
//
//     ImageView infoIcon = (ImageView) view.findViewById(R.id.infoIcon);
//     infoIcon.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View view) {
//             showMenuScreen();
//         }
//     });
//
//     ImageView infoIconClose = (ImageView) view.findViewById(R.id.infoIconClose);
//     infoIconClose.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View view) {
//             hideMenuScreen();
//         }
//     });
//
//     RelativeLayout rlSkip = (RelativeLayout) view.findViewById(R.id.rlSkip);
//     rlSkip.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View view) {
//             //hideMenuScreen();
//             cancelOrder();
//         }
//     });
//
//
//     RelativeLayout rlCall = (RelativeLayout) view.findViewById(R.id.rlCall);
//     rlCall.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View view) {
//             hideMenuScreen();
//             callOperator();
//         }
//     });

     //grid = (ImageView) view.findViewById(R.id.grid);
 }

//        private void callOperator() {
//            SUtils.callOperator(this);
//        }

        private void showMenuScreen() {
            menuScreen.setVisibility(View.VISIBLE);
        }

        private void hideMenuScreen() {
            menuScreen.setVisibility(View.INVISIBLE);
        }

        private void setInfo() {
            pictureTaken = false;
            setTakePhotoButton(true);

            TextView currentPhotoNum = (TextView) view.findViewById(R.id.currentPhotoNum);
            TextView totalNum = (TextView) view.findViewById(R.id.totalNum);
            TextView pageTitle = (TextView) view.findViewById(R.id.pageTitle);

            int currentDoc = LocalSettings.getCurrentDocNumber(getContext());
            long totalDoc = 8; //DatabaseHelper.getTotalDocuments();
            int currentPage = LocalSettings.getCurrentPage(getContext());

            currentPhotoNum.setText(Integer.toString(currentDoc));
            totalNum.setText("/ " + Long.toString(totalDoc));

            //String[] pageTitles = getResources().getStringArray(R.array.photo_page_titles);
            //String titleText = pageTitles[currentDoc-1];

            String titleText = "ImageTitle"; //SUtils.getPictureTitle(currentDoc, currentPage);
            pageTitle.setText(titleText);

            saveEnterTime(currentDoc);
        }

        private void setTakePhotoButton(boolean enabled) {

            rlTakePhoto.setClickable(enabled);
            rlTakePhoto.setFocusable(enabled);
            rlTakePhoto.setEnabled(enabled);

            if(enabled){
                rlTakePhotoMask.setVisibility(View.INVISIBLE);
            }else{
                rlTakePhotoMask.setVisibility(View.VISIBLE);
            }
        }


        private void saveEnterTime(int photoNum) {
            //String dateStr = RequestUtils.getCurrentDateFormatted();
            //LocalSettings.setEnterTime(getContext(), photoNum, dateStr);
        }

        private void cancelOrder() {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext());
            dialog.setMessage(this.getString(R.string.camera_not_available));
            dialog.setPositiveButton(this.getString(R.string.camera_not_available), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    //SUtils.clearSettings(TakePhotoActivity.this);
                    //SUtils.removeAllTemporaryFiles(TakePhotoActivity.this);
                    //SUtils.removeOrderEncodedFiles(TakePhotoActivity.this, LocalSettings.getOrderId(TakePhotoActivity.this));
                    //ImageLoader.getInstance().clearMemoryCache();

                    //RequestUtils.sendCancelOrderStatus(TakePhotoActivity.this);
                    //RequestUtils.sendFinalStatus(TakePhotoActivity.this.getApplicationContext(), LocalSettings.getOrderId(TakePhotoActivity.this), LocalSettings.getToken(TakePhotoActivity.this), SUtils.DEFAULT_CANCEL_ORDER_STATUS);

                    //Intent intent = new Intent(TakePhotoActivity.this, QRActivity.class);
                    //startActivity(intent);

                    //finish();
                }
            });
            dialog.setNegativeButton(this.getString(R.string.camera_not_available), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    hideMenuScreen();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }

        private CameraView.Callback mCallback = new CameraView.Callback() {

            @Override
            public void onCameraOpened(CameraView cameraView) {
                //Log.d(TAG, "onCameraOpened");
            }

            @Override
            public void onCameraClosed(CameraView cameraView) {
                //Log.d(TAG, "onCameraClosed");
            }

            @Override
            public void onPictureTaken(final CameraView cameraView, byte[] data) {
                //Log.d(TAG, "onPictureTaken " + data.length);
                //Toast.makeText(cameraView.getContext(), R.string.picture_taken, Toast.LENGTH_SHORT).show();

                SavePhotoAsyncTask savePhotoAsyncTask = new SavePhotoAsyncTask(data);
                savePhotoAsyncTask.execute();
            }

        };

        private void showProgressBar() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(this.getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        private void hideProgressBar() {
            if(progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }




    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (mCameraView != null && !pictureTaken) {

                LocalSettings.setDeviceOrientationDuringTakePhoto(getContext(), currentOrientation);

                float frameRatio = (float) mCameraView.getHeight() / (float) mCameraView.getWidth();
                LocalSettings.setFrameRatio(getContext(), frameRatio);

                pictureTaken = true;
                setTakePhotoButton(false);

                mCameraView.takePicture();
            }
        }
    };

private class SavePhotoAsyncTask extends AsyncTask<Void, Void, Boolean> {

    byte[] imageBytes;

    public SavePhotoAsyncTask(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProgressBar();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        DataAccess dataAccess = DataAccess.getInstance(getContext());
        WebContext current = WebContext.getInstance();
        applicationId = current.Application.ApplicationId;
        List<Document> documents = dataAccess.getDocumentsByApplicationId(applicationId);
        Document document = documents.get(0);
        documentId = document.Id;

        Scan scan = new Scan();
        scan.ApplicationId = applicationId;
        scan.DocumentId = documentId;
        int count = dataAccess.countScansByDocumentId(documentId);
        scan.Page = ++count;
        scan.ScanStatus = ScanStatus.Created;

        byte[] smallPhoto = resizeBitmap(imageBytes);
        byte[] largePhoto = imageBytes;

        scan.SmallPhoto = smallPhoto;
        scan.LargePhoto = largePhoto;

        int result = dataAccess.insertScan(scan);
        return result > 0;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        hideProgressBar();
        if (result) {
            launchScanActivity();
        } else {
            Toast.makeText(getContext(), R.string.camera_not_available, Toast.LENGTH_SHORT).show();
            setInfo();
        }
    }
}

    public byte[] resizeBitmap(byte[] inputBytes) {

        int inputBytesLength = inputBytes.length;

        int targetW = 100;
        int targetH = 100;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        Bitmap inputBitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.length, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated API 21

        Bitmap outputBitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.length, bmOptions);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        outputBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] outputBytes = stream.toByteArray();

        int outputBytesLength = outputBytes.length;

        return outputBytes;
    }


    private void launchScanActivity() {

        mCameraView.removeCallback(mCallback);
        mCameraView.stop();
        mCameraView = null;
        initViews();

        //Intent intent = new Intent(TakePhotoActivity.this, EditPhotoActivity.class);

        //intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);
        //intent.putExtra(PICTURE_URI, uri.toString());
        //startActivityForResult(intent, REQUEST_CODE);

        //startActivity(intent);

        //finish();
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
