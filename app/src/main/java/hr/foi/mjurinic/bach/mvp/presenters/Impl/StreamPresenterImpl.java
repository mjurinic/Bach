package hr.foi.mjurinic.bach.mvp.presenters.Impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import javax.inject.Inject;

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

    @Inject
    public StreamPresenterImpl(StreamView streamView, Context context) {
        this.streamView = streamView;
        this.context = context;
    }

    public void initWifiDirect() {
        wifiManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        wifiChannel = wifiManager.initialize(context, getMainLooper(), null);
        wifiBroadcastReceiver = new WifiDirectBroadcastReceiver(wifiManager, wifiChannel);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        context.registerReceiver(wifiBroadcastReceiver, intentFilter);

        Log.d(TAG, "Wi-Fi Peer-to-Peer initialized!");
    }

    @Override
    public void createWifiP2PGroup() {
        wifiManager.createGroup(wifiChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Wi-Fi Peer-to-Peer group successfully created.");
            }

            @Override
            public void onFailure(int reason) {
                streamView.showError("P2P group creation failed. Retry.");
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
    public void generateQrCode() {

    }
}
