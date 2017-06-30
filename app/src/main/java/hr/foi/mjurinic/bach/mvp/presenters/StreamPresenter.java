package hr.foi.mjurinic.bach.mvp.presenters;

import hr.foi.mjurinic.bach.listeners.DataSentListener;
import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.views.StreamView;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;

public interface StreamPresenter {

    void createWifiP2PGroup();

    void removeWifiP2PGroup();

    void initLocalService(WifiHostInformation wifiHostInformation);

    void initMediaTransport(WifiHostInformation wifiHostInformation);

    void generateQrCode(WifiHostInformation wifiHostInformation);

    void closeOpenConnections();

    void updateView(StreamView view);

    void sendData(ProtoMessage data, DataSentListener listener);
}
