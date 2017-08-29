package hr.foi.mjurinic.bach.mvp.presenters.stream;

import hr.foi.mjurinic.bach.mvp.presenters.BasePresenter;

public interface StreamPresenter extends BasePresenter {

    void closeStream();

    void closeSockets();
}
