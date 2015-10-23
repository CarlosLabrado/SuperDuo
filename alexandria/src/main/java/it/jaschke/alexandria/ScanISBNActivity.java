package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import it.jaschke.alexandria.CameraPreview.CameraPreview;


/**
 * Handles the QR scanning
 */
public class ScanISBNActivity extends Activity {


    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    public static final String ISBN_EXTRA = "isbn_extra";


    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;


//    public static Bus bus;


    static {
        System.loadLibrary("iconv");
    }

    public ScanISBNActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_isbn);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
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

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

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
//                    scanText.setText("barcode result " + sym.getData());
                    barcodeScanned = true;
                    startValidationProcess(sym.getData());
                }
            }
        }
    };

    /**
     * Temporal validation process for when the user finishes scanning a QR code
     */
    private void startValidationProcess(String isbnCode) {
        Toast.makeText(this, isbnCode, Toast.LENGTH_SHORT).show();
        Intent intent = getIntent();
        intent.putExtra(ISBN_EXTRA, isbnCode);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.app_toolbar_title_scan_qr), null, false);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.help, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_help_list:
//                showDialog();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    private void showDialog() {
//        AlertDialog.Builder db = new AlertDialog.Builder(getActivity());
//        db.setTitle(R.string.dialog_help_title);
//        db.setMessage(
//                R.string.dialog_help_message_qr);
//        db.setNegativeButton(R.string.dialog_help_dismiss, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface d, int arg1) {
//                d.cancel();
//            }
//        });
//        db.show();
//    }


}