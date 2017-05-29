package hr.foi.mjurinic.bach.mvp.presenters;

import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.views.StreamView;

public interface StreamPresenter {

    void createWifiP2PGroup();

    void removeWifiP2PGroup();

    void generateQrCode(WifiHostInformation wifiHostInformation);

    void updateView(StreamView view);
}
