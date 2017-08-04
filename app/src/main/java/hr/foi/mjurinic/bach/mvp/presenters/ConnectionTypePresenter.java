package hr.foi.mjurinic.bach.mvp.presenters;

import hr.foi.mjurinic.bach.models.WifiHostInformation;

public interface ConnectionTypePresenter extends BasePresenter {

    void initSocketLayer(WifiHostInformation hostInformation);

    void generateQrCode(WifiHostInformation wifiHostInformation);
}
