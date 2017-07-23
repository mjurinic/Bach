package hr.foi.mjurinic.bach.state;

import android.hardware.Camera;

import java.util.ArrayList;
import java.util.List;

import hr.foi.mjurinic.bach.fragments.stream.StreamFragment;
import hr.foi.mjurinic.bach.listeners.DataSentListener;
import hr.foi.mjurinic.bach.models.CameraSize;
import hr.foi.mjurinic.bach.models.ReceivedPacket;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.StreamPresenterImpl;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.WatchPresenterImpl;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.presenters.WatchPresenter;
import hr.foi.mjurinic.bach.mvp.views.StreamView;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessageType;
import hr.foi.mjurinic.bach.network.protocol.ProtoStreamConfig;
import hr.foi.mjurinic.bach.network.protocol.ProtoStreamInfo;
import timber.log.Timber;

public class StreamInfoState implements State {

    private int retryCnt;

    @Override
    public void process(ReceivedPacket data, Object presenter) {
        ProtoMessage payload = (ProtoMessage) data.getPayload();

        switch (payload.getId()) {
            case ProtoMessageType.STREAM_INFO_REQUEST: {
                if (presenter instanceof StreamPresenter) {
                    Timber.d("Received StreamInfoRequest.");

                    final StreamPresenterImpl streamPresenter = (StreamPresenterImpl) presenter;

                    Timber.d("[DEBUG] " + streamPresenter.getCurrentView().toString());

                    if (streamPresenter.getCurrentView() instanceof StreamFragment) {
                        StreamView streamView = (StreamView) streamPresenter.getCurrentView();
                        streamView.initCamera(0); // Back camera? TODO Init cameras beforehand.

                        List<CameraSize> resolutions = new ArrayList<>();

                        for (Camera.Size size : streamView.getCamera().getParameters().getSupportedPictureSizes()) {
                            resolutions.add(new CameraSize(size.width, size.height));
                        }

                        final ProtoMessage streamInfoResponse = new ProtoStreamInfo(
                                resolutions, streamView.getCamera().getParameters().getSupportedPreviewFpsRange()
                        );

                        streamPresenter.setStreamInfo((ProtoStreamInfo) streamInfoResponse);

                        streamPresenter.sendData(streamInfoResponse, new DataSentListener() {
                            @Override
                            public void onSuccess() {
                                Timber.d("StreamInfoResponse sent. Next state: StreamConfigState");
                                streamPresenter.setState(new StreamConfigState());
                            }

                            @Override
                            public void onError() {
                                if (retryCnt == 5) {
                                    Timber.d("Client unreachable.");
                                    return;
                                }

                                try {
                                    Timber.d("StreamInfoResponse failed. Retrying in 2 seconds.");

                                    Thread.sleep(2000);
                                    retryCnt += 1;

                                    streamPresenter.sendData(streamInfoResponse, this);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        Timber.d("Wrong view.");
                    }
                }

                break;
            }

            case ProtoMessageType.STREAM_INFO_RESPONSE: {
                if (presenter instanceof WatchPresenter) {
                    Timber.d("Received StreamInfoResponse.");

                    final WatchPresenterImpl watchPresenter = (WatchPresenterImpl) presenter;
                    ProtoStreamInfo streamInfo = (ProtoStreamInfo) payload;

                    Timber.d("Host camera specs received!");
                    Timber.d("Supported resolutions: ");

                    for (CameraSize size : streamInfo.getResolutions()) {
                        Timber.d("\t[*] " + size.getWidth() + "x" + size.getHeight());
                    }

                    Timber.d("Fps range: ");

                    for (int[] fps : streamInfo.getSupportedFpsRange()) {
                        Timber.d("\t[*] " + fps[0] + " - " + fps[1]);
                    }

                    watchPresenter.setStreamInfo(streamInfo);

                    // Create a Stream configuration request to match current
                    // network speed & device resolution. Sort different configuration
                    // options by "niceness". In case first config request fails just choose
                    // the next configuration from the list.

                    final ProtoMessage streamConfigRequest = new ProtoStreamConfig(
                            streamInfo.getResolutions().get(0),
                            streamInfo.getSupportedFpsRange().get(0)
                    );

                    watchPresenter.setStreamConfig((ProtoStreamConfig) streamConfigRequest);
                    watchPresenter.sendData(streamConfigRequest, new DataSentListener() {
                        @Override
                        public void onSuccess() {
                            Timber.d("StreamConfigRequest sent. Next state: StreamConfigState");
                            watchPresenter.setState(new StreamConfigState());
                        }

                        @Override
                        public void onError() {
                            if (retryCnt == 5) {
                                Timber.d("Client unreachable.");
                                return;
                            }

                            try {
                                Timber.d("StreamConfigRequest failed. Retrying in 2 seconds.");

                                Thread.sleep(2000);
                                retryCnt += 1;

                                watchPresenter.sendData(streamConfigRequest, this);

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
