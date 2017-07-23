package hr.foi.mjurinic.bach.network.protocol;

import hr.foi.mjurinic.bach.models.CameraSize;

public class ProtoStreamConfig extends ProtoMessage {

    private CameraSize resolution;
    private int[] fpsRange;

    public ProtoStreamConfig(CameraSize resolution, int[] fpsRange) {
        super.id = ProtoMessageType.STREAM_CONFIG_REQUEST;
        this.resolution = resolution;
        this.fpsRange = fpsRange;
    }

    public CameraSize getResolution() {
        return resolution;
    }

    public int[] getFpsRange() {
        return fpsRange;
    }
}
