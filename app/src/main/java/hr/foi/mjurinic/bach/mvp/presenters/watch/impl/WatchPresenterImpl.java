package hr.foi.mjurinic.bach.mvp.presenters.watch.impl;

import hr.foi.mjurinic.bach.listeners.DatagramSentListener;
import hr.foi.mjurinic.bach.listeners.SocketListener;
import hr.foi.mjurinic.bach.models.ReceivedPacket;
import hr.foi.mjurinic.bach.mvp.interactors.SocketInteractor;
import hr.foi.mjurinic.bach.mvp.presenters.watch.WatchPresenter;
import hr.foi.mjurinic.bach.mvp.views.WatchView;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessageType;
import hr.foi.mjurinic.bach.network.protocol.ProtoMultimedia;
import hr.foi.mjurinic.bach.utils.state.State;
import timber.log.Timber;

public class WatchPresenterImpl implements WatchPresenter, SocketListener {

    private WatchView view;
    private SocketInteractor socketInteractor;
    private String currState = State.STREAM_CONFIG_STATE;

    public WatchPresenterImpl(WatchView view, SocketInteractor socketInteractor) {
        this.view = view;
        this.socketInteractor = socketInteractor;

        updateSocketCallback();
    }

    @Override
    public void handleDatagram(ReceivedPacket data) {
        if (data.getPayload() instanceof ProtoMessage) {
            ProtoMessage message = (ProtoMessage) data.getPayload();

            switch (message.getId()) {
                case ProtoMessageType.MULTIMEDIA:
                    if (currState.equals(State.STREAMING_STATE)) {
                        Timber.i("[STREAMING_STATE] Frame received!");
                        view.updateFrame((ProtoMultimedia) message);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void sendMessage(ProtoMessage message, DatagramSentListener callback) {
        socketInteractor.send(message, callback);
    }

    @Override
    public void updateProgressText(String progress) {
        // No progress text
    }

    @Override
    public void sendClientReady() {
        sendMessage(new ProtoMessage(ProtoMessageType.CLIENT_READY), new DatagramSentListener(this) {
            @Override
            public void onSuccess() {
                Timber.d("[STREAM_CONFIG_STATE] ClientReady sent.");
                currState = State.STREAMING_STATE;
            }
        });
    }

    @Override
    public void updateFrame(ProtoMultimedia multimedia) {
        view.updateFrame(multimedia);
    }

    private void updateSocketCallback() {
        Timber.d("Updating socket callback...");
        socketInteractor.updateReceiverCallback(this);
    }
}
