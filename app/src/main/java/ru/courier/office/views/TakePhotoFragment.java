package ru.courier.office.views;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

public class TakePhotoFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public static final int BRIGHTNESS_THRESHOLD = 20;
    private static final String ARG_APPLICATION_ID = "applicationId";
    private static final String ARG_DOCUMENT_ID = "documentId";
    private static final String ARG_SCAN_ID = "scanId";
    private static final int IMAGE_NORMAL = 0;
    private static final int IMAGE_DARK = 1;
    private static final int IMAGE_BLUR = 2;
    private static final float BRIGHTNESS_THRESHOLD_PERCENT = .25f;
    boolean pictureTaken = false;
    private int _applicationId;
    private int _documentId;
    private int _scanId;
    private View _view;
    private Context _context;
    private CameraView mCameraView;
    private RelativeLayout rlTakePhoto;
    private RelativeLayout menuScreen;
    private RelativeLayout rlFlash;
    private ImageView flashIcon;
    private ProgressDialog progressDialog;
    private OrientationManager orientationManager;
    private int currentOrientation = OrientationManager.ScreenOrientation.PORTRAIT.ordinal();

    private DataAccess _dataAccess;
    private Application _application;
    private Document _currentDocument;
    private Scan _scan;
    private Toolbar _toolbar;
    private int _totalDocs;
    private int _currentDocIndex = 0;
    private int _currentScanIndex = 0;
    private TextView _tvTitle;
            
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
            _documentId = getArguments().getInt(ARG_DOCUMENT_ID);
            _scanId = getArguments().getInt(ARG_SCAN_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _view = inflater.inflate(R.layout.fragment_take_photo, container, false);
        _toolbar = (Toolbar) getActivity().findViewById(R.id.tlbMain);
        _toolbar.setVisibility(View.GONE);

        WebContext webContext = WebContext.getInstance();
        _dataAccess = DataAccess.getInstance(getContext());
        _context = getContext();

        ImageView ivPrev = (ImageView) _view.findViewById(R.id.ivPrev);
        ivPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPrevDocument();
            }
        });

        ImageView ivNext = (ImageView) _view.findViewById(R.id.ivNext);
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNextDocument();
            }
        });

        _tvTitle = (TextView) _view.findViewById(R.id.tvTitle);

        orientationManager = new OrientationManager(_context, SensorManager.SENSOR_DELAY_NORMAL, new OrientationManager.OrientationListener() {
            @Override
            public void onOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {
                currentOrientation = screenOrientation.ordinal();
            }
        });
        orientationManager.enable();

        if (_applicationId > 0) {
            _application = _dataAccess.getApplicationById(_applicationId);
            webContext.Application = _application;
            _application.DocumentList = _dataAccess.getDocumentsByApplicationGuid(_application.ApplicationGuid);
            _totalDocs = _application.DocumentList.size() - 1;
        }

        if (_documentId > 0) {
            Document document = _dataAccess.getDocumentById(_documentId);
            _applicationId = document.ApplicationId;
            _application = _dataAccess.getApplicationById(_applicationId);
            webContext.Application = _application;
            _application.DocumentList = _dataAccess.getDocumentsByApplicationGuid(_application.ApplicationGuid);
            _totalDocs = _application.DocumentList.size() - 1;
        }

        if (_scanId > 0) {
            _scan = _dataAccess.getScanById(_scanId);
            _applicationId = _scan.ApplicationId;
            _application = _dataAccess.getApplicationById(_applicationId);
            webContext.Application = _application;
            _application.DocumentList = _dataAccess.getDocumentsByApplicationGuid(_application.ApplicationGuid);
            _totalDocs = _application.DocumentList.size() - 1;

            for (Document document : _application.DocumentList) {
                _currentDocument = _application.DocumentList.get(_currentDocIndex);
                if (document.Id == _scan.DocumentId) {
                    break;
                }
                _currentDocIndex = _currentDocIndex + 1;
            }

            List<Scan> scanList = _dataAccess.getScansByDocumentId(_currentDocument.Id);
            for (Scan scan : scanList) {
                _currentScanIndex = _currentScanIndex + 1;
                if (scan.Id == _scanId) {
                    break;
                }
            }
        }

        setCurrentDocument();
        initCamera();

        return _view;
    }

    private void setPrevDocument() {
        if (_currentDocIndex > 0) {
            _currentDocIndex = _currentDocIndex - 1;
        }
        setCurrentDocument();
    }

    private void setNextDocument() {
        if (_currentDocIndex < _totalDocs) {
            _currentDocIndex = _currentDocIndex + 1;
        }
        setCurrentDocument();
    }

    private void setTitle() {
        String title = String.format("%d. %s", _currentScanIndex, _currentDocument.Title);
        _tvTitle.setText(title);
    }

    private void setCurrentDocument() {
        _currentDocument = _application.DocumentList.get(_currentDocIndex);

        if (_scanId == 0) {
            _currentScanIndex = _dataAccess.countScansByDocumentId(_currentDocument.Id);
            _currentScanIndex = _currentScanIndex + 1;
        }
        setTitle();
    }

    private void initCamera() {

        mCameraView = (CameraView) _view.findViewById(R.id.cvCamera);
        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
            mCameraView.start();
        }

        rlTakePhoto = (RelativeLayout) _view.findViewById(R.id.rlTakePhoto);
        rlTakePhoto.setOnClickListener(clickListener);

        flashIcon = (ImageView) _view.findViewById(R.id.flashIcon);

        rlFlash = (RelativeLayout) _view.findViewById(R.id.rlFlash);

        rlFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
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

        pictureTaken = false;
        setTakePhotoButton(true);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (mCameraView != null && !pictureTaken) {

                LocalSettings.setDeviceOrientationDuringTakePhoto(_context, currentOrientation);

                float frameRatio = (float) mCameraView.getHeight() / (float) mCameraView.getWidth();
                LocalSettings.setFrameRatio(_context, frameRatio);

                pictureTaken = true;
                setTakePhotoButton(false);

                mCameraView.takePicture();
            }
        }
    };

    private void setTakePhotoButton(boolean state) {
        rlTakePhoto.setClickable(state);
        rlTakePhoto.setFocusable(state);
        rlTakePhoto.setEnabled(state);
    }

    private void resetCamera() {
        disposeCamera();
        setTakePhotoButton(true);
        initCamera();
    }

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

    private void disposeCamera() {
        if (mCameraView != null) {
            mCameraView.removeCallback(mCallback);
            mCameraView.stop();
            mCameraView = null;
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
        disposeCamera();
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private CameraView.Callback mCallback = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
        }

        @Override
        public void onPictureTaken(final CameraView cameraView, byte[] imageBytes) {
            SaveImageAsyncTask savePhotoAsyncTask = new SaveImageAsyncTask(imageBytes);
            savePhotoAsyncTask.execute();
        }
    };

    private class SaveImageAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private byte[] _imageBytes;

        public SaveImageAsyncTask(byte[] imageBytes) {
            _imageBytes = imageBytes;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            int imageByteslength = _imageBytes.length;
            Scan scan = new Scan();
            scan.Id = _scanId;
            scan.ApplicationId = _application.Id;
            scan.ApplicationGuid = _application.ApplicationGuid;
            scan.DocumentGuid = _currentDocument.DocumentGuid;
            scan.DocumentId = _currentDocument.Id;
            scan.PageNum = _currentScanIndex;
            scan.ScanStatus = ScanStatus.Ready;
            byte[] smallBytes = resizeBitmap(_imageBytes);
            int smallByteslength = smallBytes.length;
            scan.SmallPhoto = smallBytes;
            //byte[] totalBytes = drawDate(_imageBytes);
            //scan.LargePhoto = totalBytes;
            scan.LargePhoto = _imageBytes;
            scan.ImageLength = scan.LargePhoto.length;
            if (_scanId > 0) {
                _dataAccess.updateScanImage(scan);
                ScanListFragment scanListFragment = ScanListFragment.newInstance(_scan.DocumentId);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, scanListFragment).commit();
            } else {
                scan.Id = _dataAccess.insertScan(scan);
            }
            return scan.Id > 0;
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

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            hideProgressBar();
            setCurrentDocument();
            resetCamera();

            if (result) {

            } else {
                Toast.makeText(_context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
