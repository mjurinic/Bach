package hr.foi.mjurinic.bach.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Base64;

import hr.foi.mjurinic.bach.helpers.Crypto;
import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.presenters.stream.ConnectionTypePresenter;
import hr.foi.mjurinic.bach.mvp.presenters.stream.impl.ConnectionTypePresenterImpl;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel wifiChannel;
    private ConnectionTypePresenter presenter;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, ConnectionTypePresenter presenter) {
        this.wifiManager = manager;
        this.wifiChannel = channel;
        this.presenter = presenter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION: {
                wifiManager.requestGroupInfo(wifiChannel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup group) {
                        if (group != null && !((ConnectionTypePresenterImpl) presenter).isAccessPointCreated()) {
                            ((ConnectionTypePresenterImpl) presenter).setAccessPointCreated(true);

                            final WifiHostInformation wifiHostInformation = new WifiHostInformation();

                            wifiManager.requestConnectionInfo(wifiChannel, new WifiP2pManager.ConnectionInfoListener() {
                                @Override
                                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                                    wifiHostInformation.setDeviceIpAddress(info.groupOwnerAddress.getHostAddress());
                                }
                            });

                            wifiHostInformation.setNetworkName(group.getNetworkName());
                            wifiHostInformation.setPassphrase(group.getPassphrase());
                            wifiHostInformation.setDeviceMacAddress(group.getOwner().deviceAddress);
                            wifiHostInformation.setB64AESkey(Base64.encodeToString(Crypto.generateKey(), Base64.NO_WRAP));

                            presenter.initSocketLayer(wifiHostInformation);
                        }
                    }
                });

                break;
            }

            default:
                break;
        }
    }
}
