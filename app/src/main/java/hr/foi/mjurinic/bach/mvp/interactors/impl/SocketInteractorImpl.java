package hr.foi.mjurinic.bach.mvp.interactors.impl;

import android.util.Pair;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import hr.foi.mjurinic.bach.listeners.DataSentListener;
import hr.foi.mjurinic.bach.listeners.SocketListener;
import hr.foi.mjurinic.bach.models.ReceivedPacket;
import hr.foi.mjurinic.bach.mvp.interactors.SocketInteractor;
import hr.foi.mjurinic.bach.network.MediaSocket;
import timber.log.Timber;

public class SocketInteractorImpl implements SocketInteractor {

    private Thread senderThread;
    private boolean isSenderActive;
    private Thread receiverThread;
    private boolean isReceiverActive;
    private Queue<Pair<Object, DataSentListener>> outboundQueue;

    @Override
    public void startSender(final MediaSocket socket) {
        outboundQueue = new LinkedBlockingDeque<>();

        senderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isSenderActive) {
                    while (isSenderActive && outboundQueue.isEmpty());

                    Timber.d("New outbound data.");

                    // In case thread has been interrupted and queue was not empty.
                    if (!outboundQueue.isEmpty()) {
                        Pair<Object, DataSentListener> currItem = outboundQueue.remove();

                        if (socket.send(currItem.first)) {
                            currItem.second.onSuccess();

                        } else {
                            currItem.second.onError();
                        }
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
    public void startReceiver(final MediaSocket socket, final SocketListener callback) {
        receiverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isReceiverActive) {
                    ReceivedPacket response = socket.receive();

                    if (response != null) {
                        callback.onSuccess(response);

                    } else {
                        callback.onError();
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
    public void send(Object obj, DataSentListener listener) {
        outboundQueue.add(new Pair(obj, listener));
    }

    @Override
    public void stopSender() {
        Timber.d("Stopping sender thread...");

        if (outboundQueue != null) {
            outboundQueue.clear();
        }

        isSenderActive = false;
    }

    @Override
    public void stopReceiver() {
        isReceiverActive = false;
    }
}
