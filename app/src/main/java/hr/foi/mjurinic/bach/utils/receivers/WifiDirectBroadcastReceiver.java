package hr.foi.mjurinic.bach.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import javax.inject.Inject;

import hr.foi.mjurinic.bach.activities.MainActivity;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.WatchPresenterImpl;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    @Inject
    WatchPresenterImpl watchPresenterImpl;

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity mainActivity;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity mainActivity) {
        super();

        this.manager = manager;
        this.channel = channel;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION: {
                String message = "";

                if (WifiP2pManager.WIFI_P2P_STATE_ENABLED == intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)) {
                    message = "Wi-Fi P2P enabled!";

                } else {
                    message = "Wi-Fi P2P NOT enabled.";
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                break;
            }

            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION: {
                System.out.println("Hello");
                manager.requestPeers(channel, watchPresenterImpl);

                break;
            }

            default:
                break;
        }
    }
}
