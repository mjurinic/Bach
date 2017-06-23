package hr.foi.mjurinic.bach.mvp.presenters;

import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.views.WatchView;

public interface WatchPresenter {

    void connectToWifiHost(WifiHostInformation hostInformation);

    void disconnectWifi();

    void updateView(WatchView view);
}
