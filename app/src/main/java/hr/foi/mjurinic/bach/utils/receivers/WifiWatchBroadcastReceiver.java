package hr.foi.mjurinic.bach.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

import hr.foi.mjurinic.bach.mvp.presenters.WatchPresenter;
import timber.log.Timber;

public class WifiWatchBroadcastReceiver extends BroadcastReceiver {

    private WatchPresenter watchPresenter;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION: {
                if (watchPresenter != null) {
                    Timber.d(intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO).toString());
                }
            }


        }
    }

    public void setWatchPresenter(WatchPresenter watchPresenter) {
        this.watchPresenter = watchPresenter;
    }
}
