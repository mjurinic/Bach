package hr.foi.mjurinic.bach.fragments.stream;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.models.CameraSize;
import hr.foi.mjurinic.bach.mvp.presenters.stream.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.presenters.stream.impl.StreamPresenterImpl;
import hr.foi.mjurinic.bach.mvp.views.StreamView;
import hr.foi.mjurinic.bach.network.protocol.ProtoStreamInfo;
import hr.foi.mjurinic.bach.utils.views.CameraPreviewSurfaceView;
import timber.log.Timber;

public class StreamFragment extends BaseFragment implements StreamView {

    // Views
    private Toolbar toolbar;
    private TextView tvProgress;
    private FrameLayout cameraPreview;
    private RelativeLayout streamProgressLayout;

    // Camera
    private Camera camera;
    private CameraPreviewSurfaceView surfaceView;
    private boolean isBackCameraActive;
    private int cameraFrontId = -1;
    private int cameraBackId = 0;

    private StreamPresenter streamPresenter;

    @Override
    protected int getViewStubLayoutResource() {
        return R.layout.fragment_streamer;
    }

    @Override
    protected void onCreateViewAfterViewStubInflated(View inflatedView, Bundle savedInstanceState) {
        Timber.d("StreamFragment inflated!");

        streamPresenter = new StreamPresenterImpl(this, ((StreamContainerFragment) getParentFragment()).getSocketInteractor());

        bindViews(inflatedView);
        initCamera(0);
    }

    @Override
    public void showCameraPreview() {
        surfaceView = new CameraPreviewSurfaceView(getBaseActivity().getApplicationContext(), camera);
        cameraPreview.addView(surfaceView);

        toolbar.setVisibility(View.GONE);
        streamProgressLayout.setVisibility(View.GONE);
        cameraPreview.setVisibility(View.VISIBLE);
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

    private boolean initCamera(int cameraId) {
        releaseCamera();

        try {
            camera = Camera.open(cameraId);
            camera.setDisplayOrientation(90);

            isBackCameraActive = (cameraId == cameraBackId);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void releaseCamera() {
        cameraPreview.removeAllViews();

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private void bindViews(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_primary_color);
        toolbar.setTitle("Waiting for Client connection...");

        tvProgress = (TextView) view.findViewById(R.id.tv_progress);
        cameraPreview = (FrameLayout) view.findViewById(R.id.stream_camera_preview);
        streamProgressLayout = (RelativeLayout) view.findViewById(R.id.stream_progress_layout);
    }
}
