package hr.foi.mjurinic.bach.fragments.stream;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.fragments.BaseFragment;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.views.StreamView;
import hr.foi.mjurinic.bach.utils.views.CameraPreviewSurfaceView;
import timber.log.Timber;

public class StreamFragment extends BaseFragment implements StreamView {

    //@BindView(R.id.toolbar_primary_color)
    Toolbar toolbar;

    //@BindView(R.id.tv_watch_progress_text)
    TextView tvProgressText;

    //@BindView(R.id.streamer_camera_preview)
    FrameLayout cameraPreview;

    //@BindView(R.id.streamer_progress_layout)
    RelativeLayout streamProgressLayout;

    private StreamPresenter streamPresenter;
    private Camera camera;
    private CameraPreviewSurfaceView surfaceView;
    private boolean isBackCameraActive;
    private int cameraFrontId = -1;
    private int cameraBackId = -1;

    @Override
    protected int getViewStubLayoutResource() {
        return R.layout.fragment_streamer;
    }

    @Override
    protected void onCreateViewAfterViewStubInflated(View inflatedView, Bundle savedInstanceState) {
        // TODO init logic here
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

    /**
     * Tells the presenter that this current view is active.
     */
    public void updateView() {
        Timber.d("Update view!");
        streamPresenter.updateView(this);
    }
}
