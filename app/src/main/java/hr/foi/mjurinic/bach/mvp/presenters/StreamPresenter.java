package hr.foi.mjurinic.bach.mvp.presenters;

public interface StreamPresenter {

    void initWifiDirect();

    void createWifiP2PGroup();

    void removeWifiP2PGroup();

    void generateQrCode();
}
