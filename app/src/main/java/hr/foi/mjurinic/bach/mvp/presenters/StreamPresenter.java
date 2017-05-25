package hr.foi.mjurinic.bach.mvp.presenters;

import android.net.wifi.p2p.WifiP2pManager;

public interface StreamPresenter {

    void createWifiP2PGroup(WifiP2pManager manager, WifiP2pManager.Channel channel);
}
