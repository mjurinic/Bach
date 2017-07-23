package hr.foi.mjurinic.bach.mvp.presenters.Impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.support.v4.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Map;

import javax.inject.Inject;

import hr.foi.mjurinic.bach.BachApp;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.listeners.DataSentListener;
import hr.foi.mjurinic.bach.listeners.SocketListener;
import hr.foi.mjurinic.bach.models.ReceivedPacket;
import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.interactors.SocketInteractor;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.views.BaseStreamView;
import hr.foi.mjurinic.bach.network.MediaSocket;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;
import hr.foi.mjurinic.bach.network.protocol.ProtoStreamInfo;
import hr.foi.mjurinic.bach.state.HelloState;
import hr.foi.mjurinic.bach.state.State;
import timber.log.Timber;

public class StreamPresenterImpl implements StreamPresenter, SocketListener {

    private static final String SERVICE_TYPE = "_bach._udp.";

    @Inject
    SocketInteractor socketInteractor;

    private BaseStreamView streamView;
    private Context context;
    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel wifiChannel;
    private State state;
    private MediaSocket mediaSocket;
    private ProtoStreamInfo streamInfo;
    private boolean isIniting = true;

    @Inject
    public StreamPresenterImpl(Context context) {
        this.context = context;

        wifiManager = BachApp.getInstance().getManager();
        wifiChannel = BachApp.getInstance().getChannel();
    }

    @Override
    public void createWifiP2PGroup() {
        BachApp.getInstance().registerWifiDirectBroadcastReceiver();

        // Called to avoid Wi-Fi P2P "Busy" error.
        removeWifiP2PGroup();

        streamView.updateProgressText("Creating Wi-Fi P2P group...");

        wifiManager.createGroup(wifiChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Timber.d("Wi-Fi Peer-to-Peer group created.");
            }

            @Override
            public void onFailure(int reason) {
                streamView.showError("P2P group creation failed. Make sure Wi-Fi is on. (" + reason + ")");
            }
        });
    }

    @Override
    public void removeWifiP2PGroup() {
        wifiManager.removeGroup(wifiChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Timber.d("Removed Wi-Fi Peer-to-Peer group.");
            }

            @Override
            public void onFailure(int reason) {
                Timber.d("Failed to remove Wi-Fi Peer-to-Peer group: " + reason);
            }
        });
    }

    @Override
    public void generateQrCode(WifiHostInformation wifiHostInformation) {
        BitMatrix result;

        int width = (int) context.getResources().getDimension(R.dimen.qr_code_size);
        int height = (int) context.getResources().getDimension(R.dimen.qr_code_size);
        int white = ResourcesCompat.getColor(context.getResources(), android.R.color.white, null);
        int black = ResourcesCompat.getColor(context.getResources(), android.R.color.black, null);

        try {
            String payload = new Gson().toJson(wifiHostInformation);
            result = new MultiFormatWriter().encode(payload, BarcodeFormat.QR_CODE, width, height, null);

            Timber.d(payload);

            int w = result.getWidth();
            int h = result.getHeight();

            int[] pixels = new int[w * h];

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    pixels[y * w + x] = result.get(x, y) ? black : white;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, w, h);

            streamView.showQrCode(bitmap);

        } catch (IllegalArgumentException | WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeOpenConnections() {
        isIniting = true;

        if (socketInteractor != null) {
            socketInteractor.stopReceiver();
            socketInteractor.stopSender();
        }

        wifiManager.removeGroup(wifiChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Timber.d("Disconnected from Wi-Fi P2P.");
            }

            @Override
            public void onFailure(int reason) {
                Timber.d("Error disconnecting Wi-Fi P2P.");
            }
        });
    }

    @Override
    public void updateView(BaseStreamView view) {
        streamView = view;
    }

    @Override
    public void sendData(ProtoMessage data, DataSentListener listener) {
        socketInteractor.send(data, listener);
    }

    @Override
    public void nextFragment() {
        streamView.nextFragment();
    }

    /**
     * Advertise newly created access point.
     */
    @Override
    public void initLocalService(final WifiHostInformation wifiHostInformation) {
        Gson gson = new Gson();
        Map<String, String> extraInfo = gson.fromJson(gson.toJson(wifiHostInformation), Map.class);

        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(wifiHostInformation.getNetworkName(), SERVICE_TYPE, extraInfo);

        streamView.updateProgressText("Initializing local service...");

        wifiManager.addLocalService(wifiChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Timber.d("New local service created.");
                generateQrCode(wifiHostInformation);
            }

            @Override
            public void onFailure(int reason) {
                Timber.d("Failed to register new local service. (" + reason + ")");
            }
        });
    }

    @Override
    public void initMediaTransport(WifiHostInformation wifiHostInformation) {
        Timber.d("Initializing media transport socket.");

        isIniting = false;

        streamView.updateProgressText("Initializing media transport socket...");

        mediaSocket = new MediaSocket();
        socketInteractor.startReceiver(mediaSocket, this);

        setState(new HelloState());

        streamView.updateProgressText("Waiting for clients...");
        Timber.d("Current state: 'Hello'.");

        wifiHostInformation.setDevicePort(String.valueOf(mediaSocket.getPort()));

        initLocalService(wifiHostInformation);
    }

    /**
     * Socket callback.
     *
     * @param data Instance of ReceivedPacket.
     */
    @Override
    public void onSuccess(ReceivedPacket data) {
        Timber.d("Received new packet.");

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

    public void setState(State state) {
        this.state = state;
    }

    public MediaSocket getMediaSocket() {
        return mediaSocket;
    }

    public BaseStreamView getCurrentView() {
        return streamView;
    }

    public ProtoStreamInfo getStreamInfo() {
        return streamInfo;
    }

    public void setStreamInfo(ProtoStreamInfo streamInfo) {
        this.streamInfo = streamInfo;
    }

    public SocketInteractor getSocketInteractor() {
        return socketInteractor;
    }

    public boolean isIniting() {
        return isIniting;
    }
}
