package hr.foi.mjurinic.bach.mvp.presenters;

import hr.foi.mjurinic.bach.listeners.DataSentListener;
import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.views.WatchView;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;

public interface WatchPresenter {

    void connectToWifiHost(WifiHostInformation hostInformation);

    void initMediaTransport();

    void closeOpenConnections();

    void updateView(WatchView view);

    void sendData(ProtoMessage data, DataSentListener listener);
}
