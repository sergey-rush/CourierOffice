package ru.courier.office.views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import ru.courier.office.R;
import ru.courier.office.core.CameraPreview;
import ru.courier.office.core.LocalSettings;
import ru.courier.office.data.ApplicationManager;

import java.io.File;
import java.io.IOException;

import static me.dm7.barcodescanner.core.CameraUtils.getCameraInstance;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain getContext() fragment must implement the
 * {@link QrcodeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QrcodeFragment#newInstance} factory method to
 * create an instance of getContext() fragment.
 */
public class QrcodeFragment extends Fragment implements View.OnClickListener {

    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0;
    public boolean showQRScannerPageOnResume = false;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private boolean checkScanResult = true;
    private ProgressDialog progressDialog;
    private String qrCodeValue = "";
    private FrameLayout cameraPreview;

    static {
        System.loadLibrary("iconv");
    }

    private OnFragmentInteractionListener mListener;

    public QrcodeFragment() {
        // Required empty public constructor
    }

    public static QrcodeFragment newInstance() {
        QrcodeFragment fragment = new QrcodeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrcode, container, false);
        cameraPreview = (FrameLayout) view.findViewById(R.id.cameraPreview);
        if(!backCameraExists()){
            showNoCameraDialog();
        }
        showQRScanner();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //case R.id.btnCapture:
            case 1:
                onButtonCaptureClick(view);
                break;
        }
    }

    public void onButtonCaptureClick(View view) {
        Toast.makeText(view.getContext(), "onButtonCaptureClick", Toast.LENGTH_LONG).show();
    }

    public void showQRScanner() {

        boolean permissionCameraGranted = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        //Log.d("rlf_app", "permissionCameraGranted " + permissionCameraGranted);
        if(!permissionCameraGranted){
            requestCameraPermission();
            return;
        }

        initControls();
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

    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(this.getActivity(),
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_CAMERA_REQUEST_CODE);
    }

    private void initControls() {
        this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        if(mCamera == null) {
            showNoCameraDialog();
            return;
        }

        // Instance barcode scanner
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(getContext(), mCamera, previewCb, autoFocusCB);


        cameraPreview.addView(mPreview);

        startScan();
    }

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
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
                    Log.i("rlf_app", "<<<<Bar Code>>> " + sym.getData());
                    String scanResult = sym.getData().trim();
                    //Toast.makeText(getActivity(), scanResult, Toast.LENGTH_SHORT).show();
                    barcodeScanned = true;
                    //Log.i("rlf_app", "checkScanResult " + checkScanResult);
                    if(checkScanResult) {
                        checkScanResult = false;
                        checkBarCode(scanResult);
                    }
                    break;
                }
            }
        }
    };

    private void checkBarCode(String scanResult) {
        //Log.d("rlf_app", "scanResult " + scanResult);
        qrCodeValue = scanResult;
        ApplicationManager applicationManager = new ApplicationManager(getContext(), this, qrCodeValue);
        final AsyncTask<Void, Void, Void> execute = applicationManager.execute();
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
                if (mPreview!=null && mPreview.isSurfaceCreated()) { // check if surface created before using autofocus
                    try {
                        mCamera.autoFocus(autoFocusCB);
                        autoFocusStarted = true;
                    } catch (Exception e) {
                        Log.d("rlf_app", "Error starting autoFocus " + e.getMessage());
                    }
                }
                //Log.d("rlf_app", "autoFocusStarted " + autoFocusStarted);
                if(!autoFocusStarted)    scheduleAutoFocus(); // wait 2 sec and then do check again
            }
        }
    };

    private void startScan() {
        barcodeScanned = false;
        mCamera.setPreviewCallback(previewCb);
        mCamera.startPreview();
        previewing = true;

        //mCamera.autoFocus(autoFocusCB);
        boolean autoFocusStarted = false;
        if (mPreview!=null && mPreview.isSurfaceCreated()) { // check if surface created before using autofocus
            try {
                mCamera.autoFocus(autoFocusCB);
                autoFocusStarted = true;
            } catch (Exception e) {
                Log.d("rlf_app", "Error starting autoFocus " + e.getMessage());
            }
        }
        //Log.d("rlf_app", "autoFocusStarted " + autoFocusStarted);
        if(!autoFocusStarted)    scheduleAutoFocus(); // wait 2 sec and then do check again
    }

    public void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    public static boolean backCameraExists() {

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

//    private void updateLocalSettings(ActivationResultModel response) {
//
//        LocalSettings.setToken(getContext(), response.token);
//        LocalSettings.setTdesKey(getContext(), response.tdesKey);
//        LocalSettings.setTdesIV(getContext(), response.tdesIV);
//
//        LocalSettings.setOrderId(QRActivity.getContext(), qrCodeValue);
//
//        SUtils.clearSettings(QRActivity.getContext());
//        //SUtils.removeOrderEncodedFiles(QRActivity.getContext(), qrCodeValue);
//
//        SUtils.deleteAllTemporaryPictures(QRActivity.getContext());
//
//        ImageLoader.getInstance().clearMemoryCache();
//
//        DatabaseHelper.clearDocs();
//        DatabaseHelper.saveDocs(response.docs);
//
//    }

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
     * This interface must be implemented by activities that contain getContext()
     * fragment to allow an interaction in getContext() fragment to be communicated
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
