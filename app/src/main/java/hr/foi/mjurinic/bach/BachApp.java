package hr.foi.mjurinic.bach;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;

import hr.foi.mjurinic.bach.dagger.components.DaggerAppComponent;
import hr.foi.mjurinic.bach.utils.receivers.WifiDirectBroadcastReceiver;
import timber.log.Timber;

public class BachApp extends Application {

    private static BachApp instance;

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiDirectBroadcastReceiver receiver;
    private IntentFilter intentFilter;

    @Override
    public void onCreate() {
        super.onCreate();

        setInstance(this);
        DaggerAppComponent.create().inject(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        Timber.d("Registering intent filter.");

        // init WifiBroadcastReceiver
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(BachApp.getInstance().getApplicationContext(), getMainLooper(), null);
        receiver = new WifiDirectBroadcastReceiver(manager, channel);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
    }

    /**
     * Called later on by StreamFragment or WatchFragment
     */
    public void registerWifiDirectBroadcastReceiver() {
        registerReceiver(receiver, intentFilter);
    }

    public static BachApp getInstance() {
        return instance;
    }

    public static void setInstance(BachApp instance) {
        BachApp.instance = instance;
    }

    public WifiDirectBroadcastReceiver getWifiDirectBroadcastReceiver() {
        return receiver;
    }

    public WifiP2pManager getManager() {
        return manager;
    }

    public WifiP2pManager.Channel getChannel() {
        return channel;
    }
}
