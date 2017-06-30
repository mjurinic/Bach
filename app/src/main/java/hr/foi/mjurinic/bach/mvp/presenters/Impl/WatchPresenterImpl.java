package hr.foi.mjurinic.bach.mvp.presenters.Impl;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.inject.Inject;

import hr.foi.mjurinic.bach.listeners.DataSentListener;
import hr.foi.mjurinic.bach.listeners.SocketListener;
import hr.foi.mjurinic.bach.models.ReceivedPacket;
import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.interactors.SocketInteractor;
import hr.foi.mjurinic.bach.mvp.presenters.WatchPresenter;
import hr.foi.mjurinic.bach.mvp.views.WatchView;
import hr.foi.mjurinic.bach.network.MediaSocket;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessageType;
import hr.foi.mjurinic.bach.state.HelloState;
import hr.foi.mjurinic.bach.state.State;
import timber.log.Timber;

public class WatchPresenterImpl implements WatchPresenter, SocketListener {

    private WatchView watchView;
    private SocketInteractor socketInteractor;
    private Context context;
    private WifiManager wifiManager;
    private int netId;
    private State state;
    private int retryCnt;

    @Inject
    public WatchPresenterImpl(WatchView watchView, SocketInteractor socketInteractor, Context context) {
        this.watchView = watchView;
        this.socketInteractor = socketInteractor;
        this.context = context;

        Timber.d("WatchPresenterImpl created.");
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public void connectToWifiHost(WifiHostInformation hostInformation) {
        wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = String.format("\"%s\"", hostInformation.getNetworkName());
        wifiConfiguration.preSharedKey = String.format("\"%s\"", hostInformation.getPassphrase());

        watchView.updateProgressText("Connecting to: " + hostInformation.getNetworkName() + "...");

        netId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.enableNetwork(netId, false);
        wifiManager.reconnect();

        initMediaTransport(hostInformation);

        // TODO add wifi_change_status thingy and read device ip there if needed
        Timber.d("Successfully connected to: " + hostInformation.getNetworkName());
        Timber.d("Device IP: " + Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        Timber.d("Link speed: " + wifiManager.getConnectionInfo().getLinkSpeed());
    }

    @Override
    public void initMediaTransport(WifiHostInformation wifiHostInformation) {
        Timber.d("Initializing media transport socket.");

        watchView.updateProgressText("Initializing media transport socket...");

        MediaSocket mediaSocket = new MediaSocket();
        try {
            mediaSocket.setDestinationIp(InetAddress.getByName(wifiHostInformation.getDeviceIpAddress()));
            mediaSocket.setDestinationPort(wifiHostInformation.getDevicePortAsInt());

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }

        socketInteractor.startSender(mediaSocket);
        socketInteractor.startReceiver(mediaSocket, this);

        setState(new HelloState());

        socketInteractor.send(new ProtoMessage(ProtoMessageType.HELLO_REQUEST), new DataSentListener() {
            @Override
            public void onSuccess() {
                Timber.d("HelloRequest sent.");
            }

            @Override
            public void onError() {
                if (retryCnt == 5) {
                    watchView.updateProgressText("Host. unreachable. Closing...");
                    Timber.d("Host unreachable. Closing...");

                    return;
                }

                try {
                    Timber.d("HelloRequest failed. Retrying in 1 seconds.");

                    Thread.sleep(1000);
                    retryCnt += 1;

                    socketInteractor.send(new ProtoMessage(ProtoMessageType.HELLO_REQUEST), this);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        watchView.updateProgressText("Pinging host...");
        Timber.d("Current state: 'Hello'.");
    }

    @Override
    public void closeOpenConnections() {
        socketInteractor.stopReceiver();
        socketInteractor.stopSender();

        if (wifiManager != null) {
            wifiManager.disconnect();
        }
    }

    @Override
    public void updateView(WatchView view) {
        watchView = view;
    }

    @Override
    public void sendData(ProtoMessage data, DataSentListener listener) {
        socketInteractor.send(data, listener);
    }

    /**
     * Socket callback.
     *
     * @param data Instance of ReceivedPacket.
     */
    @Override
    public void onSuccess(ReceivedPacket data) {
        if (state != null && data.getPayload() instanceof ProtoMessage) {
            state.process(data, this);
        }
    }

    /**
     * Socket callback.
     */
    @Override
    public void onError() {

    }
}
