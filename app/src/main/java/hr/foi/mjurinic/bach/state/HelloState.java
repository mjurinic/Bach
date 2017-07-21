package hr.foi.mjurinic.bach.state;

import hr.foi.mjurinic.bach.listeners.DataSentListener;
import hr.foi.mjurinic.bach.models.ReceivedPacket;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.StreamPresenterImpl;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.WatchPresenterImpl;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.presenters.WatchPresenter;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessageType;
import timber.log.Timber;

public class HelloState implements State {

    private int retryCnt = 0;

    @Override
    public void process(ReceivedPacket data, Object presenter) {
        ProtoMessage payload = (ProtoMessage) data.getPayload();

        switch (payload.getId()) {
            case ProtoMessageType.HELLO_REQUEST: {
                if (presenter instanceof StreamPresenter) {
                    final StreamPresenterImpl streamPresenter = (StreamPresenterImpl) presenter;
                    final ProtoMessage helloResponse = new ProtoMessage(ProtoMessageType.HELLO_RESPONSE);

                    Timber.d("Received HelloRequest.");
                    Timber.d("[HELLO_STATE] Sender IP: " + data.getSenderIp().getHostAddress());
                    Timber.d("[HELLO_STATE] Sender port: " + data.getSenderPort());

                    streamPresenter.getMediaSocket().setDestinationIp(data.getSenderIp());
                    streamPresenter.getMediaSocket().setDestinationPort(data.getSenderPort());

                    streamPresenter.sendData(helloResponse, new DataSentListener() {
                        @Override
                        public void onSuccess() {
                            streamPresenter.setState(new StreamInfoState());
                            streamPresenter.nextFragment();
                            Timber.d("HelloResponse sent. New state: 'StreamInfoState'");
                        }

                        @Override
                        public void onError() {
                            if (retryCnt == 5) {
                                Timber.d("Client unreachable.");
                                return;
                            }

                            try {
                                Timber.d("HelloResponse failed. Retrying in 2 seconds.");

                                Thread.sleep(2000);
                                retryCnt += 1;

                                streamPresenter.sendData(helloResponse, this);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                break;
            }

            case ProtoMessageType.HELLO_RESPONSE: {
                if (presenter instanceof WatchPresenter) {
                    final WatchPresenterImpl watchPresenter = (WatchPresenterImpl) presenter;
                    final ProtoMessage helloResponse = new ProtoMessage(ProtoMessageType.HELLO_RESPONSE);

                    Timber.d("Host responded. Transition to 'StreamInfoState'.");

                    watchPresenter.sendData(new ProtoMessage(ProtoMessageType.STREAM_INFO_REQUEST), new DataSentListener() {
                        @Override
                        public void onSuccess() {
                            watchPresenter.setState(new StreamInfoState());
                        }

                        @Override
                        public void onError() {
                            if (retryCnt == 5) {
                                Timber.d("Unable to contact host device.");
                                return;
                            }

                            try {
                                Timber.d("HelloResponse failed. Retrying in 2 seconds.");

                                Thread.sleep(2000);
                                retryCnt += 1;

                                watchPresenter.sendData(helloResponse, this);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                break;
            }

            default:
                Timber.d("Message (" + payload.getId() + ") not supported in current state.");
                break;
        }
    }
}