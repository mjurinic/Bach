package hr.foi.mjurinic.bach.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "WifiBroadcastReceiver";

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private List<WifiP2pDevice> peers;
    private StreamPresenter streamPresenter;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, StreamPresenter streamPresenter) {
        super();

        this.manager = manager;
        this.channel = channel;
        this.streamPresenter = streamPresenter;

        peers = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION: {
                manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup group) {
                        if (group != null) {
                            streamPresenter.generateQrCode(
                                    new WifiHostInformation(group.getNetworkName(), group.getPassphrase(), group.getOwner().deviceAddress));
                        }
                    }
                });

                break;
            }

            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION: {
                String message;

                if (WifiP2pManager.WIFI_P2P_STATE_ENABLED == intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)) {
                    message = "Wi-Fi P2P enabled!";

                } else {
                    message = "Wi-Fi P2P NOT enabled.";
                }

                Log.d(TAG, message);

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
