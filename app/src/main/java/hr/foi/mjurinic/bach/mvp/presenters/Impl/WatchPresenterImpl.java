package hr.foi.mjurinic.bach.mvp.presenters.Impl;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import hr.foi.mjurinic.bach.mvp.presenters.WatchPresenter;
import hr.foi.mjurinic.bach.mvp.views.WatchView;

public class WatchPresenterImpl implements WatchPresenter {

    private static final String TAG = "WatchPresenterImpl";

    private WatchView watchView;
    private List<WifiP2pDevice> peers;

    @Inject
    public WatchPresenterImpl(WatchView watchView) {
        this.watchView = watchView;

        peers = new ArrayList<>();
    }

    @Override
    public void discoverWifiPeers(final WifiP2pManager manager, final WifiP2pManager.Channel channel) {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Yay?
            }

            @Override
            public void onFailure(int reason) {
                watchView.showError("[" + reason + "] Unable to list WiFi peers.");
            }
        });
    }
}
