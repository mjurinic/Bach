package hr.foi.mjurinic.bach.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import hr.foi.mjurinic.bach.activities.MainActivity;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.WatchPresenterImpl;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "WifiBroadcastReceiver";

    @Inject
    WatchPresenterImpl watchPresenterImpl;

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity mainActivity;
    private List<WifiP2pDevice> peers;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity mainActivity) {
        super();

        this.manager = manager;
        this.channel = channel;
        this.mainActivity = mainActivity;

        peers = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION: {
                String message;

                if (WifiP2pManager.WIFI_P2P_STATE_ENABLED == intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)) {
                    message = "Wi-Fi P2P enabled!";

                } else {
                    message = "Wi-Fi P2P NOT enabled.";
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                break;
            }

            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION: {
                System.out.println("WIFI_P2P_PEERS_CHANGED_ACTION");

                manager.requestPeers(channel, peerListListener);

                break;
            }

            default:
                break;
        }
    }

    /**
     * TODO How to move this to presenter? [PROBLEM] It's null when called.
     */
    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            List<WifiP2pDevice> refreshedPeers = new ArrayList<>(peerList.getDeviceList());

            if (!refreshedPeers.equals(peerList)) {
                peers.clear();
                peers.addAll(refreshedPeers);

                for (WifiP2pDevice peer : peers) {
                    System.out.println(peer.toString());
                }
            }

            if (peers.size() == 0) {
                Log.d(TAG, "No devices found");
            }
        }
    };
}
