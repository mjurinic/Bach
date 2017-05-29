package hr.foi.mjurinic.bach.mvp.presenters;

import hr.foi.mjurinic.bach.models.WifiHostInformation;

public interface StreamPresenter {

    void initWifiDirect();

    void createWifiP2PGroup();

    void removeWifiP2PGroup();

    void generateQrCode(WifiHostInformation wifiHostInformation);
}
