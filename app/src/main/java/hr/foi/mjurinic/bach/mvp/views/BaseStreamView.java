package hr.foi.mjurinic.bach.mvp.views;

import android.graphics.Bitmap;

public interface BaseStreamView extends BaseView {

    void showQrCode(Bitmap qrCode);

    void updateProgressText(String message);

    void nextFragment();
}
