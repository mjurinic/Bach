package hr.foi.mjurinic.bach.mvp.interactors;

import hr.foi.mjurinic.bach.listeners.DataSentListener;
import hr.foi.mjurinic.bach.listeners.SocketListener;
import hr.foi.mjurinic.bach.network.MediaSocket;

public interface SocketInteractor {

    void startSender(MediaSocket socket);

    void startReceiver(MediaSocket socket, SocketListener callback);

    void send(Object obj, DataSentListener callback);

    void stopSender();

    void stopReceiver();
}
