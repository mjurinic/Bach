package hr.foi.mjurinic.bach.mvp.presenters;

import hr.foi.mjurinic.bach.models.WifiHostInformation;

public interface QrScannerPresenter extends BasePresenter {

    void initSocketLayer(WifiHostInformation hostInformation);
}
