package hr.foi.mjurinic.bach.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
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

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.manager = manager;
        this.channel = channel;

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
                            final WifiHostInformation wifiHostInformation = new WifiHostInformation();

                            manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                                @Override
                                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                                    wifiHostInformation.setDeviceIpAddress(info.groupOwnerAddress.getHostAddress());
                                }
                            });

                            wifiHostInformation.setNetworkName(group.getNetworkName());
                            wifiHostInformation.setPassphrase(group.getPassphrase());
                            wifiHostInformation.setDeviceMacAddress(group.getOwner().deviceAddress);

                            streamPresenter.initMediaTransport(wifiHostInformation);
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

            default:
                break;
        }
    }

    /**
     * Manually update StreamPresenter because BaseStreamView instance can change.
     *
     * @param streamPresenter
     */
    public void setStreamPresenter(StreamPresenter streamPresenter) {
        this.streamPresenter = streamPresenter;
    }
}
