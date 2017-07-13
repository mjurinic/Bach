package hr.foi.mjurinic.bach.mvp.views;

import android.hardware.Camera;

public interface StreamView extends BaseStreamView {

    boolean initCamera(int cameraId);

    void showCameraPreview();

    Camera getCamera();
}
