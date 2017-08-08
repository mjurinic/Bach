package hr.foi.mjurinic.bach.mvp.presenters.stream;

import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.presenters.BasePresenter;

public interface ConnectionTypePresenter extends BasePresenter {

    void initSocketLayer(WifiHostInformation hostInformation);

    void generateQrCode(WifiHostInformation wifiHostInformation);
}
