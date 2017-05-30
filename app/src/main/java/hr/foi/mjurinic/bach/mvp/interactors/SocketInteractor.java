package hr.foi.mjurinic.bach.mvp.interactors;

import hr.foi.mjurinic.bach.listeners.Listener;
import hr.foi.mjurinic.bach.network.MediaSocket;

public interface SocketInteractor {

    void startSender(MediaSocket socket);

    void startReceiver(MediaSocket socket, Listener<Object> callback);

    void send(Object obj);

    void stopSender();

    void stopReceiver();
}
