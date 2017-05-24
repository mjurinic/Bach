package hr.foi.mjurinic.bach.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.dagger.components.DaggerWatchComponent;
import hr.foi.mjurinic.bach.dagger.modules.WatchModule;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.WatchPresenterImpl;
import hr.foi.mjurinic.bach.mvp.views.WatchView;
import hr.foi.mjurinic.bach.utils.receivers.WifiDirectBroadcastReceiver;
import hr.foi.mjurinic.bach.utils.views.CameraPreviewSurfaceView;

public class MainActivity extends BaseActivity implements WatchView {

    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private static final int PERMISSION_REQUEST_ACCESS_WIFI_STATE = 1;

    private static final int PERMISSION_REQUEST_ACCESS_NETWORK_STATE = 2;

    @BindView(R.id.fl_camera_preview)
    FrameLayout cameraPreview;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    WatchPresenterImpl watchPresenterImpl;

    private int cameraFrontId = -1;
    private int cameraBackId = -1;
    private boolean hasCameraPermission;
    private boolean isBackCameraActive;
    private boolean isFlashActive;
    private Camera camera;
    private CameraPreviewSurfaceView cameraPreviewSurfaceView;
    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel wifiChannel;
    private BroadcastReceiver wifiBroadcastReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        DaggerWatchComponent.builder().
                watchModule(new WatchModule(this)).
                build().
                inject(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (hasPermissions()) {
            identifyCameras();
            initCamera(cameraFrontId != -1 ? cameraFrontId : cameraBackId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        releaseCamera();

        if (wifiBroadcastReceiver != null) {
            unregisterReceiver(wifiBroadcastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasCameraPermission && camera == null) {
            if (cameraBackId == -1 && cameraFrontId == -1) {
                identifyCameras();
            }

            initCamera(isBackCameraActive ? cameraBackId : cameraFrontId);
        }

        if (wifiBroadcastReceiver != null) {
            registerReceiver(wifiBroadcastReceiver, intentFilter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_switch_cameras:
                onSwitchCamerasClick();
                return true;

            case R.id.action_flash_on:
            case R.id.action_flash_off:
                onToggleFlashClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                hasCameraPermission = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            }
        }
    }

    @OnClick(R.id.iv_broadcast)
    void onBroadcastClick() {
        initBroadcastReceiver();

    }

    @OnClick(R.id.iv_watch)
    void onWatchClick() {
        initBroadcastReceiver();
        watchPresenterImpl.discoverWifiPeers(wifiManager, wifiChannel);
    }

    private void initBroadcastReceiver() {
        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wifiChannel = wifiManager.initialize(this, getMainLooper(), null);
        wifiBroadcastReceiver = new WifiDirectBroadcastReceiver(wifiManager, wifiChannel, this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        registerReceiver(wifiBroadcastReceiver, intentFilter);
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
    }

    private void initCamera(int cameraId) {
        try {
            camera = Camera.open(cameraId);
            camera.setDisplayOrientation(90);

            isBackCameraActive = (cameraId == cameraBackId);

        } catch (Exception e) {
            // TODO Add user friendly dialog msg(?)
            e.printStackTrace();
        }

        cameraPreviewSurfaceView = new CameraPreviewSurfaceView(this, camera);
        cameraPreview.addView(cameraPreviewSurfaceView);
    }

    private boolean hasPermissions() {
        hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        if (!hasCameraPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }

        return hasCameraPermission;
    }

    private void flashOff() {

        Camera.Parameters params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

        toolbar.getMenu().findItem(R.id.action_flash_on).setVisible(false);
        toolbar.getMenu().findItem(R.id.action_flash_off).setVisible(true);

        camera.setParameters(params);

        isFlashActive = false;
    }

    private void onToggleFlashClick() {
        if (hasFlash()) {
            Camera.Parameters params = camera.getParameters();
            params.setFlashMode(isFlashActive ? Camera.Parameters.FLASH_MODE_OFF : Camera.Parameters.FLASH_MODE_TORCH);

            camera.setParameters(params);

            isFlashActive = !isFlashActive;

            toolbar.getMenu().findItem(R.id.action_flash_on).setVisible(isFlashActive);
            toolbar.getMenu().findItem(R.id.action_flash_off).setVisible(!isFlashActive);
        }
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
        if (camera != null) {
            cameraPreview.removeAllViews();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void listPeers() {
        // TODO Dialog with peer list?
    }
}
