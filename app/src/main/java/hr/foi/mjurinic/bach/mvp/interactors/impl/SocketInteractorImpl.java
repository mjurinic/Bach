package hr.foi.mjurinic.bach.mvp.interactors.impl;

import java.util.LinkedList;
import java.util.Queue;

import hr.foi.mjurinic.bach.listeners.Listener;
import hr.foi.mjurinic.bach.mvp.interactors.SocketInteractor;
import hr.foi.mjurinic.bach.network.MediaSocket;
import timber.log.Timber;

public class SocketInteractorImpl implements SocketInteractor {

    private Thread senderThread;
    private boolean isSenderActive;
    private Thread receiverThread;
    private boolean isReceiverActive;
    private Queue<Object> outbandQueue;

    @Override
    public void startSender(final MediaSocket socket) {
        outbandQueue = new LinkedList<>();

        senderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isSenderActive) {
                    while (isSenderActive && outbandQueue.isEmpty());

                    // In case thread has been interrupted and queue was empty.
                    if (!outbandQueue.isEmpty()) {
                        socket.send(outbandQueue.remove());
                    }
                }

                Timber.d("Sender thread stopped.");
            }
        });

        isSenderActive = true;
        senderThread.start();

        Timber.d("Sender thread started.");
    }

    @Override
    public void startReceiver(final MediaSocket socket, final Listener<Object> callback) {
        receiverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isReceiverActive) {
                    Object response = socket.receive();

                    if (response != null) {
                        callback.onSuccess(response);

                    } else {
                        callback.onFailure();
                    }
                }

                Timber.d("Receiver thread stopped.");
            }
        });

        isReceiverActive = true;
        receiverThread.start();

        Timber.d("Receiver thread started.");
    }

    @Override
    public void send(Object obj) {
        outbandQueue.add(obj);
    }

    @Override
    public void stopSender() {
        Timber.d("Stopping sender thread...");

        if (outbandQueue != null) {
            outbandQueue.clear();
        }

        isSenderActive = false;
    }

    @Override
    public void stopReceiver() {
        isReceiverActive = false;
    }
}
