package hr.foi.mjurinic.bach.mvp.presenters.Impl;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;

import javax.inject.Inject;

import hr.foi.mjurinic.bach.mvp.presenters.WatchPresenter;
import hr.foi.mjurinic.bach.mvp.views.WatchView;

public class WatchPresenterImpl implements WatchPresenter, WifiP2pManager.PeerListListener {

    private WatchView watchView;
    private WatchPresenterImpl watchPresenter;

    @Inject
    public WatchPresenterImpl(WatchView watchView) {
        this.watchView = watchView;
        this.watchPresenter = this;
    }

    @Override
    public void discoverWifiPeers(final WifiP2pManager manager, final WifiP2pManager.Channel channel) {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // manager.requestPeers(channel,watchPresenter);
            }

            @Override
            public void onFailure(int reason) {
                watchView.showError("[" + reason + "] Unable to list WiFi peers.");
            }
        });
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        System.out.println("oh yea");

        for (WifiP2pDevice device : peers.getDeviceList()) {
            System.out.println(device.toString());
        }
    }
}
