package hr.foi.mjurinic.bach.utils.views;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import hr.foi.mjurinic.bach.mvp.views.StreamView;

public class CameraPreviewSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private StreamView streamView;

    public CameraPreviewSurfaceView(Context context, Camera camera, StreamView streamView) {
        super(context);
        this.camera = camera;
        this.streamView = streamView;

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {
                    Camera.Size previewSize = camera.getParameters().getPreviewSize();

                    YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21, previewSize.width, previewSize.height, null);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    yuvImage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 50, out);

                    streamView.handleFrame(out.toByteArray());
                }
            });

            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // When view changes.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Release camera in activity.
    }
}
