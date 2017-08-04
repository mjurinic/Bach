package hr.foi.mjurinic.bach.mvp.presenters.Impl;

import android.graphics.Bitmap;
import android.support.v4.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import hr.foi.mjurinic.bach.BachApp;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.listeners.DatagramSentListener;
import hr.foi.mjurinic.bach.listeners.SocketListener;
import hr.foi.mjurinic.bach.models.ReceivedPacket;
import hr.foi.mjurinic.bach.models.WifiHostInformation;
import hr.foi.mjurinic.bach.mvp.interactors.SocketInteractor;
import hr.foi.mjurinic.bach.mvp.presenters.ConnectionTypePresenter;
import hr.foi.mjurinic.bach.mvp.views.ConnectionTypeView;
import hr.foi.mjurinic.bach.network.MediaSocket;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessageType;
import timber.log.Timber;


public class ConnectionTypePresenterImpl implements ConnectionTypePresenter, SocketListener {

    private SocketInteractor socketInteractor;
    private ConnectionTypeView view;
    private MediaSocket mediaSocket;

    public ConnectionTypePresenterImpl(SocketInteractor socketInteractor, ConnectionTypeView view) {
        this.socketInteractor = socketInteractor;
        this.view = view;
    }

    @Override
    public void initSocketLayer(WifiHostInformation hostInformation) {
        Timber.d("Initializing socket layer...");
        view.updateProgressText("Initializing media transport socket...");

        mediaSocket = new MediaSocket();
        socketInteractor.startReceiver(mediaSocket, this);

        hostInformation.setDevicePort(String.valueOf(mediaSocket.getPort()));
        view.advertiseAccessPoint(hostInformation);

        // TODO setState(new HelloState());

        Timber.d("Current state: 'Hello'.");
        view.updateProgressText("Waiting for clients...");
    }

    @Override
    public void generateQrCode(WifiHostInformation wifiHostInformation) {
        BitMatrix result;

        int width = (int) BachApp.getInstance().getApplicationContext().getResources().getDimension(R.dimen.qr_code_size);
        int height = (int) BachApp.getInstance().getApplicationContext().getResources().getDimension(R.dimen.qr_code_size);
        int white = ResourcesCompat.getColor(BachApp.getInstance().getApplicationContext().getResources(), android.R.color.white, null);
        int black = ResourcesCompat.getColor(BachApp.getInstance().getApplicationContext().getResources(), android.R.color.black, null);

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

            view.showQrCode(bitmap);

        } catch (IllegalArgumentException | WriterException e) {
            e.printStackTrace();
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

    @Override
    public void handleDatagram(ReceivedPacket packet) {
        if (packet.getPayload() instanceof ProtoMessage) {
            ProtoMessage message = (ProtoMessage) packet.getPayload();

            switch (message.getId()) {
                case ProtoMessageType.HELLO_REQUEST:
                    Timber.d("Received HelloRequest.");
                    handleHelloRequest(packet);
                    break;

                default:
                    break;
            }
        }
    }

    private void handleHelloRequest(ReceivedPacket packet) {
        mediaSocket.setDestinationIp(packet.getSenderIp());
        mediaSocket.setDestinationPort(packet.getSenderPort());
        socketInteractor.startSender(mediaSocket);

        sendMessage(new ProtoMessage(ProtoMessageType.HELLO_RESPONSE), new DatagramSentListener(this) {
            @Override
            public void onSuccess() {
                Timber.d("HelloResponse sent. New state: 'StreamInfoState'");
                // TODO switch to StreamInfoState and handle those messages
            }
        });
    }
}
