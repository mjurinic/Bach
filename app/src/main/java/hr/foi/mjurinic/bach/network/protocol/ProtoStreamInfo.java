package hr.foi.mjurinic.bach.network.protocol;

import android.hardware.Camera;

import java.util.List;

public class ProtoStreamInfo extends ProtoMessage {

    private List<Camera.Size> resolutions;
    private List<int[]> supportedFpsRange;

    public ProtoStreamInfo(List<Camera.Size> resolutions, List<int[]> supportedFpsRange) {
        super.id = ProtoMessageType.STREAM_INFO_RESPONSE;
        this.resolutions = resolutions;
        this.supportedFpsRange = supportedFpsRange;
    }

    public List<Camera.Size> getResolutions() {
        return resolutions;
    }

    public List<int[]> getSupportedFpsRange() {
        return supportedFpsRange;
    }
}
