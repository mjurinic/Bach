package hr.foi.mjurinic.bach.mvp.views;

import hr.foi.mjurinic.bach.network.protocol.ProtoStreamConfig;
import hr.foi.mjurinic.bach.network.protocol.ProtoStreamInfo;

public interface StreamView extends BaseView {

    void showCameraPreview(ProtoStreamConfig streamConfig);

    void handleFrame(byte[] frame);

    ProtoStreamInfo provideCameraInfo();
}
