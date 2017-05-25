package hr.foi.mjurinic.bach.mvp.presenters.Impl;

import android.net.wifi.p2p.WifiP2pManager;

import javax.inject.Inject;

import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.views.StreamView;

public class StreamPresenterImpl implements StreamPresenter {

    private StreamView streamView;

    @Inject
    public StreamPresenterImpl(StreamView streamView) {
        this.streamView = streamView;
    }

    @Override
    public void createWifiP2PGroup(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        manager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //
            }

            @Override
            public void onFailure(int reason) {
                streamView.showError("P2P group creation failed. Retry.");
            }
        });
    }
}
