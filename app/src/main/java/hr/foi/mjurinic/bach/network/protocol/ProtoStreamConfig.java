package hr.foi.mjurinic.bach.network.protocol;

import android.hardware.Camera;

public class ProtoStreamConfig extends ProtoMessage {

    private Camera.Size resolution;
    private int[] fpsRange;

    public ProtoStreamConfig(Camera.Size resolution, int[] fpsRange) {
        super.id = ProtoMessageType.STREAM_CONFIG_REQUEST;
        this.resolution = resolution;
        this.fpsRange = fpsRange;
    }

    public Camera.Size getResolution() {
        return resolution;
    }

    public int[] getFpsRange() {
        return fpsRange;
    }
}
