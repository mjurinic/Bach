package hr.foi.mjurinic.bach.mvp.views;

import android.graphics.Bitmap;

import hr.foi.mjurinic.bach.models.WifiHostInformation;

public interface ConnectionTypeView extends BaseView {

    void advertiseAccessPoint(WifiHostInformation hostInformation);

    void showQrCode(Bitmap qrCode);
}
