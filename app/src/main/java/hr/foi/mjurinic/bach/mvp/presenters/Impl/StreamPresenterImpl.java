package hr.foi.mjurinic.bach.mvp.presenters.Impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.views.StreamView;
import hr.foi.mjurinic.bach.utils.receivers.WifiDirectBroadcastReceiver;

import static android.os.Looper.getMainLooper;

public class StreamPresenterImpl implements StreamPresenter {

    private static final String TAG = "StreamPresenter";

    private StreamView streamView;
    private Context context;
    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel wifiChannel;
    private BroadcastReceiver wifiBroadcastReceiver;
    private IntentFilter intentFilter;

    public StreamPresenterImpl(StreamView streamView, Context context) {
        this.streamView = streamView;
        this.context = context;
    }

    @Override
    public void initWifiDirect() {
        if (wifiBroadcastReceiver != null) return;

        wifiManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        wifiChannel = wifiManager.initialize(context, getMainLooper(), null);
        wifiBroadcastReceiver = new WifiDirectBroadcastReceiver(wifiManager, wifiChannel, this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        context.registerReceiver(wifiBroadcastReceiver, intentFilter);

        Log.d(TAG, "Wi-Fi Peer-to-Peer initialized!");
    }

    @Override
    public void createWifiP2PGroup() {
        removeWifiP2PGroup();

        wifiManager.createGroup(wifiChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Wi-Fi Peer-to-Peer group created.");
            }

            @Override
            public void onFailure(int reason) {
                streamView.showError("P2P group creation failed. Retry. " + reason);
            }
        });
    }

    @Override
    public void removeWifiP2PGroup() {
        wifiManager.removeGroup(wifiChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Removed Wi-Fi Peer-to-Peer group.");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Failed to remove Wi-Fi Peer-to-Peer group: " + reason);
            }
        });
    }

    @Override
    public void generateQrCode(WifiHostInformation wifiHostInformation) {
        streamView.showQrCode(encodeAsBitmap(wifiHostInformation));
    }

    private Bitmap encodeAsBitmap(WifiHostInformation wifiHostInformation) {
        BitMatrix result;

        int width = (int) context.getResources().getDimension(R.dimen.qr_code_size);
        int height = (int) context.getResources().getDimension(R.dimen.qr_code_size);
        int white = ResourcesCompat.getColor(context.getResources(), android.R.color.white, null);
        int black = ResourcesCompat.getColor(context.getResources(), android.R.color.black, null);

        try {
            String payload = new Gson().toJson(wifiHostInformation);
            result = new MultiFormatWriter().encode(payload,  BarcodeFormat.QR_CODE, width, height, null);

            Log.d(TAG, payload);

            int w = result.getWidth();
            int h = result.getHeight();

            int[] pixels = new int[w * h];

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    pixels[y * w + x] = result.get(x, y) ? black : white;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, w, h);

            return bitmap;

        } catch (IllegalArgumentException | WriterException e) {
            e.printStackTrace();
        }

        return null;
    }
}
