package hr.foi.mjurinic.bach.mvp.presenters.Impl;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import javax.inject.Inject;

import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.interactors.SocketInteractor;
import hr.foi.mjurinic.bach.mvp.presenters.WatchPresenter;
import hr.foi.mjurinic.bach.mvp.views.WatchView;
import timber.log.Timber;

public class WatchPresenterImpl implements WatchPresenter {

    private WatchView watchView;
    private SocketInteractor socketInteractor;
    private Context context;
    private WifiManager wifiManager;
    private int netId;

    @Inject
    public WatchPresenterImpl(WatchView watchView, SocketInteractor socketInteractor, Context context) {
        this.watchView = watchView;
        this.socketInteractor = socketInteractor;
        this.context = context;

        Timber.d("WatchPresenterImpl created.");
    }

    @Override
    public void connectToWifiHost(WifiHostInformation hostInformation) {
        wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = String.format("\"%s\"", hostInformation.getNetworkName());
        wifiConfiguration.preSharedKey = String.format("\"%s\"", hostInformation.getPassphrase());

        watchView.updateProgressText("Connecting to: " + hostInformation.getNetworkName() + "...");

        netId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.enableNetwork(netId, false);
        wifiManager.reconnect();

        watchView.updateProgressText("Success!");

        // TODO add wifi_change_status thingy and read device ip there if needed
        Timber.d("Successfully connected to: " + hostInformation.getNetworkName());
        Timber.d("Device IP: " + Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        Timber.d("Link speed: " + wifiManager.getConnectionInfo().getLinkSpeed());
    }

    @Override
    public void disconnectWifi() {
        if (wifiManager != null) {
            wifiManager.disconnect();
        }
    }

    @Override
    public void updateView(WatchView view) {
        watchView = view;
    }
}
