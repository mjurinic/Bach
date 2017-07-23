package hr.foi.mjurinic.bach.network.protocol;

import java.util.List;

import hr.foi.mjurinic.bach.models.CameraSize;

public class ProtoStreamInfo extends ProtoMessage {

    private List<CameraSize> resolutions;
    private List<int[]> supportedFpsRange;

    public ProtoStreamInfo(List<CameraSize> resolutions, List<int[]> supportedFpsRange) {
        super.id = ProtoMessageType.STREAM_INFO_RESPONSE;
        this.resolutions = resolutions;
        this.supportedFpsRange = supportedFpsRange;
    }

    public List<CameraSize> getResolutions() {
        return resolutions;
    }

    public List<int[]> getSupportedFpsRange() {
        return supportedFpsRange;
    }
}
