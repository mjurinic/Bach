package hr.foi.mjurinic.bach.mvp.presenters.watch.impl;

import android.util.Base64;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.inject.Inject;

import hr.foi.mjurinic.bach.listeners.DatagramSentListener;
import hr.foi.mjurinic.bach.listeners.SocketListener;
import hr.foi.mjurinic.bach.models.CameraSize;
import hr.foi.mjurinic.bach.models.ReceivedPacket;
import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.interactors.SocketInteractor;
import hr.foi.mjurinic.bach.mvp.presenters.watch.QrScannerPresenter;
import hr.foi.mjurinic.bach.mvp.views.QrScannerView;
import hr.foi.mjurinic.bach.network.MediaSocket;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessageType;
import hr.foi.mjurinic.bach.network.protocol.ProtoStreamConfig;
import hr.foi.mjurinic.bach.network.protocol.ProtoStreamInfo;
import hr.foi.mjurinic.bach.utils.state.State;
import timber.log.Timber;

public class QrScannerPresenterImpl implements QrScannerPresenter, SocketListener {

    private SocketInteractor socketInteractor;
    private QrScannerView view;
    private ProtoStreamInfo streamInfo;
    private int lastConfigIndex;
    private String currState = State.HELLO_STATE;
    private ProtoStreamInfo.CameraInfo cameraInfo;

    @Inject
    public QrScannerPresenterImpl(SocketInteractor socketInteractor, QrScannerView view) {
        this.socketInteractor = socketInteractor;
        this.view = view;
    }

    @Override
    public void initSocketLayer(WifiHostInformation hostInformation) {
        Timber.d("Initializing media transport socket.");
        updateProgressText("Initializing media transport socket...");

        MediaSocket mediaSocket = new MediaSocket();
        mediaSocket.setKey(Base64.decode(hostInformation.getB64AESkey(), Base64.NO_WRAP));

        try {
            mediaSocket.setDestinationIp(InetAddress.getByName(hostInformation.getDeviceIpAddress()));
            mediaSocket.setDestinationPort(hostInformation.getDevicePortAsInt());

        } catch (UnknownHostException e) {
            e.printStackTrace();
            view.updateProgressText("Whoops! Couldn't initialize socket layer.");

            return;
        }

        socketInteractor.startSender(mediaSocket);
        socketInteractor.startReceiver(mediaSocket, this);

        helloState();

        updateProgressText("Pinging host...");
    }

    @Override
    public void closeSockets() {
        currState = State.HELLO_STATE;
        socketInteractor.stopSender();
        socketInteractor.stopReceiver();
    }

    @Override
    public void handleDatagram(ReceivedPacket data) {
        if (data.getPayload() instanceof ProtoMessage) {
            ProtoMessage message = (ProtoMessage) data.getPayload();

            Timber.d("Current state: " + currState);
            Timber.d("Received message id: " + message.getId());

            switch (message.getId()) {
                case ProtoMessageType.HELLO_RESPONSE:
                    if (currState.equals(State.HELLO_STATE)) {
                        Timber.d("Received HelloResponse!");

                        try {
                            // Waiting for client to switch fragments lol (hacky hacky)
                            Thread.sleep(2000);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        streamInfoState();
                    }
                    break;

                case ProtoMessageType.STREAM_INFO_RESPONSE:
                    if (currState.equals(State.STREAM_INFO_STATE)) {
                        Timber.d("Received StreamInfoResponse!");
                        handleStreamInfoResponse((ProtoStreamInfo) message);
                    }
                    break;

                case ProtoMessageType.STREAM_CONFIG_RESPONSE_OK:
                    if (currState.equals(State.STREAM_CONFIG_STATE)) {
                        Timber.d("Received StreamConfigResponse: OK!");
                        view.nextFragment();
                    }
                    break;

                case ProtoMessageType.STREAM_CONFIG_RESPONSE_ERROR:
                    if (currState.equals(State.STREAM_CONFIG_STATE)) {
                        Timber.d("Received StreamConfigResponse: Error!");
                        streamConfigState(streamInfo, ++lastConfigIndex);
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

    private void helloState() {
        Timber.d("[HELLO_STATE] Started.");
        updateProgressText("Handshake...");

        sendMessage(new ProtoMessage(ProtoMessageType.HELLO_REQUEST), new DatagramSentListener(this) {
            @Override
            public void onSuccess() {
                Timber.d("[HELLO_STATE] HelloRequest sent.");
            }
        });
    }

    private void streamInfoState() {
        Timber.d("[STREAM_INFO_STATE] Started.");
        updateProgressText("Gathering stream info...");

        currState = State.STREAM_INFO_STATE;

        sendMessage(new ProtoMessage(ProtoMessageType.STREAM_INFO_REQUEST), new DatagramSentListener(this) {
            @Override
            public void onSuccess() {
                Timber.d("[STREAM_INFO_STATE] StreamInfoRequest sent.");
            }
        });
    }

    private void handleStreamInfoResponse(ProtoStreamInfo streamInfo) {
        Timber.d("Host camera specs received!");
        Timber.d("Supported resolutions: ");

        updateProgressText("Configuring stream parameters...");

        this.streamInfo = streamInfo;
        cameraInfo = streamInfo.getFrontCameraInfo() != null ? streamInfo.getFrontCameraInfo() : streamInfo.getBackCameraInfo();

        Timber.d("Displaying " + (streamInfo.getFrontCameraInfo() != null ? "front camera info..." : "back camera info..."));

        for (CameraSize size : cameraInfo.getResolutions()) {
            Timber.d("\t[*] " + size.getWidth() + "x" + size.getHeight());
        }

        Timber.d("Fps range: ");

        for (int[] fps : cameraInfo.getSupportedFpsRange()) {
            Timber.d("\t[*] " + fps[0] + " - " + fps[1]);
        }

        streamConfigState(streamInfo, 0);
    }

    private void streamConfigState(ProtoStreamInfo streamInfo, int configPosition) {
        Timber.d("[STREAM_CONFIG_STATE] Started.");

        currState = State.STREAM_CONFIG_STATE;

        // Create a Stream configuration request to match current
        // network speed & device resolution. Sort different configuration
        // options by "niceness". In case first config request fails just choose
        // the next configuration from the list.

        if (configPosition == cameraInfo.getResolutions().size()) {
            updateProgressText("Whoops! Couldn't find a working stream configuration.");
            return;
        }

        ProtoMessage streamConfigRequest = new ProtoStreamConfig(
                cameraInfo.getResolutions().get(configPosition),
                cameraInfo.getSupportedFpsRange().get(0) // Always one possible value for FPS range?
        );

        sendMessage(streamConfigRequest, new DatagramSentListener(this) {
            @Override
            public void onSuccess() {
                Timber.d("[STREAM_CONFIG_STATE] StreamConfigRequest sent.");
            }
        });
    }
}
