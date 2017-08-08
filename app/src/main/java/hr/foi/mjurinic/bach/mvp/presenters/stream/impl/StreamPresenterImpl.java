package hr.foi.mjurinic.bach.mvp.presenters.stream.impl;

import java.util.Arrays;

import hr.foi.mjurinic.bach.listeners.DatagramSentListener;
import hr.foi.mjurinic.bach.listeners.SocketListener;
import hr.foi.mjurinic.bach.models.CameraSize;
import hr.foi.mjurinic.bach.models.ReceivedPacket;
import hr.foi.mjurinic.bach.mvp.interactors.SocketInteractor;
import hr.foi.mjurinic.bach.mvp.presenters.stream.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.views.StreamView;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessageType;
import hr.foi.mjurinic.bach.network.protocol.ProtoStreamConfig;
import hr.foi.mjurinic.bach.network.protocol.ProtoStreamInfo;
import hr.foi.mjurinic.bach.utils.state.State;
import timber.log.Timber;

public class StreamPresenterImpl implements StreamPresenter, SocketListener {

    private StreamView view;
    private SocketInteractor socketInteractor;
    private ProtoStreamInfo streamInfo;
    private String currState = State.STREAM_INFO_STATE;

    public StreamPresenterImpl(StreamView view, SocketInteractor socketInteractor) {
        this.view = view;
        this.socketInteractor = socketInteractor;

        updateSocketCallback();
    }

    @Override
    public void handleDatagram(ReceivedPacket data) {
        if (data.getPayload() instanceof ProtoMessage) {
            ProtoMessage message = (ProtoMessage) data.getPayload();

            switch (message.getId()) {
                case ProtoMessageType.STREAM_INFO_REQUEST:
                    if (currState.equals(State.STREAM_INFO_STATE)) {
                        Timber.d("Received StreamInfoRequest!");
                        handleStreamInfoRequest();
                    }
                    break;

                case ProtoMessageType.STREAM_CONFIG_REQUEST:
                    if (currState.equals(State.STREAM_CONFIG_STATE)) {
                        Timber.d("Received StreamConfigRequest!");
                        handleStreamConfigRequest((ProtoStreamConfig) message);
                    }
                    break;

                case ProtoMessageType.CLIENT_READY:
                    if (currState.equals(State.STREAMING_STATE)) {
                        view.showCameraPreview();
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
        view.updateProgressText(progress);
    }

    private void handleStreamInfoRequest() {
        streamInfo = view.provideCameraInfo();

        sendMessage(streamInfo, new DatagramSentListener(this) {
            @Override
            public void onSuccess() {
                Timber.d("[STREAM_INFO_STATE] StreamInfoResponse sent.");
                Timber.d("Next state: STREAM_CONFIG_STATE.");

                currState = State.STREAM_CONFIG_STATE;
            }
        });
    }

    private void handleStreamConfigRequest(ProtoStreamConfig payload) {
        Timber.d("Requested resolution: " + payload.getResolution().getWidth() + "x" + payload.getResolution().getHeight());
        Timber.d("Requested FPS: " + payload.getFpsRange()[0] + " - " + payload.getFpsRange()[1]);

        boolean checkConfig = false;

        for (CameraSize resolution : streamInfo.getResolutions()) {
            if (resolution.equals(payload.getResolution())) {
                for (int[] fpsRange : streamInfo.getSupportedFpsRange()) {
                    Timber.d(fpsRange.toString() + " ][ " + payload.getFpsRange().toString());

                    if (Arrays.equals(fpsRange, payload.getFpsRange())) {
                        checkConfig = true;
                        break;
                    }
                }

                break;
            }
        }

        sendMessage(new ProtoMessage(checkConfig ? ProtoMessageType.STREAM_CONFIG_RESPONSE_OK : ProtoMessageType
                .STREAM_CONFIG_RESPONSE_ERROR), new DatagramSentListener(this) {
            @Override
            public void onSuccess() {
                Timber.d("[STREAM_CONFIG_STATE] StreamConfigResponse sent!");
                Timber.d("Next state: STREAMING_STATE.");

                currState = State.STREAMING_STATE;
            }
        });
    }

    private void updateSocketCallback() {
        Timber.d("Updating socket callback...");
        socketInteractor.updateReceiverCallback(this);
    }
}