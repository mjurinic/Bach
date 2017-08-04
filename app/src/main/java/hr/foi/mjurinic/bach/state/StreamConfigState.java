package hr.foi.mjurinic.bach.state;

import java.util.Arrays;

import hr.foi.mjurinic.bach.models.CameraSize;
import hr.foi.mjurinic.bach.models.ReceivedPacket;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.StreamPresenterImpl;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.WatchPresenterImpl;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.presenters.WatchPresenter;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessageType;
import hr.foi.mjurinic.bach.network.protocol.ProtoStreamConfig;
import timber.log.Timber;

public class StreamConfigState implements State {

    private int retryCnt;

    @Override
    public void process(ReceivedPacket data, Object presenter) {
        ProtoMessage payload = (ProtoMessage) data.getPayload();

        switch (payload.getId()) {
            case ProtoMessageType.STREAM_CONFIG_REQUEST: {
                if (presenter instanceof StreamPresenter) {
                    Timber.d("StreamConfigRequest received.");

                    final StreamPresenterImpl streamPresenter = (StreamPresenterImpl) presenter;
                    boolean checkConfig = false;

                    ProtoStreamConfig streamConfigRequest = (ProtoStreamConfig) payload;
                    retryCnt = 0;

                    Timber.d("Requested resolution: " + streamConfigRequest.getResolution().getWidth() + "x" + streamConfigRequest
                            .getResolution().getHeight());

                    Timber.d("Requested FPS: " + streamConfigRequest.getFpsRange()[0] + " - " + streamConfigRequest.getFpsRange()[1]);

                    for (CameraSize resolution : streamPresenter.getStreamInfo().getResolutions()) {
                        if (resolution.equals(streamConfigRequest.getResolution())) {
                            for (int[] fpsRange : streamPresenter.getStreamInfo().getSupportedFpsRange()) {
                                Timber.d(fpsRange.toString() + " ][ " + streamConfigRequest.getFpsRange().toString());

                                if (Arrays.equals(fpsRange, streamConfigRequest.getFpsRange())) {
                                    checkConfig = true;
                                    break;
                                }
                            }

                            break;
                        }
                    }

                    final boolean configOk = checkConfig;
                    final ProtoMessage response = new ProtoMessage(
                            configOk ? ProtoMessageType.STREAM_CONFIG_RESPONSE_OK :
                                    ProtoMessageType.STREAM_CONFIG_RESPONSE_ERROR
                    );

//                    streamPresenter.sendData(response, new DatagramSentListener() {
//                        @Override
//                        public void onSuccess() {
//                            if (configOk) {
//                                Timber.d("StreamConfigResponseOk sent. Next state: StreamState");
//                                // TODO change to StreamState
//
//                            } else {
//                                Timber.d("StreamConfigResponseError sent. Waiting for client correction...");
//                            }
//                        }
//
//                        @Override
//                        public void onError() {
//                            if (retryCnt == 5) {
//                                Timber.d("Client unreachable.");
//                                return;
//                            }
//
//                            try {
//                                Timber.d("StreamConfigResponse failed. Retrying in 2 seconds.");
//
//                                Thread.sleep(2000);
//                                retryCnt += 1;
//
//                                streamPresenter.sendData(response, this);
//
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                }

                break;
            }

            case ProtoMessageType.STREAM_CONFIG_RESPONSE_OK: {
                if (presenter instanceof WatchPresenter) {
                    Timber.d("StreamConfigResponseOk received.");

                    final WatchPresenterImpl watchPresenter = (WatchPresenterImpl) presenter;

                    // Here you can do modifications to your view
                    // based on stream configuration parameters

                    // TODO change to StreamState
                }

                break;
            }

            case ProtoMessageType.STREAM_CONFIG_RESPONSE_ERROR: {
                if (presenter instanceof WatchPresenter) {
                    Timber.d("StreamConfigRequestError received.");

                    final WatchPresenterImpl watchPresenter = (WatchPresenterImpl) presenter;

                    // TODO how to choose next configuration?
                }

                break;
            }

            default:
                Timber.d("Message (" + payload.getId() + ") not supported in current state.");
                break;
        }
    }
}
