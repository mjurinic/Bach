package hr.foi.mjurinic.bach.state;

import hr.foi.mjurinic.bach.models.ReceivedPacket;
import hr.foi.mjurinic.bach.mvp.presenters.Impl.StreamPresenterImpl;
import hr.foi.mjurinic.bach.mvp.presenters.StreamPresenter;
import hr.foi.mjurinic.bach.mvp.presenters.WatchPresenter;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessage;
import hr.foi.mjurinic.bach.network.protocol.ProtoMessageType;
import timber.log.Timber;

public class StreamInfoState implements State {

    @Override
    public void process(ReceivedPacket data, Object presenter) {
        ProtoMessage payload = (ProtoMessage) data.getPayload();

        switch (payload.getId()) {
            case ProtoMessageType.STREAM_INFO_REQUEST: {
                if (presenter instanceof StreamPresenter) {
                    StreamPresenterImpl streamPresenter = (StreamPresenterImpl) presenter;


                }

                break;
            }

            case ProtoMessageType.STREAM_INFO_RESPONSE: {
                if (presenter instanceof WatchPresenter) {

                }

                break;
            }

            default:
                Timber.d("Message (" + payload.getId() + ") not supported in current state.");
                break;
        }
    }
}
