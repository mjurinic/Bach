package hr.foi.mjurinic.bach.mvp.presenters.watch;

import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.presenters.BasePresenter;

public interface QrScannerPresenter extends BasePresenter {

    void initSocketLayer(WifiHostInformation hostInformation);

    void closeSockets();
}
