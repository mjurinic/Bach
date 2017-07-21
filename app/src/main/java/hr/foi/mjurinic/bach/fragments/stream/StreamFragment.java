package hr.foi.mjurinic.bach.fragments.stream;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.views.StreamView;
import hr.foi.mjurinic.bach.utils.views.CameraPreviewSurfaceView;

public class StreamFragment extends BaseFragment implements StreamView {

    @BindView(R.id.toolbar_primary_color)
    Toolbar toolbar;

    @BindView(R.id.tv_watch_progress_text)
    TextView tvProgressText;

    @BindView(R.id.streamer_camera_preview)
    FrameLayout cameraPreview;

    @BindView(R.id.streamer_progress_layout)
    RelativeLayout streamProgressLayout;

    @Inject
    StreamPresenter streamPresenter;

    private Camera camera;
    private CameraPreviewSurfaceView surfaceView;
    private boolean isBackCameraActive;
    private int cameraFrontId = -1;
    private int cameraBackId = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_streamer, container, false);

        ButterKnife.bind(this, view);
        ((StreamContainerFragment) getParentFragment()).getStreamComponent().inject(this);

        toolbar.setTitle("Initializing stream...");
        streamPresenter.updateView(this);

        // BachApp.getInstance().getWifiDirectBroadcastReceiver().setStreamPresenter(streamPresenter);

        return view;
    }


    @Override
    public boolean initCamera(int cameraId) {
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

    @Override
    public void showCameraPreview() {
        surfaceView = new CameraPreviewSurfaceView(getBaseActivity().getApplicationContext(), camera);
        cameraPreview.addView(surfaceView);

        toolbar.setVisibility(View.GONE);
        streamProgressLayout.setVisibility(View.GONE);
        cameraPreview.setVisibility(View.VISIBLE);
    }

    @Override
    public void showQrCode(Bitmap qrCode) {

    }

    @Override
    public void updateProgressText(String message) {
        tvProgressText.setText(message);
    }

    @Override
    public void nextFragment() {

    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    public StreamContainerFragment getContainerFragment() {
        return (StreamContainerFragment) getParentFragment();
    }
}
