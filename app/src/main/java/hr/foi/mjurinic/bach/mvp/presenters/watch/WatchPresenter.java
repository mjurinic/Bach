package hr.foi.mjurinic.bach.mvp.presenters.watch;

import hr.foi.mjurinic.bach.mvp.presenters.BasePresenter;
import hr.foi.mjurinic.bach.network.protocol.ProtoMultimedia;

public interface WatchPresenter extends BasePresenter {

    void updateSocketCallback();

    void sendClientReady();

    void updateFrame(ProtoMultimedia multimedia);

    void closeStream();
}