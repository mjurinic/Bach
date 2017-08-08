package hr.foi.mjurinic.bach.mvp.presenters.watch;

import hr.foi.mjurinic.bach.listeners.DatagramSentListener;
import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.views.WatchView;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;

public interface WatchPresenter {

    void connectToWifiHost(WifiHostInformation hostInformation);

    void initMediaTransport(WifiHostInformation hostInformation);

    void closeOpenConnections();

    void updateView(WatchView view);

    void sendData(ProtoMessage data, DatagramSentListener listener);
}
