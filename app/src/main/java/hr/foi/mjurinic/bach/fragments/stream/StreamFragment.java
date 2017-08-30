package hr.foi.mjurinic.bach.fragments.stream;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.activities.MainActivity;
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
    private RelativeLayout toolbarLayout;
    private RelativeLayout streamProgressLayout;
    private RelativeLayout endOfStreamLayout;
    private ImageView ivFlash;
    private ImageView ivCameraSwitch;
    private ImageView ivStop;
    private Button btnOk;
    private Button btnCancel;

    // Camera
    private Camera camera;
    private CameraPreviewSurfaceView surfaceView;
    private boolean isBackCameraActive;
    private boolean isFlashActive;
    private boolean isCameraPreviewVisible; // prevents SurfaceView creation during StreamConfig phase
    private int cameraFrontId = -1;
    private int cameraBackId = 0;

    private StreamPresenter streamPresenter;
    private StreamView view;
    private boolean isStreamActive;

    @Override
    protected int getViewStubLayoutResource() {
        return R.layout.fragment_streamer;
    }

    @Override
    protected void onCreateViewAfterViewStubInflated(View inflatedView, Bundle savedInstanceState) {
        Timber.d("StreamFragment inflated!");
        bindViews(inflatedView);
        identifyCameras();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (camera == null && super.hasInflated) {
            if (cameraBackId == -1 && cameraFrontId == -1) {
                identifyCameras();
            }

            initCamera(isBackCameraActive ? cameraBackId : cameraFrontId);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Timber.d("StreamFragment onPause!");

        releaseCamera();

        if (!isStreamActive && streamPresenter != null) {
            streamPresenter.closeSockets();
            ((ConnectionTypeFragment) ((StreamContainerFragment) getParentFragment()).getNthFragment(0)).disconnect();
        }
    }

    @Override
    public void showCameraPreview(ProtoStreamConfig streamConfig) {
        Camera.Parameters params = camera.getParameters();

        // camParams.setPreviewSize(streamConfig.getResolution().getWidth(), streamConfig.getResolution().getHeight());
        params.setPreviewSize(1280, 720);
        camera.setParameters(params);

        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initSurfaceView();

                btnCancel.setVisibility(View.GONE);
                toolbarPrimaryColor.setVisibility(View.GONE);
                streamProgressLayout.setVisibility(View.GONE);

                cameraPreview.setVisibility(View.VISIBLE);
                toolbarLayout.setVisibility(View.VISIBLE);

                isStreamActive = true;
                ((MainActivity) getBaseActivity()).hideTabLayout();
            }
        });
    }

    @Override
    public void handleFrame(byte[] frame) {
        // No re-sends on multimedia messages!
        streamPresenter.sendMessage(new ProtoMultimedia(frame, !isBackCameraActive), new DatagramSentListener(streamPresenter, 5) {
            @Override
            public void onSuccess() {}
        });
    }

    @Override
    public ProtoStreamInfo provideCameraInfo() {
        ProtoStreamInfo.CameraInfo frontCameraInfo = null;
        ProtoStreamInfo.CameraInfo backCameraInfo;

        // Gather back camera info first.
        initCamera(cameraBackId);
        isBackCameraActive = true;

        backCameraInfo = gatherCameraInfo(camera);

        // If front camera exists, gather its info and leave it initialized.
        if (cameraFrontId != -1) {
            releaseCamera();
            isBackCameraActive = false;

            initCamera(cameraFrontId);
            frontCameraInfo = gatherCameraInfo(camera);
        }

        return new ProtoStreamInfo(frontCameraInfo, backCameraInfo);
    }

    @Override
    public void clearComponents() {
        ((ConnectionTypeFragment) ((StreamContainerFragment) getParentFragment()).getNthFragment(0)).disconnect();

        releaseCamera();

        ((StreamContainerFragment) getParentFragment()).changeActiveFragment(0);
        ((MainActivity) getBaseActivity()).jumpToHomeFragment();

        // Resetting views from main thread.
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ConnectionTypeFragment) ((StreamContainerFragment) getParentFragment()).getNthFragment(0)).resetFragment();
                ((MainActivity) getBaseActivity()).showTabLayout();
                resetFragment();
            }
        });
    }

    @Override
    public void displayEndOfStreamView() {
        releaseCamera();
        ((ConnectionTypeFragment) ((StreamContainerFragment) getParentFragment()).getNthFragment(0)).disconnect();

        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toolbarPrimaryColor.setTitle("Stream Ended");
                toolbarLayout.setVisibility(View.GONE);
                cameraPreview.setVisibility(View.GONE);
                toolbarPrimaryColor.setVisibility(View.VISIBLE);
                endOfStreamLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void updateProgressText(String progress) {
        tvProgress.setText(progress);
    }

    public void init() {
        view = this;
        streamPresenter = new StreamPresenterImpl(this, ((StreamContainerFragment) getParentFragment()).getSocketInteractor());
    }

    private void resetFragment() {
        Timber.d("Resetting StreamFragment...");

        isStreamActive = false;
        toolbarPrimaryColor.setTitle("Waiting for Client Connection...");

        // Show 1. step
        btnCancel.setVisibility(View.VISIBLE);
        toolbarPrimaryColor.setVisibility(View.VISIBLE);
        streamProgressLayout.setVisibility(View.VISIBLE);

        // Hide 2. step
        cameraPreview.setVisibility(View.GONE);
        toolbarLayout.setVisibility(View.GONE);

        // Hide 3. step
        endOfStreamLayout.setVisibility(View.GONE);
    }

    private ProtoStreamInfo.CameraInfo gatherCameraInfo(Camera camera) {
        List<CameraSize> resolutions = new ArrayList<>();

        for (Camera.Size size : camera.getParameters().getSupportedPictureSizes()) {
            resolutions.add(new CameraSize(size.width, size.height));
        }

        return new ProtoStreamInfo.CameraInfo(resolutions, camera.getParameters().getSupportedPreviewFpsRange());
    }

    private void initSurfaceView() {
        Timber.d("Init SurfaceView...");

        surfaceView = new CameraPreviewSurfaceView(getBaseActivity().getApplicationContext(), camera, view);
        cameraPreview.addView(surfaceView);
        isCameraPreviewVisible = true;
    }

    private boolean initCamera(int cameraId) {
        Timber.d("Init camera...");

        try {
            camera = Camera.open(cameraId);
            camera.setDisplayOrientation(90);

            isBackCameraActive = (cameraId == cameraBackId);

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
        // Threading issues during "StreamInfoState".
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cameraPreview != null) {
                    cameraPreview.removeAllViews();
                }
            }
        });

        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    private void bindViews(View view) {
        toolbarPrimaryColor = (Toolbar) view.findViewById(R.id.toolbar_primary_color);
        toolbarPrimaryColor.setTitle("Waiting for Client Connection...");

        toolbarLayout = (RelativeLayout) view.findViewById(R.id.toolbar_layout);
        tvProgress = (TextView) view.findViewById(R.id.tv_progress);
        cameraPreview = (FrameLayout) view.findViewById(R.id.stream_camera_preview);
        streamProgressLayout = (RelativeLayout) view.findViewById(R.id.stream_progress_layout);
        endOfStreamLayout = (RelativeLayout) view.findViewById(R.id.end_stream_layout);

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

        ivStop = (ImageView) view.findViewById(R.id.iv_stop);
        ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStreamActive = false;
                streamPresenter.closeStream();
            }
        });

        btnOk = (Button) view.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFragment();
                ((ConnectionTypeFragment) ((StreamContainerFragment) getParentFragment()).getNthFragment(0)).resetFragment();
                ((StreamContainerFragment) getParentFragment()).changeActiveFragment(0);
                ((MainActivity) getBaseActivity()).showTabLayout();
                ((MainActivity) getBaseActivity()).jumpToHomeFragment();
            }
        });

        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseCamera();
                ((ConnectionTypeFragment) ((StreamContainerFragment) getParentFragment()).getNthFragment(0)).disconnect();
                ((ConnectionTypeFragment) ((StreamContainerFragment) getParentFragment()).getNthFragment(0)).resetFragment();
                ((StreamContainerFragment) getParentFragment()).changeActiveFragment(0);
                ((MainActivity) getBaseActivity()).jumpToHomeFragment();
            }
        });
    }
}
