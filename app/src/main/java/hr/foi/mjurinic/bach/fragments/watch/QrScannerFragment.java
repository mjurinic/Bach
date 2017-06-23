package hr.foi.mjurinic.bach.fragments.watch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.presenters.WatchPresenter;
import timber.log.Timber;

public class QrScannerFragment extends BaseFragment {

    @BindView(R.id.toolbar_primary_color)
    Toolbar toolbar;

    @BindView(R.id.camera_preview)
    FrameLayout cameraPreview;

    @Inject
    WatchPresenter watchPresenter;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView surfaceView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_scanner, container, false);

        ButterKnife.bind(this, view);
        ((WatchContainerFragment) getParentFragment()).getWatchComponent().inject(this);

        toolbar.setTitle("Scan the QR Code");

        barcodeDetector = new BarcodeDetector.Builder(getBaseActivity().getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(getBaseActivity().getApplicationContext(), barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {
                    Timber.d(barcodes.valueAt(0).displayValue);

                    // Idx: 1 -> ConnectionFragment
                    ((WatchContainerFragment) getParentFragment()).changeActiveFragment(1);

                    watchPresenter.connectToWifiHost(
                            new Gson().fromJson(barcodes.valueAt(0).displayValue, WifiHostInformation.class));

                    // TODO properly stop preview and camera
                    cameraSource.stop();
                }
            }
        });

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

        return view;
    }
}
