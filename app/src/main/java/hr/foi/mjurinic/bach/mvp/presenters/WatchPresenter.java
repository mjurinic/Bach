package hr.foi.mjurinic.bach.mvp.presenters;

import android.net.wifi.p2p.WifiP2pManager;

public interface WatchPresenter {

    void discoverWifiPeers(WifiP2pManager manager, WifiP2pManager.Channel channel);
}
