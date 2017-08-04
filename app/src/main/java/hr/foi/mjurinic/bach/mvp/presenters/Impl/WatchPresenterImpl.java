package hr.foi.mjurinic.bach.mvp.presenters.Impl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.inject.Inject;

import hr.foi.mjurinic.bach.listeners.DatagramSentListener;
import hr.foi.mjurinic.bach.listeners.SocketListener;
import hr.foi.mjurinic.bach.models.ReceivedPacket;
import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.interactors.SocketInteractor;
import hr.foi.mjurinic.bach.mvp.presenters.WatchPresenter;
import hr.foi.mjurinic.bach.mvp.views.WatchView;
import hr.foi.mjurinic.bach.network.MediaSocket;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;
import hr.foi.mjurinic.bach.network.protocol.ProtoStreamConfig;
import hr.foi.mjurinic.bach.network.protocol.ProtoStreamInfo;
import hr.foi.mjurinic.bach.state.HelloState;
import hr.foi.mjurinic.bach.state.State;
import timber.log.Timber;

public class WatchPresenterImpl implements WatchPresenter, SocketListener {

    private WatchView watchView;
    private SocketInteractor socketInteractor;
    private Context context;
    private WifiManager wifiManager;
    private State state;

    private int netId;
    private int retryCnt;

    private ProtoStreamInfo streamInfo;
    private ProtoStreamConfig streamConfig;

    @Inject
    public WatchPresenterImpl(WatchView watchView, SocketInteractor socketInteractor, Context context) {
        this.watchView = watchView;
        this.socketInteractor = socketInteractor;
        this.context = context;

        Timber.d("WatchPresenterImpl created.");
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
        }

        return false;
    }

    @Override
    public void connectToWifiHost(final WifiHostInformation hostInformation) {
        //BachApp.getInstance().registerWifiWatchBroadcastReceiver();
        //BachApp.getInstance().getWatchBroadcastReceiver().setWatchPresenter(this);

        wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        wifiManager.disconnect(); // Disconnect from current network

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = String.format("\"%s\"", hostInformation.getNetworkName());
        wifiConfiguration.preSharedKey = String.format("\"%s\"", hostInformation.getPassphrase());

        // watchView.updateProgressText("Connecting to: " + hostInformation.getNetworkName() + "...");

        netId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.enableNetwork(netId, false);
        wifiManager.reconnect();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isConnected()) ;

                initMediaTransport(hostInformation);

                Timber.d("Successfully connected to: " + hostInformation.getNetworkName());
                Timber.d("Device IP: " + Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
                Timber.d("Link speed: " + wifiManager.getConnectionInfo().getLinkSpeed());
            }
        }).start();
    }

    @Override
    public void initMediaTransport(WifiHostInformation hostInformation) {
        Timber.d("Initializing media transport socket.");

        // watchView.updateProgressText("Initializing media transport socket...");

        MediaSocket mediaSocket = new MediaSocket();

        try {
            mediaSocket.setDestinationIp(InetAddress.getByName(hostInformation.getDeviceIpAddress()));
            mediaSocket.setDestinationPort(hostInformation.getDevicePortAsInt());

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }

        socketInteractor.startSender(mediaSocket);
        socketInteractor.startReceiver(mediaSocket, this);

        setState(new HelloState());

//        socketInteractor.send(new ProtoMessage(ProtoMessageType.HELLO_REQUEST), new DatagramSentListener() {
//            @Override
//            public void onSuccess() {
//                Timber.d("HelloRequest sent.");
//            }
//
//            @Override
//            public void onError() {
//                if (retryCnt == 5) {
//                    // watchView.updateProgressText("Host. unreachable. Closing...");
//                    Timber.d("Host unreachable. Closing...");
//
//                    return;
//                }
//
//                try {
//                    Timber.d("HelloRequest failed. Retrying in 2 seconds.");
//
//                    Thread.sleep(2000);
//                    retryCnt += 1;
//
//                    socketInteractor.send(new ProtoMessage(ProtoMessageType.HELLO_REQUEST), this);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        // watchView.updateProgressText("Pinging host...");
        Timber.d("Current state: 'Hello'.");
    }

    @Override
    public void closeOpenConnections() {
        socketInteractor.stopReceiver();
        socketInteractor.stopSender();

        if (wifiManager != null) {
            wifiManager.disconnect();
            wifiManager.removeNetwork(netId);
        }
    }

    @Override
    public void updateView(WatchView view) {
        watchView = view;
    }

    @Override
    public void sendData(ProtoMessage data, DatagramSentListener listener) {
        socketInteractor.send(data, listener);
    }

//    /**
//     * Socket callback.
//     *
//     * @param data Instance of ReceivedPacket.
//     */
//    @Override
//    public void onSuccess(ReceivedPacket data) {
//        if (state != null && data.getPayload() instanceof ProtoMessage) {
//            state.process(data, this);
//        }
//    }
//
//    /**
//     * Socket callback.
//     */
//    @Override
//    public void onError() {
//
//    }

    public void setState(State state) {
        this.state = state;
    }

    public ProtoStreamInfo getStreamInfo() {
        return streamInfo;
    }

    public void setStreamInfo(ProtoStreamInfo streamInfo) {
        this.streamInfo = streamInfo;
    }

    public ProtoStreamConfig getStreamConfig() {
        return streamConfig;
    }

    public void setStreamConfig(ProtoStreamConfig streamConfig) {
        this.streamConfig = streamConfig;
    }

    @Override
    public void handleDatagram(ReceivedPacket data) {

    }
}
