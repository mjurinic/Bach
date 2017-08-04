package hr.foi.mjurinic.bach.mvp.presenters;

import hr.foi.mjurinic.bach.listeners.DatagramSentListener;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;

public interface BasePresenter {

    void sendMessage(ProtoMessage message, DatagramSentListener callback);

    void updateProgressText(String progress);
}
