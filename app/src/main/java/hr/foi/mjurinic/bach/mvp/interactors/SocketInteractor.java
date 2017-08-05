package hr.foi.mjurinic.bach.mvp.interactors;

import hr.foi.mjurinic.bach.listeners.DatagramSentListener;
import hr.foi.mjurinic.bach.listeners.SocketListener;
import hr.foi.mjurinic.bach.network.MediaSocket;

public interface SocketInteractor {

    void startSender(MediaSocket socket);

    void startReceiver(MediaSocket socket, SocketListener callback);

    void send(Object obj, DatagramSentListener callback);

    void stopSender();

    void stopReceiver();

    void updateReceiverCallback(SocketListener callback);
}
