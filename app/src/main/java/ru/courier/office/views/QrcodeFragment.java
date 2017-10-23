package ru.courier.office.views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import ru.courier.office.R;
import ru.courier.office.core.Application;
import ru.courier.office.core.CameraPreview;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.ApplicationProvider;
import ru.courier.office.web.WebContext;

public class QrcodeFragment extends Fragment{

    static {System.loadLibrary("iconv");}

    private OnFragmentInteractionListener mListener;

    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0;
    public boolean showQRScannerPageOnResume = false;
    private static Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private boolean checkScanResult = true;
    private ProgressDialog progressDialog;
    private String _applicationGuid;
    private FrameLayout cameraPreview;
    private View _view;

    private DataAccess _dataAccess;
    private WebContext _webContext;
    private Context _context;

    public QrcodeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _view = inflater.inflate(R.layout.fragment_qrcode, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.tlbMain);
        toolbar.setTitle(getString(R.string.title_qrcode_fragment));
        _context = getContext();

        _dataAccess = DataAccess.getInstance(getContext());
        _webContext = WebContext.getInstance();

        initCamera();
        return _view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        initCamera();
    }

    private void initCamera() {

        cameraPreview = (FrameLayout) _view.findViewById(R.id.cameraPreview);

        if (!backCameraExists()) {
            showNoCameraDialog();
        }

        boolean permissionCameraGranted = (ContextCompat.checkSelfPermission(_context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (!permissionCameraGranted) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_REQUEST_CODE);
            return;
        }

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        if (mCamera == null) {
            showNoCameraDialog();
            return;
        }

        // Instance barcode scanner
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(getContext(), mCamera, previewCallback, autoFocusCB);
        cameraPreview.addView(mPreview);

        startScan();
    }


    public void showNoCameraDialog() {
        AlertDialog noCameraDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom)
                .setTitle(R.string.error)
                .setMessage(R.string.camera_not_available)
                .setIcon(R.drawable.ic_error)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                }).create();
        noCameraDialog.show();
    }

    private static boolean backCameraExists() {

        int backCameraId = -1;
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                backCameraId = i;
            }
        }
        return (backCameraId > -1);
    }

    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    String applicationGuid = sym.getData().trim();
                    barcodeScanned = true;
                    if (checkScanResult) {
                        checkScanResult = false;
                        checkBarCode(applicationGuid);
                    }
                    break;
                }
            }
        }
    };

    private void checkBarCode(String applicationGuid) {

        _applicationGuid = applicationGuid;

        Application application = _dataAccess.getApplicationByApplicationGuid(_applicationGuid);
        if (application != null) {
            String message = String.format("Заявка %s уже зарегистрирована в базе данных приложения", _applicationGuid);
            ApplicationExistsDialog(message).show();
            setFragment(new NoteFragment());

        } else {
            disposeCamera();
            ApplicationAsyncTask applicationAsyncTask = new ApplicationAsyncTask(_applicationGuid);
            applicationAsyncTask.execute();
        }
    }

    private AlertDialog ApplicationExistsDialog(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom)
                .setTitle("Заявка уже зарегистрирована")
                .setMessage(message)
                .setIcon(R.drawable.ic_error)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                }).create();
        return alertDialog;
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            scheduleAutoFocus();
        }
    };


    private void scheduleAutoFocus() {
        autoFocusHandler.postDelayed(doAutoFocus, 2000);
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing) {
                //mCamera.autoFocus(autoFocusCB);
                boolean autoFocusStarted = false;
                if (mPreview != null && mPreview.isSurfaceCreated()) { // check if surface created before using autofocus
                    try {
                        mCamera.autoFocus(autoFocusCB);
                        autoFocusStarted = true;
                    } catch (Exception e) {
                        Log.d("rlf_app", "Error starting autoFocus " + e.getMessage());
                    }
                }

                if (!autoFocusStarted) scheduleAutoFocus(); // wait 2 sec and then do check again
            }
        }
    };

    private void startScan() {
        barcodeScanned = false;
        mCamera.setPreviewCallback(previewCallback);
        mCamera.startPreview();
        previewing = true;

        //mCamera.autoFocus(autoFocusCB);
        boolean autoFocusStarted = false;
        if (mPreview != null && mPreview.isSurfaceCreated()) { // check if surface created before using autofocus
            try {
                mCamera.autoFocus(autoFocusCB);
                autoFocusStarted = true;
            } catch (Exception e) {
                Log.d("rlf_app", "Error starting autoFocus " + e.getMessage());
            }
        }
        //Log.d("rlf_app", "autoFocusStarted " + autoFocusStarted);
        if (!autoFocusStarted) scheduleAutoFocus(); // wait 2 sec and then do check again
    }

    public void disposeCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        //Camera camera = null;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCamera;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class ApplicationAsyncTask extends AsyncTask<Void, Void, Void> {

        private String _qrCodeValue;
        private int applicationId;
        private int responseCode;

        public ApplicationAsyncTask(String qrCodeValue) {
            _qrCodeValue = qrCodeValue;
        }

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(_context);
            pDialog.setMessage("Пожалуйста, подождите...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String postData = String.format("{\"ApplicationId\":\"%s\"}", _qrCodeValue);
            ApplicationProvider applicationProvider = new ApplicationProvider();
            responseCode = applicationProvider.postApplication(postData);
            if (responseCode == 200) {
                applicationId = _dataAccess.addApplication(_webContext.Application);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void output) {
            super.onPostExecute(output);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (responseCode == 200) {
                onScanSuccessDialog().show();
                return;
            } else if (responseCode == 400) {
                ApplicationFailed().show();
            } else {
                ConnectionFailed().show();
            }
        }

        private AlertDialog onScanSuccessDialog() {
            AlertDialog alertDialog = new AlertDialog.Builder(_context, R.style.AlertDialogCustom)
                    .setTitle("Сканирование заявки")
                    .setMessage("Вы желаете продолжить сканирование заявки сейчас?")
                    .setIcon(R.drawable.ic_question)
                    .setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                            TakePhotoFragment fragment = TakePhotoFragment.newInstance(applicationId, 0, 0);
                            showFragment(fragment);

                        }
                    }).setNegativeButton("Отложить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                            AppListFragment fragment = new AppListFragment();
                            showFragment(fragment);
                        }
                    }).create();
            return alertDialog;
        }

        private AlertDialog ApplicationFailed() {
            AlertDialog alertDialog = new AlertDialog.Builder(_context, R.style.AlertDialogCustom)
                    .setTitle("Заявка недействительна")
                    .setMessage("Кредитная заявка недействительна")
                    .setIcon(R.drawable.ic_error)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                            AppListFragment fragment = new AppListFragment();
                            showFragment(fragment);
                        }

                    }).create();
            return alertDialog;
        }

        private AlertDialog ConnectionFailed() {
            AlertDialog alertDialog = new AlertDialog.Builder(_context, R.style.AlertDialogCustom)
                    .setTitle(R.string.connection_failed)
                    .setMessage(R.string.connection_not_available)
                    .setIcon(R.drawable.ic_error)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                            AppListFragment fragment = new AppListFragment();
                            showFragment(fragment);
                        }

                    }).create();
            return alertDialog;
        }
    }

    private void showFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = ((AppCompatActivity) _context).getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        }
    }
}
