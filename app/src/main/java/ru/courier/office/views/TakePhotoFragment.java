package ru.courier.office.views;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.courier.office.R;
import ru.courier.office.core.Application;
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

    private static final String ARG_APPLICATION_ID = "applicationId";
    private static final String ARG_DOCUMENT_ID = "documentId";
    private static final String ARG_SCAN_ID = "scanId";
    private int _applicationId;
    private int documentId;
    private int _scanId;
    private View view;
    private CameraView mCameraView;
    private RelativeLayout rlTakePhoto;
    private RelativeLayout menuScreen;
    private RelativeLayout rlFlash;
    private ImageView flashIcon;
    boolean pictureTaken = false;
    private ProgressDialog progressDialog;
    private OrientationManager orientationManager;
    private int currentOrientation = OrientationManager.ScreenOrientation.PORTRAIT.ordinal();
    private DataAccess _dataAccess;
    private Application _application;
    private Document _currentDocument;
    private Toolbar _toolbar;
    private int _totalDocs;
    private int _currentDoc = 0;
    private int _currentScan = 0;
    private static final int IMAGE_NORMAL = 0;
    private static final int IMAGE_DARK = 1;
    private static final int IMAGE_BLUR = 2;
    private static final float BRIGHTNESS_THRESHOLD_PERCENT = .25f;
    public static final int BRIGHTNESS_THRESHOLD = 20;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param documentId DocumentId
     * @param applicationId ApplicationId
     * @return A new instance of fragment TakePhotoFragment.
     */
    public static TakePhotoFragment newInstance(int applicationId, int documentId, int scanId) {
        TakePhotoFragment fragment = new TakePhotoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_APPLICATION_ID, applicationId);
        args.putInt(ARG_DOCUMENT_ID, documentId);
        args.putInt(ARG_SCAN_ID, scanId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _applicationId = getArguments().getInt(ARG_APPLICATION_ID);
            documentId = getArguments().getInt(ARG_DOCUMENT_ID);
            _scanId = getArguments().getInt(ARG_SCAN_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_take_photo, container, false);
        _toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        orientationManager = new OrientationManager(getContext(), SensorManager.SENSOR_DELAY_NORMAL, new OrientationManager.OrientationListener() {
            @Override
            public void onOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {
                currentOrientation = screenOrientation.ordinal();
            }
        });
        orientationManager.enable();

        WebContext webContext = WebContext.getInstance();
        _dataAccess = DataAccess.getInstance(getContext());

        if (_applicationId > 0) {
            _application = _dataAccess.getApplicationById(_applicationId);
            webContext.Application = _application;
            _application.DocumentList = _dataAccess.getDocumentsByApplicationGuid(_application.ApplicationGuid);
            _totalDocs = _application.DocumentList.size();
        }

        setCurrentDocument();
        setCurrentScan();
        initViews();

        return view;
    }

    private void setCurrentDocument() {
        _currentDocument = _application.DocumentList.get(_currentDoc);
        if (_totalDocs > _currentDoc) {
            _currentDoc++;
        }
    }

    private void setCurrentScan() {

        if (_currentScan == 0) {
            _currentScan = _dataAccess.countScansByDocumentId(_currentDocument.Id);
        }
        ++_currentScan;
        String title = String.format("%d. %s", _currentScan, _currentDocument.Title);
        _toolbar.setTitle(title);
    }

    private void initViews() {
        dispose();
        mCameraView = (CameraView) view.findViewById(R.id.cvCamera);
        if (mCameraView != null) {
            //mCameraView.setFlash(CameraView.FLASH_AUTO);
            //mCameraView.setAspectRatio(AspectRatio.of(4,3));
            mCameraView.addCallback(mCallback);
            mCameraView.start();
        }

        rlTakePhoto = (RelativeLayout) view.findViewById(R.id.rlTakePhoto);
        rlTakePhoto.setOnClickListener(clickListener);

        //rlTakePhotoMask = (ImageView) view.findViewById(R.id.rlTakePhotoMask);

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
        //menuScreen = (RelativeLayout) view.findViewById(R.id.menuScreen);
        //menuScreen.setVisibility(View.INVISIBLE);

//        ImageView infoIcon = (ImageView) view.findViewById(R.id.infoIcon);
//        infoIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showMenuScreen();
//            }
//        });

//        ImageView infoIconClose = (ImageView) view.findViewById(R.id.infoIconClose);
//        infoIconClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                hideMenuScreen();
//            }
//        });

//        RelativeLayout rlSkip = (RelativeLayout) view.findViewById(R.id.rlSkip);
//        rlSkip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //hideMenuScreen();
//                cancelOrder();
//            }
//        });


//        RelativeLayout rlCall = (RelativeLayout) view.findViewById(R.id.rlCall);
//        rlCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                hideMenuScreen();
//                callOperator();
//            }
//        });
    }

    private void callOperator() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", LocalSettings.OPERATOR_PHONE_NUMBER, null));
        getActivity().startActivityForResult(intent, 99);
    }

    private void showMenuScreen() {
            menuScreen.setVisibility(View.VISIBLE);
    }

    private void hideMenuScreen() {
            menuScreen.setVisibility(View.INVISIBLE);
        }

    private void setInfo() {
            pictureTaken = false;
            setTakePhotoButton(true);



            //TextView currentPhotoNum = (TextView) view.findViewById(R.id.currentPhotoNum);
            //TextView totalNum = (TextView) view.findViewById(R.id.totalNum);
            //TextView pageTitle = (TextView) view.findViewById(R.id.pageTitle);

            //int currentDoc = LocalSettings.getCurrentDocNumber(getContext());
            //long totalDoc = 8; //DatabaseHelper.getTotalDocuments();
            //int currentPage = LocalSettings.getCurrentPage(getContext());

            //currentPhotoNum.setText(Integer.toString(currentDoc));
            //totalNum.setText("/ " + Long.toString(totalDoc));

            //String[] pageTitles = getResources().getStringArray(R.array.photo_page_titles);
            //String titleText = pageTitles[currentDoc-1];

            //String titleText = "ImageTitle"; //SUtils.getPictureTitle(currentDoc, currentPage);
            //pageTitle.setText(titleText);

            //saveEnterTime(currentDoc);
        }

    private void setTakePhotoButton(boolean enabled) {

            rlTakePhoto.setClickable(enabled);
            rlTakePhoto.setFocusable(enabled);
            rlTakePhoto.setEnabled(enabled);

//            if(enabled){
//                rlTakePhotoMask.setVisibility(View.INVISIBLE);
//            }else{
//                rlTakePhotoMask.setVisibility(View.VISIBLE);
//            }
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

    private byte[] _imageBytes;

    public SavePhotoAsyncTask(byte[] imageBytes) {
        _imageBytes = imageBytes;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProgressBar();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        //progressDialog.setMessage(getString(R.string.please_wait_while_prepare_image));

        Scan scan = new Scan();
        scan.ApplicationGuid = _application.ApplicationGuid;
        scan.DocumentGuid = _currentDocument.DocumentGuid;
        scan.DocumentId = _currentDocument.Id;
        scan.PageNum = _currentScan;
        scan.ScanStatus = ScanStatus.None;
        scan.ImageLength = _imageBytes.length;
        scan.SmallPhoto = resizeBitmap(_imageBytes);
        scan.LargePhoto = drawDate(_imageBytes);

        //progressDialog.setMessage(getString(R.string.please_wait_while_process_brightness));
        Bitmap inputBitmap = BitmapFactory.decodeByteArray(scan.SmallPhoto, 0, scan.SmallPhoto.length);

        int brightness = defineBrightness(inputBitmap);
        if(brightness == IMAGE_NORMAL) {
            //progressDialog.setMessage(getString(R.string.please_wait_while_process_blur));

                _scanId = _dataAccess.insertScan(scan);

        }

        return _scanId > 0;
    }

    public byte[] resizeBitmap(byte[] inputBytes) {
        int inputBytesLength = inputBytes.length;
        int targetW = 200;
        int targetH = 200;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        Bitmap inputBitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.length, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
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

    private byte[] drawDate(byte[] inputBytes) {
        Bitmap inputBitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.length);
        String dateStr = new SimpleDateFormat("dd.MM.yyyy", Locale.US).format(new Date());
        String timeStr = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
        int lineHeight = (int) Math.round(inputBitmap.getWidth() * .14);
        long textSize = Math.round(0.5 * lineHeight);
        long textMargin = Math.round(inputBitmap.getWidth() * .08);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap tempBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), lineHeight, conf);
        Canvas canvas = new Canvas(tempBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE); // back Color
        canvas.drawRect(0, 0, inputBitmap.getWidth(), lineHeight, paint);
        paint.setColor(Color.BLACK); // Text Color
        paint.setTextSize(textSize); // Text Size
        paint.setFakeBoldText(true);
        long dateTextWidth = Math.round(paint.measureText(dateStr));
        long timeTextWidth = Math.round(paint.measureText(timeStr));
        long timeStrX = inputBitmap.getWidth() - timeTextWidth - textMargin;
        if (timeStrX < (textMargin * 2 + dateTextWidth))
            timeStrX = (textMargin * 2 + dateTextWidth);
        canvas.drawText(dateStr, textMargin, textSize + (lineHeight - textSize) / 2, paint);
        canvas.drawText(timeStr, timeStrX, textSize + (lineHeight - textSize) / 2, paint);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight() + lineHeight, inputBitmap.getConfig());
        Canvas canvas1 = new Canvas(outputBitmap);
        canvas1.drawBitmap(inputBitmap, null, new RectF(0, 0, inputBitmap.getWidth(), inputBitmap.getHeight()), null);
        canvas1.drawBitmap(tempBitmap, null, new RectF(0, inputBitmap.getHeight(), inputBitmap.getWidth(), inputBitmap.getHeight() + lineHeight), null);
        inputBitmap.recycle();
        tempBitmap.recycle();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        outputBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] outputBytes = stream.toByteArray();
        return outputBytes;
    }

    private Integer defineBrightness(Bitmap inputBitmap) {
        int result;
        Date d1 = new Date();


        int histogram[] = new int[256];

        for (int i = 0; i < 256; i++) {
            histogram[i] = 0;
        }

        int bitmapWidth = inputBitmap.getWidth();
        int bitmapHeight = inputBitmap.getHeight();
        int totalPixels = bitmapWidth * bitmapHeight;

        int counter = 0;

        for (int x = 0; x < bitmapWidth; x++) {
            for (int y = 0; y < bitmapHeight; y++) {
                int pixel = inputBitmap.getPixel(x, y);

                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);

                int brightness = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
                histogram[brightness]++;
                counter++;
            }
        }

        // Count pixels with brightness less then 20 //default 10
        int darkPixelCount = 0;
        for (int i = 0; i < BRIGHTNESS_THRESHOLD; i++) {
            darkPixelCount += histogram[i];
        }

        if (darkPixelCount > totalPixels * BRIGHTNESS_THRESHOLD_PERCENT) {
            result = IMAGE_DARK;
        } else {
            result = IMAGE_NORMAL;
        }

        Date d2 = new Date();
        long diff = d2.getTime() - d1.getTime();

        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        hideProgressBar();

        setCurrentScan();

        if (result) {

            //FragmentManager fm = getFragmentManager();
            //FragmentTransaction ft = fm.beginTransaction();
            //EditPhotoFragment editPhotoFragment = EditPhotoFragment.newInstance(_scanId);
            //ft.replace(R.id.container, editPhotoFragment);
            //ft.commit();

            dispose();
        } else {
            Toast.makeText(getContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            setInfo();
        }
    }
}
    private void dispose() {
        if (mCameraView != null) {
            mCameraView.removeCallback(mCallback);
            mCameraView.stop();
            mCameraView = null;
        }
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
