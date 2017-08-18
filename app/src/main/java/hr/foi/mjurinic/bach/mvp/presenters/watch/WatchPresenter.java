package hr.foi.mjurinic.bach.mvp.presenters.watch;

import android.graphics.Bitmap;

import hr.foi.mjurinic.bach.mvp.presenters.BasePresenter;

public interface WatchPresenter extends BasePresenter {

    void sendClientReady();

    void updateFrame(Bitmap frame);
}