package hr.foi.mjurinic.bach.fragments.watch;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;

import java.io.IOException;

import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.activities.MainActivity;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.presenters.watch.QrScannerPresenter;
import hr.foi.mjurinic.bach.mvp.presenters.watch.impl.QrScannerPresenterImpl;
import hr.foi.mjurinic.bach.mvp.views.QrScannerView;
import timber.log.Timber;

public class QrScannerFragment extends BaseFragment implements QrScannerView {

    // Views
    private QrScannerPresenter qrScannerPresenter;
    private FrameLayout cameraPreview;
    private RelativeLayout progressLayout;
    private TextView tvProgress;
    private Button btnCancel;

    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private WifiManager wifiManager;
    private int netId;
    private boolean isInflated;

    @Override
    protected int getViewStubLayoutResource() {
        return R.layout.fragment_qr_scanner;
    }

    @Override
    protected void onCreateViewAfterViewStubInflated(View inflatedView, Bundle savedInstanceState) {
        bindViews(inflatedView);

        isInflated = true;
        qrScannerPresenter = new QrScannerPresenterImpl(((WatchContainerFragment) getParentFragment()).getSocketInteractor(), this);
    }

    @Override
    public void updateProgressText(final String progress) {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvProgress.setText(progress);
            }
        });
    }

    @Override
    public void nextFragment() {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((WatchContainerFragment) getParentFragment()).changeActiveFragment(1);
            }
        });
    }

    @Override
    public void resetFragment() {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnCancel.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                cameraPreview.setVisibility(View.VISIBLE);
            }
        });

        qrScannerPresenter.closeSockets();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (cameraSource == null) {
            initQrScanner();
            initCameraPreview();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isInflated = false;
    }

    public void init() {
        if (isInflated && cameraSource == null) {
            initQrScanner();
            initCameraPreview();
        }
    }

    public void disconnect() {
        if (wifiManager != null) {
            wifiManager.disconnect();
        }
    }

    private void showProgressLayout() {
        btnCancel.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.VISIBLE);
        cameraPreview.setVisibility(View.GONE);

        releaseCamera();
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseActivity().getApplicationContext().
                getSystemService(getBaseActivity().getApplicationContext().CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
        }

        return false;
    }

    private void initWifiP2P(final WifiHostInformation hostInformation) {
        Timber.d("Initializing Wi-Fi P2P...");

        wifiManager = (WifiManager) getBaseActivity().getApplicationContext().
                getSystemService(getBaseActivity().getApplicationContext().WIFI_SERVICE);

        wifiManager.disconnect(); // Disconnect from current network

        final WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = String.format("\"%s\"", hostInformation.getNetworkName());
        wifiConfiguration.preSharedKey = String.format("\"%s\"", hostInformation.getPassphrase());

        updateProgressText("Connecting to " + hostInformation.getNetworkName() + "...");

        netId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.enableNetwork(netId, false);
        wifiManager.reconnect();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isConnected()) ;

                qrScannerPresenter.initSocketLayer(hostInformation);

                Timber.d("Successfully connected to: " + wifiConfiguration.SSID);
                Timber.d("Device IP: " + Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
                Timber.d("Link speed: " + wifiManager.getConnectionInfo().getLinkSpeed());
            }
        }).start();
    }

    private void initQrScanner() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getBaseActivity().getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(getBaseActivity().getApplicationContext(), barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedFps(15)
                .setRequestedPreviewSize(600, 800)
                .build();

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {
                    Timber.d(barcodes.valueAt(0).displayValue);

                    getBaseActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgressLayout();
                            initWifiP2P(new Gson().fromJson(barcodes.valueAt(0).displayValue, WifiHostInformation.class));
                        }
                    });
                }
            }
        });
    }

    private void initCameraPreview() {
        surfaceView = new SurfaceView(getBaseActivity().getApplicationContext());

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    //noinspection MissingPermission
                    cameraSource.start(surfaceView.getHolder());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        cameraPreview.addView(surfaceView);
    }

    private void releaseCamera() {
        if (cameraSource != null) {
            cameraPreview.removeAllViews();
            cameraSource.stop();
            cameraSource = null;
        }
    }

    private void bindViews(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_primary_color);
        toolbar.setTitle("Scan QR Code");

        cameraPreview = (FrameLayout) view.findViewById(R.id.camera_preview);
        progressLayout = (RelativeLayout) view.findViewById(R.id.progress_layout);
        tvProgress = (TextView) view.findViewById(R.id.tv_progress);

        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
                resetFragment();
                ((MainActivity) getBaseActivity()).jumpToHomeFragment();
            }
        });
    }
}
