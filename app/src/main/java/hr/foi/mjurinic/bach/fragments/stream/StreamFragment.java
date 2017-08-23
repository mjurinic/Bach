package hr.foi.mjurinic.bach.fragments.stream;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.listeners.DatagramSentListener;
import hr.foi.mjurinic.bach.models.CameraSize;
import hr.foi.mjurinic.bach.mvp.presenters.stream.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.presenters.stream.impl.StreamPresenterImpl;
import hr.foi.mjurinic.bach.mvp.views.StreamView;
import hr.foi.mjurinic.bach.network.protocol.ProtoMultimedia;
import hr.foi.mjurinic.bach.network.protocol.ProtoStreamConfig;
import hr.foi.mjurinic.bach.network.protocol.ProtoStreamInfo;
import hr.foi.mjurinic.bach.utils.views.CameraPreviewSurfaceView;
import timber.log.Timber;

public class StreamFragment extends BaseFragment implements StreamView {

    // Views
    private Toolbar toolbarPrimaryColor;
    private TextView tvProgress;
    private FrameLayout cameraPreview;
    private LinearLayout toolbarLayout;
    private RelativeLayout streamProgressLayout;
    private ImageView ivFlash;
    private ImageView ivCameraSwitch;

    // Camera
    private Camera camera;
    private CameraPreviewSurfaceView surfaceView;
    private boolean isBackCameraActive;
    private boolean isFlashActive;
    private boolean isCameraPreviewVisible;
    private int cameraFrontId = -1;
    private int cameraBackId = 0;

    private StreamPresenter streamPresenter;
    private StreamView view;

    @Override
    protected int getViewStubLayoutResource() {
        return R.layout.fragment_streamer;
    }

    @Override
    protected void onCreateViewAfterViewStubInflated(View inflatedView, Bundle savedInstanceState) {
        Timber.d("StreamFragment inflated!");

        streamPresenter = new StreamPresenterImpl(this, ((StreamContainerFragment) getParentFragment()).getSocketInteractor());
        view = this;

        setHasOptionsMenu(true);
        bindViews(inflatedView);

        identifyCameras();
        initCamera(cameraFrontId != -1 ? cameraFrontId : cameraBackId);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (camera == null) {
            if (cameraBackId == -1 && cameraFrontId == -1) {
                identifyCameras();
            }

            initCamera(isBackCameraActive ? cameraBackId : cameraFrontId);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
        isCameraPreviewVisible = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCamera();
        isCameraPreviewVisible = false;
    }

    @Override
    public void showCameraPreview(ProtoStreamConfig streamConfig) {
        Camera.Parameters params = camera.getParameters();

        // camParams.setPreviewSize(streamConfig.getResolution().getWidth(), streamConfig.getResolution().getHeight());
        //camParams.setPreviewSize(640, 480);
        params.setPreviewSize(1280, 720);

        camera.setParameters(params);

        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initSurfaceView();

                toolbarPrimaryColor.setVisibility(View.GONE);
                streamProgressLayout.setVisibility(View.GONE);

                cameraPreview.setVisibility(View.VISIBLE);
                toolbarLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void handleFrame(byte[] frame) {
        // No re-sends on multimedia messages!
        streamPresenter.sendMessage(new ProtoMultimedia(frame, !isBackCameraActive), new DatagramSentListener(streamPresenter, 5) {
            @Override
            public void onSuccess() {
                // Timber.i("Frame sent!");
            }
        });
    }

    @Override
    public ProtoStreamInfo provideCameraInfo() {
        List<CameraSize> resolutions = new ArrayList<>();

        for (Camera.Size size : camera.getParameters().getSupportedPictureSizes()) {
            resolutions.add(new CameraSize(size.width, size.height));
        }

        return new ProtoStreamInfo(resolutions, camera.getParameters().getSupportedPreviewFpsRange());
    }

    @Override
    public void updateProgressText(String progress) {
        tvProgress.setText(progress);
    }

    private void initSurfaceView() {
        surfaceView = new CameraPreviewSurfaceView(getBaseActivity().getApplicationContext(), camera, view);
        cameraPreview.addView(surfaceView);
        isCameraPreviewVisible = true;
    }

    private boolean initCamera(int cameraId) {
        try {
            camera = Camera.open(cameraId);
            camera.setDisplayOrientation(90);

            isBackCameraActive = (cameraId == cameraBackId);

            Timber.d("isCameraPreviewVisible: " + isCameraPreviewVisible);

            if (isCameraPreviewVisible) {
                Camera.Parameters params = camera.getParameters();
                params.setPreviewSize(1280, 720);

                camera.setParameters(params);

                initSurfaceView();
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean hasFlash() {
        List<String> flashModes = camera.getParameters().getSupportedFlashModes();

        if (flashModes != null) {
            for (String flashMode : flashModes) {
                if (Camera.Parameters.FLASH_MODE_ON.equals(flashMode)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void identifyCameras() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
            Camera.getCameraInfo(i, cameraInfo);

            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraFrontId = i;

            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraBackId = i;
            }
        }

        Timber.d("Front cameraId: " + cameraFrontId);
        Timber.d("Back cameraId: " + cameraBackId);
    }

    private void flashOff() {
        Camera.Parameters params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        params.setPreviewSize(1280, 720);

        camera.setParameters(params);

        toggleFlashIcons();

        isFlashActive = false;
    }

    private void onToggleFlashClick() {
        if (hasFlash()) {
            Camera.Parameters params = camera.getParameters();
            params.setFlashMode(isFlashActive ? Camera.Parameters.FLASH_MODE_OFF : Camera.Parameters.FLASH_MODE_TORCH);
            params.setPreviewSize(1280, 720);

            camera.setParameters(params);

            toggleFlashIcons();

            isFlashActive = !isFlashActive;
        }
    }

    private void toggleFlashIcons() {
        ivFlash.setBackground(isFlashActive ?
                getResources().getDrawable(R.drawable.ic_flash_on_white_48dp) :
                getResources().getDrawable(R.drawable.ic_flash_off_white_48dp));
    }

    private void onSwitchCamerasClick() {
        if (cameraFrontId == -1) return;

        if (isFlashActive) {
            flashOff();
        }

        releaseCamera();

        initCamera(isBackCameraActive ? cameraFrontId : cameraBackId);
    }

    private void releaseCamera() {
        if (cameraPreview != null) {
            cameraPreview.removeAllViews();
        }

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private void bindViews(View view) {
        toolbarPrimaryColor = (Toolbar) view.findViewById(R.id.toolbar_primary_color);
        toolbarPrimaryColor.setTitle("Waiting for Client Connection...");

        toolbarLayout = (LinearLayout) view.findViewById(R.id.toolbar_layout);
        tvProgress = (TextView) view.findViewById(R.id.tv_progress);
        cameraPreview = (FrameLayout) view.findViewById(R.id.stream_camera_preview);
        streamProgressLayout = (RelativeLayout) view.findViewById(R.id.stream_progress_layout);

        ivFlash = (ImageView) view.findViewById(R.id.iv_flash);
        ivFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleFlashClick();
            }
        });

        ivCameraSwitch = (ImageView) view.findViewById(R.id.iv_camera_switch);
        ivCameraSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitchCamerasClick();
            }
        });
    }
}
